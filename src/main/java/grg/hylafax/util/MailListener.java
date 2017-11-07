// MailListener.java - emails job status updates to a user
// $Id: MailListener.java,v 1.5 2007/05/07 18:26:53 sjardine Exp $
//
// - basically gives an example for getting the status callbacks on a FAX job
//
// Copyright 2003, Joe Phillips <joe.phillips@innovationsw.com>
// Copyright 2003, Innovation Software Group LLC - http://www.innovationsw.com/
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the Free
// Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//
// for information on the HylaFAX FAX server see
//  http://www.hylafax.org/
//
//  

package grg.hylafax.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import grg.hylafax.job.ReceiveEvent;
import grg.hylafax.job.ReceiveListener;
import grg.hylafax.job.SendEvent;
import grg.hylafax.job.SendListener;

/**
 * This class implements an example fax job listener. It emails the status
 * updates to a given mailbox.
 * 
 * @author $Author: sjardine $
 * @version $Id: MailListener.java,v 1.5 2007/05/07 18:26:53 sjardine Exp $
 * @see grg.hylafax.job.SendNotifier
 * @see grg.hylafax.job.SendEvent
 * @see grg.hylafax.job.ReceiveNotifier
 * @see grg.hylafax.job.ReceiveEvent
 * @see grg.hylafax.util.Notifier
 */
public class MailListener implements SendListener, ReceiveListener {
    static final SimpleDateFormat rfc822df = new SimpleDateFormat(
	    "dd MMM yyyy HH:mm:ss z");
    static final String KEY_TO = "notifier.to";
    static final String KEY_FROM = "notifier.from";

    private Properties properties;

    private final static Log log = LogFactory.getLog(MailListener.class);

    // default constructor
    public MailListener() {
	properties = new Properties(System.getProperties());
    }// MailListener

    /**
     * 
     * 
     */
    public void onSendEvent(SendEvent event) {
	try {
	    // nothing for now.
	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}
    }// onSendEvent

    /**
     * This method is called when a fax-received event occurs. It composes an
     * email, attaching relevant files and mails it to a target mailbox.
     */
    public void onReceiveEvent(ReceiveEvent event) {
	String subject;
	String body;
	String cid = null;
	Date now = new Date();

	subject = "Facsimile received";
	if ((event.getCidName() != null) && (!"".equals(event.getCidName()))) {
	    cid = event.getCidName();
	} else if ((event.getCidNumber() != null)
		&& (!"".equals(event.getCidNumber()))) {
	    cid = event.getCidNumber();
	}

	try {
	    File f = new File(event.getFilename());
	    if (!f.exists()) {
		// failure? attach log file instead
		f = new File("log" + File.separator + "c"
			+ event.getCommunicationIdentifier());
		subject = "Facsimile failed";
		body = "A facsimile failed to be received at " + now + "\n\n"
			+ "See the attached log file for session details.\n";
	    } else {
		body = "The attached facsimile was received " + now + "\n";
	    }
	    if ((event.getMessage() != null)
		    && (!"".equals(event.getMessage()))) {
		body += "The server's message is:\n\n\t" + event.getMessage();
	    }
	    if (cid != null)
		subject += " from " + cid;

	    Session s = Session.getDefaultInstance(properties);
	    MimeMessage msg = new MimeMessage(s);
	    msg.addRecipients(Message.RecipientType.TO, properties
		    .getProperty(KEY_TO));
	    msg.setSubject(subject);
	    msg.addHeader("From", properties.getProperty(KEY_FROM));
	    msg.addHeader("Date", rfc822df.format(now));
	    msg
		    .addHeader("X-MailListener",
			    "$Id: MailListener.java,v 1.5 2007/05/07 18:26:53 sjardine Exp $");

	    // first body part
	    MimeBodyPart part0 = new MimeBodyPart();
	    part0.setText(body);

	    // second body part
	    FileDataSource fds = new FileDataSource(f);
	    fds.setFileTypeMap(new MimetypesFileTypeMap());
	    DataHandler fdh = new DataHandler(fds);
	    MimeBodyPart part1 = new MimeBodyPart();
	    part1.setDataHandler(fdh);
	    part1.setDisposition(Part.INLINE);
	    part1.setFileName(f.getName());
	    // build the message
	    MimeMultipart mp = new MimeMultipart();
	    mp.addBodyPart(part0);
	    mp.addBodyPart(part1);
	    msg.setContent(mp);

	    // send the message
	    Transport.send(msg);

	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	}
    }// onReceiveEvent

}// MailListener
// MailListener.java
