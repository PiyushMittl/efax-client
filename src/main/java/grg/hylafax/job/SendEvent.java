// SendEvent.java - a HylaFAX Job representation
// $Id: SendEvent.java,v 1.2 2006/02/20 04:52:10 sjardine Exp $
//
// Copyright 2003 Innovation Software Group, LLC - http://www.innovationsw.com
//                Joe Phillips <joe.phillips@innovationsw.com>
//
// for information on the HylaFAX FAX server see
//  http://www.hylafax.org/
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
package grg.hylafax.job;


import java.util.StringTokenizer;

/**
 * This class contains the information available on a job notification.
 * @author $Author: sjardine $
 * @version $Id: SendEvent.java,v 1.2 2006/02/20 04:52:10 sjardine Exp $
 */
public class SendEvent extends Event {

    /** The job was completed successfully */
    public final static String REASON_DONE = "done";

    /** The job was not completed */
    public final static String REASON_FAILED = "failed";

    /** The job was rejected */
    public final static String REASON_REJECTED = "rejected";

    /** The job is blocked by other concurrent jobs */
    public final static String REASON_BLOCKED = "blocked";

    /** The job is being requeued for another attempt */
    public final static String REASON_REQUEUED = "requeued";

    /** The job was removed from the queue */
    public final static String REASON_REMOVED = "removed";

    /** The job was removed from the queue */
    public final static String REASON_KILLED = "killed";

    /** The job could not be sent before kill timer expired */
    public final static String REASON_TIMEDOUT = "timedout";

    /** The job document conversion failed */
    public final static String REASON_FORMATFAILED = "format_failed";

    /** The document conversion program could not be found */
    public final static String REASON_NOFORMATTER = "no_formatter";

    /** The remote side rejected a document poll */
    public final static String REASON_POLLREJECTED = "poll_rejected";

    /** There was no document available for polling */
    public final static String REASON_POLLNODOCUMENT = "poll_no_document";

    /** The document poll failed */
    public final static String REASON_POLLFAILED = "poll_failed";

    private String reason;

    private long jobTime = -1;

    private String nextAttempt = null;

    private long jobid = -1;

    public void setJobId(long jid) {
        jobid = jid;
    }

    public long getJobId() {
        return jobid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String r) {
        reason = r;
    }

    public long getElapsedTime() {
        return jobTime;
    }

    public void setElapsedTime(long time) {
        jobTime = time;
    }

    /**
     * set the job elapsed time.
     * @param time the elapsed time value in "mm:ss" format
     */
    public void setElapsedTime(String time) {
        jobTime = -1;
        if (time != null && !time.trim().equals("")) {
            StringTokenizer st = new StringTokenizer(time.trim(), ":");
            short minutes = Short.parseShort(st.nextToken());
            short seconds = Short.parseShort(st.nextToken());
            jobTime = (minutes * 60) + seconds;
        }
    }

    public void setNextAttempt(String seconds) {
        nextAttempt = seconds;
    }

    public String getNextAttempt() {
        return nextAttempt;
    }

}// SendEvent class
// SendEvent.java
