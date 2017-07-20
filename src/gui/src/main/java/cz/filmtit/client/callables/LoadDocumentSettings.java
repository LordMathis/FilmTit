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
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.Document;
import cz.filmtit.share.DocumentUserSettings;

/**
 * loads document settings
 * @author Matúš Námešný
 */
public class LoadDocumentSettings extends Callable<DocumentUserSettings> {

    SettingsDialog settingsDialog;
    TranslationWorkspace workspace;
    Document doc;

    public LoadDocumentSettings(SettingsDialog settingsDialog, Document doc) {
        this.settingsDialog = settingsDialog;
        this.doc = doc;
        enqueue();
    }

    public LoadDocumentSettings(TranslationWorkspace workspace, Document doc) {
        this.workspace = workspace;
        this.doc = doc;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(DocumentUserSettings result) {

        if (settingsDialog != null) {
            settingsDialog.getSetPostedit().setValue(result.getPosteditOn());

            if (!result.isLocalFile() && !result.getMoviePath().isEmpty()) {

                settingsDialog.getYtURL().setValue("https://www.youtube.com/watch?v=" + result.getMoviePath());
            }
            settingsDialog.getAutoplay().setValue(result.getAutoplay());

            settingsDialog.setEnabled(true);

        } else if (workspace != null) {
            workspace.setPosteditOn(result.getPosteditOn());
            workspace.setMoviePath(result.getMoviePath());
            workspace.setIsLocalFile(result.isLocalFile());
            workspace.setAutoplay(result.getAutoplay());

        }
    }

    @Override
    protected void call() {
        filmTitService.loadDocumentSettings(Gui.getSessionID(), doc, this);
    }

}
