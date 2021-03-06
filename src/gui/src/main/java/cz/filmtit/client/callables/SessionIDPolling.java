/*Copyright 2012 FilmTit authors - Karel Bílek, Josef Čech, Joachim Daiber, Jindřich Libovický, Rudolf Rosa, Jan Václ

This file is part of FilmTit.

FilmTit is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 2.0 of the License, or
(at your option) any later version.

FilmTit is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FilmTit.  If not, see <http://www.gnu.org/licenses/>.*/

package cz.filmtit.client.callables;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

import cz.filmtit.client.*;
import cz.filmtit.client.dialogs.Dialog;
import cz.filmtit.client.dialogs.SessionIDPollingDialog;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.core.client.*;
import cz.filmtit.share.*;
import cz.filmtit.share.exceptions.AuthenticationFailedException;

import java.util.*;

/**
 * Displays the {@link SessionIDPollingDialog}.
 * Check whether the user has already successfully logged in using his OpenID with the given authID.
 * Reinvokes itself iteratively until a definitive response is received
 * about the result of the authentication.
 * Sets the logged in state accordingly
 * and informs the user in case of an error.
 * @author rur
 *
 */
public class SessionIDPolling extends Callable<SessionResponse> {

	/**
	 * temporary ID for authentication
	 */
	private int authID;

	/**
	 * dialog polling for session ID
	 */
	private Dialog sessionIDPollingDialog;

	/**
	 * indicates whether polling for session ID is in progress
	 */
	private boolean sessionIDPolling = false;

    @Override
	public String getName() {
        return getNameWithParameters(authID);
    }

    @Override
	public void onSuccessAfterLog(SessionResponse response) {
		if (response != null) {
			Gui.log("A session ID received successfully! SessionId = " + response.sessionID);
			// stop polling
			sessionIDPolling = false;
			sessionIDPollingDialog.close();
			// we now have a session ID
			Gui.setSessionID(response.sessionID);
			// and a User
            Gui.logged_in(response.userWithoutDocs);
		}
		else {
			Gui.log("no session ID received");
			// continue polling
			hasReturned = false;
			new EnqueueTimer(300);
		}
	}
    
    @Override
	public void onFailureAfterLog(Throwable caught) {
		if(sessionIDPolling) {
			// stop polling
			sessionIDPolling = false;
			sessionIDPollingDialog.close();
			// say error
			if (caught instanceof AuthenticationFailedException) {
				// Userspace throws that if the authentication fails in the usual way :-)
				displayWindow("The OpenID login was not successful. Message from the server: " + caught.getLocalizedMessage());
			} else {
				// some unexpected exception
				displayWindow("The OpenID login was not successful. Message from the server: " + caught);
			}
		}
	}
    
    @Override
    protected void onFinalError(String message) {
		if(sessionIDPolling) {
			// stop polling
			sessionIDPolling = false;
			sessionIDPollingDialog.close();
			// say error
			super.onFinalError(message);
		}
		// else ignore
    }
	
	/**
	 * Check whether the user has already successfully logged in using his OpenID with the given authID.
	 * Reinvokes itself iteratively until a definitive response is received
	 * about the result of the authentication.
	 * Sets the logged in state accordingly
	 * and informs the user in case of an error.
	 */
	public SessionIDPolling(int authID) {
		super();
		
		this.authID = authID;

		createDialog();
		
		sessionIDPolling = true;
        enqueue();
	}
	
	/**
	 * open a dialog saying that we are waiting for the user to authenticate
	 */
	private void createDialog() {
		sessionIDPollingDialog = new SessionIDPollingDialog(this);
	}

//		private void startSessionIDPolling() {
//			sessionIDPolling = true;
//			
//			enqueue();
//			
//			Scheduler.RepeatingCommand poller = new RepeatingCommand() {
//				
//				@Override
//				public boolean execute() {
//					if (sessionIDPolling) {
//						enqueue();
//						// call();
//						return true;
//					} else {
//						return false;
//					}
//				}
//			};
//			
//			Scheduler.get().scheduleFixedDelay(poller, 500);
//		}

	/**
	 * Called from {@link SessionIDPollingDialog} in case the user decides to cancel the authentication process.
	 */
	public void stopSessionIDPolling() {
		sessionIDPolling = false;
	}
	
	@Override protected void call() {
		if (sessionIDPolling) {
			Gui.log("asking for session ID with authID=" + authID);
			filmTitService.getSessionID(authID, this);			
		}
	}
}
