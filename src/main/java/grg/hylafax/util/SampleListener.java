// SampleListener.java - gnu.hylafax implementation of the faxstat utility
// $Id: SampleListener.java,v 1.4 2007/02/21 00:07:49 sjardine Exp $
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
// TODO make this class more flexible
//  

package grg.hylafax.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import grg.hylafax.job.ReceiveEvent;
import grg.hylafax.job.ReceiveListener;
import grg.hylafax.job.SendEvent;
import grg.hylafax.job.SendListener;

/**
 * This class implements an example fax job listener.
 * 
 * @author $Author: sjardine $
 * @version $Id: SampleListener.java,v 1.4 2007/02/21 00:07:49 sjardine Exp $
 * @see grg.hylafax.job.SendNotifier
 * @see grg.hylafax.job.SendEvent
 * @see grg.hylafax.job.ReceiveNotifier
 * @see grg.hylafax.job.ReceiveEvent
 * @see grg.hylafax.util.Notifier
 */
public class SampleListener implements SendListener, ReceiveListener {
    
    private final static Log log = LogFactory.getLog(SampleListener.class);

	public final static String KEY_DBUSER = "notifier.db.user";

	public final static String KEY_DBPASSWORD = "notifier.db.password";

	public final static String KEY_DBDRIVER = "notifier.db.driver";

	public final static String KEY_DBURI = "notifier.db.uri";

	String DB_USER = System.getProperties().getProperty(KEY_DBUSER);

	String DB_PASSWORD = System.getProperties().getProperty(KEY_DBPASSWORD);

	String DB_URI = System.getProperties().getProperty(KEY_DBURI);

	String DB_CLASS = System.getProperties().getProperty(KEY_DBDRIVER);

	/**
	 * 
	 * 
	 */
	public void onSendEvent(SendEvent event) {
		try {

			Class.forName(DB_CLASS);
			Connection connection = DriverManager.getConnection(DB_URI,
					DB_USER, DB_PASSWORD);
			String sql = "insert into send (reason,filename,jobid,jobtime,next) values (?,?,?,?,?)";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, event.getReason());
			stmt.setString(2, event.getFilename());
			stmt.setLong(3, event.getJobId());
			stmt.setLong(4, event.getElapsedTime());
			stmt.setString(5, event.getNextAttempt());
			stmt.execute();
			stmt.close();
			connection.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}// onSendEvent

	/**
	 * This method is called when a fax-received event occurs.
	 */
	public void onReceiveEvent(ReceiveEvent event) {
		try {

			Class.forName(DB_CLASS);
			Connection connection = DriverManager.getConnection(DB_URI,
					DB_USER, DB_PASSWORD);
			String sql = "insert into receive (filename,modem,commid,message,cidnumber,cidname) values (?,?,?,?,?,?)";
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, event.getFilename());
			stmt.setString(2, event.getModem());
			stmt.setString(3, event.getCommunicationIdentifier());
			stmt.setString(4, event.getMessage());
			stmt.setString(5, event.getCidNumber());
			stmt.setString(6, event.getCidName());
			stmt.execute();
			stmt.close();
			connection.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}// onReceiveEvent

}// SampleListener
// SampleListener.java
