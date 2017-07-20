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

import com.google.gwt.user.client.Window;
import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.TranslationResult;
import java.util.Date;
import java.util.List;

/**
 * Loads older versions of all subtitle items in the document
 * @author Matúš Námešný
 */
public class LoadPreviousVersions extends Callable<List<TranslationResult>> {

    List<TranslationResult> currentResults;
    Date date;
    TranslationWorkspace workspace;

    @Override
    public void onSuccessAfterLog(List<TranslationResult> results) {
        if (results.size() != 0) {
            workspace.fillTranslationResults(results);
        } else {
            Window.alert("No previous versions of subtitles found for selected date");
        }
    }

    public LoadPreviousVersions(List<TranslationResult> currentResults, Date date, TranslationWorkspace workspace) {
        this.currentResults = currentResults;
        this.date = date;
        this.workspace = workspace;

        enqueue();
    }

    @Override
    protected void call() {
        filmTitService.loadPreviousVersions(Gui.getSessionID(), currentResults, date, this);
    }

}
