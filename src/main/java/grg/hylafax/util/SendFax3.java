// SendFax2.java - gnu.hylafax implementation of the sendfax utility
// $Id: SendFax2.java,v 1.3 2007/05/07 18:26:53 sjardine Exp $
//
// - basically gives an example for queuing a FAX job
//
// Copyright 2000, 2001, Joe Phillips <jaiger@innovationsw.com>
// Copyright 2001, Innovation Software Group, LLC - http://www.innovationsw.com
// Copyright 2006, John Yeary <jyeary@javanetwork.net>
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
// KNOWN ISSUES:
// - The password dialog is echoed to the screen, beware
// - not all symbolic pagesizes are supported yet (see Job.pagesizes)
// - can only queue a single job to the server per execution
//
// TODO make this class more flexible so it can be used by other programs rather than only called from the command line don't echo the password to the screen
//

package grg.hylafax.util;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnu.hylafax.ClientProtocol;
import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.Job;
import gnu.hylafax.Pagesize;

/**
 * This class implements most of the sendfax program as supplied with the
 * HylaFAX distribution. Not all options/features of the HylaFAX sendfax command
 * are supported.
 * <P>
 * <b>Specifically:</b>
 * <UL>
 * <LI>only one Job with one destination can be queued per execution
 * <LI>only HylaFAX <I>server</I> native file formats can be part of a job.
 * i.e. no client-side document conversions are performed
 * <LI>no built-in faxcover support
 * </UL>
 * The following command line options are supported.
 * <P>
 * 
 * <PRE>
 * 
 * -h &lt;host&gt; specifiy server hostname -u &lt;user&gt; user to login to the
 * server with -v set verbose mode -d &lt;number&gt; specify a destination FAX
 * <number> -f &lt;sender&gt; user &lt;sender&gt; as the identity of the FAX
 * sender -k &lt;time&gt; kill the job if it doesn't complete after the
 * indicated <time> -t &lt;tries&gt; make no more than &lt;tries&gt; attempts to
 * deliver the FAX -T &lt;dials&gt; maximum number of &lt;dials&gt; to attempt
 * for each job -D enable delivery notification -R enable delivery and retry
 * notification -N disable delivery and retry notification -P &lt;priority&gt;
 * assign the &lt;priority&gt; priority to the job (default: 127) -l use low
 * resolution (98 dpi) -m use medium resolution (196 dpi) -s &lt;size&gt;
 * specify the symbolic page &lt;size&gt; (legal, us-let, a3, a4, etc.)
 * 
 * </PRE>
 * 
 * <P>
 * Refer to the sendfax man page (from the HylaFAX distribution) for more
 * information.
 * <P>
 * This program has been modified to remove dependencies on
 * gnu.getopt(java-getopt) program, and has replaced the command line parser
 * with <a href="http://jakarta.apache.org/commons/cli/">Apache Jakarta Commons
 * CLI</a>
 */
public class SendFax3 {
    private final static Log log = LogFactory.getLog(SendFax3.class);

	private static Options options = new Options();

	private static HelpFormatter hf = new HelpFormatter();

