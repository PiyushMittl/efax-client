// FaxStat.java - gnu.hylafax implementation of the faxstat utility
// $Id: FaxStat.java,v 1.5 2007/05/07 18:26:53 sjardine Exp $
//
// - basically gives an example for getting the status a FAX job
//
// Copyright 2001, Joe Phillips <jaiger@net-foundry.com>
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


import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.HylaFAXClientProtocol;
import gnu.hylafax.Job;

/**
 * This class implements most of the faxstat program as supplied with the
 * HylaFAX distribution. The following command line options are supported.
 * <P>
 * 
 * <PRE>
 * 
 * -a get status of archive/ (IGNORED) -d get status of doneq/ -f get status of
 * docq/ -g display GMT timestamps -h<host>specifiy server hostname -l display
 * local timestamps -i get additional status info -r get status of receive queue
 * -s get status of send queue -u user to login to the server with -v verbose
 * mode
 * 
 * </PRE>
 * 
 * <P>
 * Refer to the faxstat man page (from the HylaFAX distribution) for more
 * information. This program depends on the gnu.getopt package for command line
 * parsing. gnu.getopt (java-getopt) can be found at <a
 * href="http://www.urbanophile.com/arenn/">http://www.urbanophile.com/arenn/
 * </a>
 */
public class FaxStat {

	public static void main(String arguments[]) {
		Vector list = new Vector();
		HylaFAXClient c = new HylaFAXClient();
		try {
			c.open("localhost");

			try {
				c.user("admin");
				// c.admin(PASSADMIN);
				c.tzone(HylaFAXClientProtocol.TZONE_LOCAL);
				for (int i = 1; i <= 2; i++) {
					list = c.getList(i == 1 ? "sendq" : "doneq");
					list.addAll(c.getList(""));
					Enumeration lines = list.elements();
					String line;
					long jobidL = -1;
					while (lines.hasMoreElements()) {
						try {
							line = (String) lines.nextElement();
							String jobid = new StringTokenizer(line)
									.nextToken();
							// --> First time returns "206...", next time
							// returns "[Job time limit..."
							jobidL = Long.parseLong(jobid);
							Job j = c.getJob(jobidL);
							String etatEnvoi = j.getProperty("STATE");
							// --> hylafax replied FAILED
							etatEnvoi = j.getProperty("STATE");
							// --> hylafax replied FAILED
							etatEnvoi = j.getProperty("STATE");
							// --> hylafax replied FAILED
							etatEnvoi = j.getProperty("STATE");
							// --> hylafax replied FAILED
							String status = j.getProperty("STATUS");
							// --> hylafax replied \ [Job time limit exceeded]\
							status = j.getProperty("STATUS");
							// --> hylafax replied \ [Job time limit exceeded]\
							status = j.getProperty("STATUS");
							// --> hylafax replied \ [Job time limit exceeded]\
							status = j.getProperty("STATUS");
							// --> hylafax replied \ [Job time limit exceeded]\
							// String IDtelecopie = j.getProperty("JOBINFO");
							// --> command jparm JOBINFO doesn't appear in the
							// log
							String sDateEnvoi = j.getProperty("SENDTIME");
							// --> hylafax replied 20050523133531
							sDateEnvoi = j.getProperty("SENDTIME");
							// --> hylafax replied 20050523133531
							sDateEnvoi = j.getProperty("SENDTIME");
							// --> hylafax replied 20050523133531
							sDateEnvoi = j.getProperty("SENDTIME");
							// --> hylafax replied 20050523133531
							sDateEnvoi = j.getProperty("SENDTIME");
							// --> hylafax replied 20050523133531
							int nbPages = Integer.parseInt(j
									.getProperty("TOTPAGES"));
							nbPages = Integer.parseInt(j
									.getProperty("TOTPAGES"));
							nbPages = Integer.parseInt(j
									.getProperty("TOTPAGES"));
							nbPages = Integer.parseInt(j
									.getProperty("TOTPAGES"));
							nbPages = Integer.parseInt(j
									.getProperty("TOTPAGES"));
							// --> command jparm TOTPAGES doesn't appear in the
							// log...
							String correspondant = j.getProperty("EXTERNAL");
							// --> hylafax replied 0475019884
							correspondant = j.getProperty("EXTERNAL");
							// --> hylafax replied 0475019884
							correspondant = j.getProperty("EXTERNAL");
							// --> hylafax replied 0475019884
							correspondant = j.getProperty("EXTERNAL");
							// --> hylafax replied 0475019884
							correspondant = j.getProperty("EXTERNAL");
							// --> hylafax replied 0475019884
							String initColl = j.getProperty("OWNER");
							// --> command jparm OWNER doesn't appear in the
							initColl = j.getProperty("OWNER");
							// --> command jparm OWNER doesn't appear in the
							initColl = j.getProperty("OWNER");
							// --> command jparm OWNER doesn't appear in the
							initColl = j.getProperty("OWNER");
							// --> command jparm OWNER doesn't appear in the
							initColl = j.getProperty("OWNER");
							// --> command jparm OWNER doesn't appear in the
							// log...
							// IDtelecopie = j.getProperty("JOBINFO"); // yes,
							// another
							// time...
							// --> hylafax replied 000000213082
							// --> exception
							// ...some statements...
							// ...write the status and message error in our
							// logs...

						} finally {
							if (i == 2) // if doneq, delete the job
								c.jdele(jobidL);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}// main
}// FaxStat

// FaxStat.java
