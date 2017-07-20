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
import cz.filmtit.share.AuditResponse;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.TranslationResult;

/**
 * Loads older version of a subtitle item
 * @author Matúš Námešný
 */
public class LoadOldSubtitleItem extends Callable<AuditResponse> {

    private TranslationResult result;
    private TranslationWorkspace workspace;
    private Number revisionNumber;

    public LoadOldSubtitleItem(TranslationResult result, TranslationWorkspace workspace, Number revisionNumber) {
        this.result = result;
        this.workspace = workspace;
        this.revisionNumber = revisionNumber;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(AuditResponse response) {

        if (response.getTranslationResult() == null) {
            Window.alert("No older versions of this subtitle item found");
            return;
        }

        TranslationResult translationResult = response.getTranslationResult();
        Number number = response.getNumber();

        workspace.addLoadedRevision(translationResult.getSourceChunk().getChunkIndex(), number);
        workspace.showResult(translationResult);
    }

    @Override
    protected void call() {
        filmTitService.loadOldSubtitleItem(Gui.getSessionID(), result, revisionNumber, this);
    }

}