	public static void main(String[] args) {
		String user = "fax"; // -u
		String host = "localhost"; // -h
		String destination = "123456"; // -d
		String from = user; // -f
		String killtime = "000259"; // -k
		int maxdials = 12; // -T
		int maxtries = 3; // -t
		int priority = 127; // -P
		String notifyaddr = user; // -f
		int resolution = 98; // -l, -m
		Dimension pagesize; // -s
		String notify = "none";
		String pagechop = "default";
		int chopthreshold = 3;
		Vector documents = new Vector();
		boolean verbose = false;
		boolean from_is_set = false;
		pagesize = Pagesize.LETTER; // default pagesize is US Letter
		char opt;

		// Define our command line options
		Option u = OptionBuilder.withArgName("user").hasArg().withDescription(
				"user to login to the server with").create('u');

		Option h = OptionBuilder.withArgName("host").hasArg().withDescription(
				"specify the fax server hostname").create('h');

		Option d = OptionBuilder.withArgName("number").hasArg()
				.withDescription("specify a destination FAX <number>").create(
						'd');

		Option f = OptionBuilder.withArgName("sender").hasArg()
				.withDescription(
						"user <sender> as the identity of the FAX sender")
				.create('f');

		Option k = OptionBuilder
				.withArgName("time")
				.hasArg()
				.withDescription(
						"kill the job if it doesn't complete after the indicated <time> (default: \"000259\", 2 hours, 59 minutes)")
				.create('k');

		Option T = OptionBuilder
				.withArgName("dials")
				.hasArg()
				.withDescription(
						"maximum number of <dials> to attempt for each job (default: 12)")
				.create('T');

		Option t = OptionBuilder
				.withArgName("tries")
				.hasArg()
				.withDescription(
						"make no more than <tries> attempts to deliver the FAX (default: 3)")
				.create('t');

		Option P = OptionBuilder
				.withArgName("priority")
				.hasArg()
				.withDescription(
						"assign the <priority> priority to the job (default: 127)")
				.create('P');

		Option s = OptionBuilder
				.withArgName("size")
				.hasArg()
				.withDescription(
						"specify the symbolic page <size> (legal, us-let, a3, a4, etc.)")
				.create('s');

		Option l = OptionBuilder.withDescription("use low resolution (98 dpi)")
				.create('l');

		Option m = OptionBuilder.withDescription(
				"use medium resolution (196 dpi)").create('m');

		Option v = OptionBuilder.withDescription("set verbose mode")
				.create('v');

		Option D = OptionBuilder
				.withDescription("enable delivery notification").create('D');

		Option R = OptionBuilder.withDescription(
				"enable delivery and retry notification").create('R');

		Option N = OptionBuilder.withDescription(
				"disable delivery and retry notification").create('N');

		// Add the options to our list of options.
		options.addOption(u);
		options.addOption(h);
		options.addOption(d);
		options.addOption(f);
		options.addOption(k);
		options.addOption(T);
		options.addOption(t);
		options.addOption(P);
		options.addOption(l);
		options.addOption(m);
		options.addOption(s);
		options.addOption(v);
		options.addOption(D);
		options.addOption(R);
		options.addOption(N);

		HylaFAXClient c = new HylaFAXClient();
//		c.setSocketTimeout(10000000);
		CommandLineParser parser = new GnuParser();
		CommandLine line = null;

		try {
			
			line = parser.parse(options, args);
			
		} catch (ParseException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
		System.out.println("------------------------>"+line.getOptionValue("u"));
		System.out.println("------------------------>"+line.getOptionValue("s"));
		
		
		
		// <<<<<<< PARSE using for each loop would be a better option for J5SE
		// 1.5+ (This would result in a dependency on J5SE 1.5+)

		Iterator it = line.iterator();
		
		
		
		
		
		while (it.hasNext()) {
			Option o = (Option) it.next();
			switch (o.getOpt().charAt(0)) {
			case 'd':
				// destination
				destination = o.getValue();
				break;
			case 'f':
				// from address
				from_is_set = true;
				from = o.getValue();
				break;
			case 'k':
				// killtime
				killtime = o.getValue();
				break;
			case 'h':
				host = o.getValue();
				break;
			case 'l':
				// low-res
				resolution = Job.RESOLUTION_LOW;
				break;
			case 'm':
				// medium-res
				resolution = Job.RESOLUTION_MEDIUM;
				break;
			case 't':
				maxtries = Integer.parseInt(o.getValue());
				break;
			case 'T':
				maxdials = Integer.parseInt(o.getValue());
				break;
			case 'D':
				notify = Job.NOTIFY_DONE;
				break;
			case 'R':
				notify = Job.NOTIFY_REQUEUE;
				break;
			case 'N':
				notify = Job.NOTIFY_NONE;
				break;
			case 'P':
				priority = Integer.parseInt(o.getValue());
				break;
			case 's':
				pagesize = Pagesize.getPagesize(o.getValue());
				if (pagesize == null) {
					// no good
					System.err.println(o.getValue()
							+ " is not a valid pagesize value");
					usage();
					System.exit(-1);
				}
				break;
			case 'u':
				user = o.getValue();
				break;
			case 'v':
				// verbose mode
				verbose = true;
				break;
			case '?':
				usage();
				System.exit(-1);
				break;
			default:
				usage();
				System.exit(-1);
				// error
				break;
			}
		}

		// validate some parameters
		if (!from_is_set) {
			from = user;
		}

		// there should be a destination
		if (destination == null) {
			// destination is required
			System.err.println("A destination fax number is required.");
			usage();
			System.exit(-1);
		}

		// make sure there is at least one file to send
		String files[] = line.getArgs();

		if (files.length < 1) {
			// at least one document is required
			System.err.println("No files specified.");
			usage();
			System.exit(-1);
		}

		// get down to business, send the FAX already
		try {
			c.open(host);

			if (c.user(user)) {
				// need password
				System.out.print("Password:");
				BufferedReader input = new BufferedReader(
						new InputStreamReader(System.in));
				String password = input.readLine();
				c.pass(password);
			}

			c.noop(); // for the heck of it
			c.tzone(ClientProtocol.TZONE_LOCAL);

			// Send files up to server

			for (int i = 0; i < files.length; i++) {
				FileInputStream file = new FileInputStream(files[i]);
				String remote_filename = c.putTemporary(file);
				documents.addElement(remote_filename);
			}

			Job job = c.createJob(); // start a new job

			// set job properties
			job.setFromUser(from);
			job.setNotifyAddress(from);
			job.setKilltime(killtime);
			job.setMaximumDials(maxdials);
			job.setMaximumTries(maxtries);
			job.setPriority(priority);
			job.setDialstring(destination);
			job.setVerticalResolution(resolution);
			job.setPageDimension(pagesize);
			job.setNotifyType(notify);
			job.setChopThreshold(chopthreshold);

			// add documents to the job
			for (int i = 0; i < documents.size(); i++) {
				String document = (String) documents.elementAt(i);
				job.addDocument(document);
			}

			c.submit(job); // submit the job to the scheduler
			System.out.println("Atin Job : " + job.getId());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			// disconnect from the server
			try {
				c.quit();
			} catch (Exception e) {
				// quit failed, not much we can do now
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * print out usage information on this program
	 */
	public static void usage() {
		// Make a pretty help output for usage.
		hf
				.printHelp(
						"SendFax <options> file1 ...",
						null,
						options,
						"Files queued must be formats that the HylaFAX server can handle natively (PS, TIFF, etc.) as no client-side conversions are performed.");
		
		
	}
}