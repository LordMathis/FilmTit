/*Copyright 2017 Matúš Námešný

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

import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.dialogs.SettingsDialog;
import cz.filmtit.share.Document;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.User;

/**
 * saves document settings
 * @author Matúš Námešný
 */
public class SaveSettings extends Callable<Void>{

    private User user;
    private Document doc;
    private String moviePath;
    private Boolean posteditOn;
    private Boolean localFile;
    private Boolean autoplay;
    private SettingsDialog settingsDialog;
    private Integer maxChar;

    public SaveSettings(User user, Document doc, String moviePath, Boolean postediOn, Boolean localFile, Boolean autoplay, SettingsDialog settingsDialog) {
        this.user = user;
        this.doc = doc;
        this.moviePath = moviePath;
        this.posteditOn = postediOn;
        this.localFile = localFile;
        this.settingsDialog = settingsDialog;
        this.autoplay = autoplay;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(Void result) {
        settingsDialog.close();
    }

    @Override
    public void onFinalError(String message) {
        super.onFinalError("ERROR: Couldn't save settings \n" + message);
    }

    @Override
    protected void call() {
        filmTitService.saveSettings(Gui.getSessionID(), doc, moviePath, posteditOn, localFile, autoplay, this);
    }

}
