// ReceiveNotifier.java - a HylaFAX Job representation
// $Id: ReceiveNotifier.java,v 1.2 2006/02/20 04:52:10 sjardine Exp $
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

/**
 * This interface defines what a class should implement in order to notify
 * others of job related events.
 * 
 * @author $Author: sjardine $
 * @version $Id: ReceiveNotifier.java,v 1.2 2006/02/20 04:52:10 sjardine Exp $
 * @see grg.hylafax.job.ReceiveListener
 * @see grg.hylafax.job.ReceiveEvent
 */
public interface ReceiveNotifier {

    /**
     * This method is called when Job state changes.
     */
    public void notifyReceiveListeners(ReceiveEvent details);

    /**
     * This method is called to register a Job ReceiveListener.
     */
    public void addReceiveListener(ReceiveListener l);

    /**
     * This method is used to deregister a Job ReceiveListener.
     */
    public void removeReceiveListener(ReceiveListener l);

}// ReceiveNotifier class
// ReceiveNotifier.java
