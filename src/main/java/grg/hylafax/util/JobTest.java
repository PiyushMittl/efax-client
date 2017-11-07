package grg.hylafax.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import gnu.hylafax.HylaFAXClient;

public class JobTest {

    private static final Log log = LogFactory.getLog(JobTest.class);

    public static void main(String[] args) throws Exception {

	BasicConfigurator.configure();

	String[] params = { "dialstring", "external", "retrytime", "lasttime",
		"sendtime", "document" };
	HylaFAXClient hyfc = new HylaFAXClient();
	// Connect to server:
	hyfc.open("hylafax");
	if (hyfc.user("autofax")) {
	    // hyfc.pass("fax");
	}

	// Replace 110 with the number of an existing job!
	hyfc.job(16839);
	System.out.println("Current job parameters (existing job):");
	for (int index = 0; index < params.length; index++) {
	    log.debug("RESP:" + params[index] + ": "
		    + hyfc.jparm(params[index]));
	}

	hyfc.jnew();
	for (int index = 0; index < params.length; index++) {
	    log.debug("RESP:" + params[index] + ": "
		    + hyfc.jparm(params[index]));
	}

	hyfc.quit();
    }
}
