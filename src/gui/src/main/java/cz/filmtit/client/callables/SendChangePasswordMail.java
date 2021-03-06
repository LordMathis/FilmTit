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

import com.github.gwtbootstrap.client.ui.Modal;
import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.dialogs.Dialog;

/**
 * Send a password reset e-mail
 * to the e-mail address of the user with the given username.
 * If email is set and username-based retrieval fails,
 * invokes SendChangePasswordMailByMail.
 * Otherwise informs the user about success or error.
 * @author rur
 *
 */
public class SendChangePasswordMail extends Callable<Boolean> {
    	
	// parameters
	private String username;
	private String email;
	private Dialog loginDialog;

    @Override
    public String getName() {
        return getNameWithParameters(username, email);
    }


    @Override
    public void onSuccessAfterLog(Boolean result) {
        if (result) {
            loginDialog.close();
            Gui.log("successful sendChangePasswordMail for " + username);
            displayWindow("A link to password change page has been sent to your e-mail address.");
        } else {
            // false = bad username or no email
            Gui.log("ERROR: sendChangePasswordMail didn't succeed, bad username or no email.");
        	if (email == null) {
                loginDialog.reactivateWithErrorMessage("There was an error sending password change email to you. " +
                        "Either the username '" + username + "' is not registered " +
                                "or there is no e-mail address associated with it. " +
                                "Please check the username or register with a new one. " +
                                "You can also try filling in you e-mail address instead.");
        	}
        	else {
                Gui.log("Will retry with email " + email);
        		new SendChangePasswordMailByMail(email, username, loginDialog);
        	}
        }
    }

    @Override
    protected void onFinalError(String message) {
        loginDialog.reactivateWithErrorMessage("There was an error sending password change email to you. " +
        		"Please try again. " +
                "If problems persist, try contacting the administrators. " +
                "Error message: " + message);
    }
    	
	/**
	 * Send a password reset e-mail
	 * to the e-mail address of the user with the given username.
	 * If email is set and username-based retrieval fails,
	 * invokes SendChangePasswordMailByMail.
	 * Otherwise informs the user about success or error.
	 * @param username A username to which associated email address to try to send the password change link.
	 * @param email An email address to try to use in {@link SendChangePasswordMailByMail} if this call fails.
	 * @param loginDialog
	 */
	public SendChangePasswordMail(String username, String email, Dialog loginDialog) {
		super();
		
		this.username = username;
		this.email = email;
		this.loginDialog = loginDialog;
		
		enqueue();
	}

	@Override protected void call() {
		filmTitService.sendChangePasswordMail(username, this);
	}    	
}

