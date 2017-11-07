// Notifier.java - gnu.hylafax implementation of the faxstat utility
// $Id: Notifier.java,v 1.4 2007/05/07 18:26:53 sjardine Exp $
//
// - gives an example for getting the status callbacks on a FAX job
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import gnu.getopt.Getopt;
import grg.hylafax.job.ReceiveEvent;
import grg.hylafax.job.ReceiveListener;
import grg.hylafax.job.SendAndReceiveNotifier;
import grg.hylafax.job.SendEvent;
import grg.hylafax.job.SendListener;

/**
 * This class implements a callback program as supplied in the notify script
 * with the HylaFAX distribution. The following command line options are
 * supported.
 * <P>
 * 
 * <PRE>
 * 
 * -f <file> queue file name -r <reason> reason code -t <time> time spent on
 * this job -n <time> ETA of next attempt if <reason> is 'requeued' -c <class>
 * The Java class name of the gnu.hylafax.job.Listener to notify -j <jobid> The
 * HylaFAX JOBID
 * 
 * </PRE>
 * 
 * <P>
 * Refer to the notify man page (from the HylaFAX distribution) for more
 * information. This program depends on the gnu.getopt package for command line
 * parsing. gnu.getopt (java-getopt) can be found at <a
 * href="http://www.urbanophile.com/arenn/">http://www.urbanophile.com/arenn/
 * </a>
 * 
 * @author $Author: sjardine $
 * @version $Id: Notifier.java,v 1.4 2007/05/07 18:26:53 sjardine Exp $
 */
public class Notifier extends SendAndReceiveNotifier {
	public final static boolean OP_SEND = true;

	public final static boolean OP_RECEIVE = false;

	public final static String KEY_PROPERTIES = "notifier.properties";

	/**
	 * 
	 * 
	 */
	public static void main(String arguments[]) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			FileNotFoundException, IOException {
		String file = null;
		String reason = null;
		String time = null;
		String nextAttempt = null;
		Class klass = null;
		long jobid = -1;
		String commid = null;
		String message = null;
		String modem = null;
		String cidname = null;
		String cidnumber = null;
		boolean OP = OP_SEND;

		Getopt g = new Getopt("Notifier", arguments, "SRf:r:t:n:c:j:M:m:i:I:N");
		char opt;
		while ((short) (opt = (char) g.getopt()) != -1) {
			switch (opt) {
			case 'f':
				file = g.getOptarg();
				break;
			case 'r':
				reason = g.getOptarg();
				break;
			case 't':
				time = g.getOptarg();
				break;
			case 'n':
				nextAttempt = g.getOptarg();
				break;
			case 'c':
				klass = Class.forName(g.getOptarg());
				break;
			case 'j':
				jobid = Long.parseLong(g.getOptarg());
				break;
			case 'm':
				modem = g.getOptarg();
				break;
			case 'M':
				message = g.getOptarg();
				break;
			case 'i':
				commid = g.getOptarg();
				break;
			case 'N':
				cidname = g.getOptarg();
				break;
			case 'I':
				cidnumber = g.getOptarg();
				break;
			case 'R':
				OP = OP_RECEIVE;
				break;
			case 'S':
				OP = OP_SEND;
				break;
			case '?':
			default:
				// error
				usage(System.err);
				System.exit(-1);
			}
		}// while processing options

		// load properties
		//
		File props = new File(System.getProperty(KEY_PROPERTIES));
		if (props.exists() && props.canRead()) {
			Properties p = new Properties(System.getProperties());
			p.load(new FileInputStream(props));
			System.setProperties(p);
		}

		// notify listeners
		//
		if (OP == OP_SEND) {
			notifySendListeners(klass, jobid, file, reason, time, nextAttempt);
		} else {
			notifyReceiveListeners(klass, file, modem, commid, message,
					cidnumber, cidname);
		}
	}// main

	public static void notifySendListeners(Class klass, long jobid,
			String file, String reason, String time, String nextAttempt)
			throws InstantiationException, IllegalAccessException {
		// verify options
		//
		if ((file == null) || ("".equals(file))) {
			usage(System.err);
			System.exit(-1);
		}
		if ((reason == null) || ("".equals(reason))) {
			usage(System.err);
			System.exit(-1);
		}
		if ((time == null) || ("".equals(time))) {
			usage(System.err);
			System.exit(-1);
		}
		if (reason.equals(SendEvent.REASON_REQUEUED)
				&& ((nextAttempt == null) || ("".equals(nextAttempt)))) {
			usage(System.err);
			System.exit(-1);
		}
		if (klass == null) {
			usage(System.err);
			System.exit(-1);
		}
		if (jobid < 0) {
			usage(System.err);
			System.exit(-1);
		}

		// Notify Listeners
		//

		SendEvent event = new SendEvent();
		event.setReason(reason);
		event.setFilename(file);
		event.setElapsedTime(time);
		event.setNextAttempt(nextAttempt);
		event.setJobId(jobid);

		SendListener l = (SendListener) klass.newInstance();

		Notifier n = new Notifier();
		n.addSendListener(l);
		n.notifySendListeners(event);

	}// notifySendListeners

	public static void notifyReceiveListeners(Class klass, String file,
			String modem, String commid, String message, String cidnumber,
			String cidname) throws InstantiationException,
			IllegalAccessException {
		// verify options
		//
		if ((file == null) || ("".equals(file))) {
			usage(System.err);
			System.exit(-1);
		}
		if ((modem == null) || ("".equals(modem))) {
			usage(System.err);
			System.exit(-1);
		}
		if ((commid == null) || ("".equals(commid))) {
			usage(System.err);
			System.exit(-1);
		}
		if (klass == null) {
			usage(System.err);
			System.exit(-1);
		}

		// Notify Listeners
		//

		ReceiveEvent event = new ReceiveEvent();
		event.setFilename(file);
		event.setModem(modem);
		event.setCommunicationIdentifier(commid);
		event.setMessage(message);
		event.setCidNumber(cidnumber);
		event.setCidName(cidname);

		ReceiveListener l = (ReceiveListener) klass.newInstance();

		Notifier n = new Notifier();
		n.addReceiveListener(l);
		n.notifyReceiveListeners(event);

	}// notifyReceiveListeners

	/**
	 * prints program usage
	 */
	public static void usage(PrintStream out) {
		String msg = "usage: Notifier -f <qfile> -r <reason> -t <time> -c <id> [ -n <next-attempt> ]\n"
				+ "Notify listeners of Job state changes.\n\n"
				+ "Options:\n"
				+ "\t-f <file>\tQueue filename\n"
				+ "\t-r <reason>\tReason for state change, see notify(8C)\n"
				+ "\t-t <time>\tJob elapsed time\n"
				+ "\t-c <class>\tJava class name of Listener to notify\n"
				+ "\t-j <jobid>\tThe HylaFAX JOBID\n"
				+ "\t-n <next-attempt>\tETA for next attempt (if <reason> is 'requeued')\n\n";
		out.print(msg);
	}// usage

}// Notifier
// Notifier.java
