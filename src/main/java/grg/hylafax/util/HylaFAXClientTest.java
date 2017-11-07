// HylaFAXClientTest.java - gnu.hylafax test application for the client
// $Id: HylaFAXClientTest.java,v 1.3 2007/05/07 18:26:53 sjardine Exp $
//
// Copyright 1999, 2000 Joe Phillips <jaiger@net-foundry.com>
// Copyright 2001, Innovation Software Group, LLC - http://www.innovationsw.com
// Copyright 2006, John Yeary <jyeary@javanetwork.net>
//
// For information on the HylaFAX FAX server see
// http://www.hylafax.org/
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

package grg.hylafax.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gnu.hylafax.Client;
import gnu.hylafax.HylaFAXClient;

/**
 * @author John Yeary <jyeary@javanetwork.net>
 * @version 1.0
 * 
 * <p>
 * This class was created to move the test out of the HylaFAXClient class.
 * </p>
 * <p>
 * TODO A better test framework needs to be put into place.
 * </p>
 */
public class HylaFAXClientTest extends HylaFAXClient {

    private final static Log log = LogFactory.getLog(Client.class);

    /** Creates a new instance of HylaFAXClientTest */
    public HylaFAXClientTest() {
	// Do nothing.
    }

    /**
     * Run some basic tests.
     * 
     * @param Arguments
     *                an array of command-line-argument Strings
     */
    public static void main(String Arguments[]) {
	HylaFAXClient c = new HylaFAXClient();

	try {
	    c.open("192.168.110.61");
	    c.noop();
	    c.setPassive(true); // use passive transfers
	    c.user("ec2-user");

	    /*
	     * Using this type of file allows us to visually verify that the
	     * files have not changed after being transferred back and forth.
	     */
	    c.type(TYPE_IMAGE); // should cause files to remain same size after

	    System.out.println("current directory is: " + c.pwd());

	    c.cwd("AtinAgarwal123");
	    // c.cwd("bad-directory-name");
	    System.out.println("current directory is: " + c.pwd());

	    c.cdup();
	    System.out.println("current directory is: " + c.pwd());

	    // c.admin("MyPassword");
	    System.out.println("idle timer set to " + c.idle() + " seconds.");
	    c.idle(1800);
	    System.out.println("idle timer set to " + c.idle() + " seconds.");

	    System.out.println("job format: " + c.jobfmt());
	    c.jobfmt("%-4j");
	    System.out.println("job format: " + c.jobfmt());

	    // set file structure
	    c.stru(STRU_FILE);
	    // c.stru(STRU_RECORD);
	    c.stru(STRU_TIFF);
	    // c.stru(STRU_PAGE);
	    c.stru(STRU_FILE);

	    // send temp file (stot)
	    {
		String filename = "test.ps";
		FileInputStream file = new FileInputStream(filename);

		String f = c.putTemporary(file);
		System.out.println("filename= " + f);

		// test size command
		long local_size, remote_size;
		local_size = (new RandomAccessFile(filename, "r").length());
		remote_size = c.size(f);
		System.out.println(filename + " local size is " + local_size);
		System.out.println(f + " remote size is " + remote_size);

		// retrieve the temp file now
		FileOutputStream out_file = new FileOutputStream(filename
			+ ".retr");
		c.get(f, out_file);
		local_size = (new RandomAccessFile(filename + ".retr", "r")
			.length());
		System.out.println(filename + ".retr size is " + local_size);

		// retrieve the temp file now (using ZLIB mode)
		FileOutputStream zip_file = new FileOutputStream(filename
			+ ".gz");
		c.mode(MODE_ZLIB);
		c.get(f, zip_file);
		local_size = (new RandomAccessFile(filename + ".gz", "r")
			.length());
		System.out.println(filename + ".gz size is " + local_size);
		c.mode(MODE_STREAM);

	    }
	    // end stot/retr test

	    // test list command
	    {
		Vector files;
		int counter;

		// list current directory
		files = c.getList();
		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}

		// list /tmp directory
		files = c.getList("/tmp");
		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}

		// list /tmp directory (with mode ZLIB)
		c.mode(MODE_ZLIB);
		files = c.getList("/tmp");
		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}
		c.mode(MODE_STREAM);

		try {
		    // attempt to list file that doesn't exist
		    c.getList("/joey-joe-joe-jr.shabba-do"); // that's the
								// worst name
								// I've ever
								// heard.
		    System.out.println("ERROR: file not found was expected");
		} catch (FileNotFoundException fnfe) {
		    // expected this, continue
		    System.out.println("GOOD: file not found, as expected");
		}

		// list current directory, should be the same as above
		files = c.getList();
		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}
	    }
	    // end list test

	    // test nlst command
	    {
		Vector files;
		int counter;

		// list /tmp directory
		files = c.getNameList("/tmp");

		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}

		// list /tmp directory
		files = c.getNameList("/tmp");

		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}

		// list /tmp directory (using mode ZLIB)
		c.mode(MODE_ZLIB);
		files = c.getNameList("/tmp");

		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}

		c.mode(MODE_STREAM);

		// list current directory
		files = c.getNameList();

		for (counter = 0; counter < files.size(); counter++) {
		    System.out.println((String) files.elementAt(counter));
		}

	    }
	    // end nlst test

	    // get system type string
	    String system = c.syst();
	    System.out.println("system type: " + system + ".");

	    c.noop();

	    // stat tests
	    {
		// test normal server status message
		Vector status = c.stat();
		int counter;
		for (counter = 0; counter < status.size(); counter++) {
		    System.out.println(status.elementAt(counter));
		}

		// test directory status
		status = c.stat("docq");
		for (counter = 0; counter < status.size(); counter++) {
		    System.out.println(status.elementAt(counter));
		}

		// test non-existing directory status
		try {
		    status = c.stat("joey-joe-joe-junior-shabba-do");
		    for (counter = 0; counter < status.size(); counter++) {
			System.out.println(status.elementAt(counter));
		    }
		} catch (FileNotFoundException fnfe) {
		    System.out
			    .println("GOOD: file not found.  this is what we expected");
		}
	    }

	    c.noop();

	    c.quit();

	} catch (Exception e) {
	    log.error(e.getMessage(), e);
	    System.out.println(e);
	}

	System.out.println("main: end");
    }

}
