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


import cz.filmtit.client.*;

import cz.filmtit.share.*;

/**
 * Retrieves the User object with the user settings. Passes them to the
 * settingsReceiver on success.
 */
public class LoadSettings extends Callable<SessionResponse> {

    // parameters
    private ReceivesSettings settingsReceiver;

    @Override
    public void onSuccessAfterLog(SessionResponse response) {
        if (response != null) {
            Gui.resetUser(response.userWithoutDocs);
            settingsReceiver.onSettingsReceived(response.userWithoutDocs);
        } else {
            Gui.log("Warning: sessionID invalid.");
            Gui.logged_out();
        }
    }

    @Override
    protected void onInvalidSession() {
        Gui.logged_out();
    }

    @Override
    protected void onFinalError(String message) {
        Gui.logged_out();
    }

    /**
     * Retrieves the User object with the user settings. Passes them to the
     * settingsReceiver on success.
     */
    public LoadSettings(ReceivesSettings settingsReceiver) {
        super();

        this.settingsReceiver = settingsReceiver;

        enqueue();
    }

    @Override
    protected void call() {
        filmTitService.checkSessionID(Gui.getSessionID(), this);
    }
}
