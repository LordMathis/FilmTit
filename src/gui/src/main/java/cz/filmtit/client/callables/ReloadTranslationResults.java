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
import cz.filmtit.client.dialogs.ReloadDocumentDialog;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.Document;
import cz.filmtit.share.TranslationResult;
import java.util.List;

/**
 * Reload translation results to show other users edits
 * @author Matúš Námešný
 */
public class ReloadTranslationResults extends Callable<Document> {

    private long documentId;
    private TranslationWorkspace workspace;

    public ReloadTranslationResults() {

    }

    public ReloadTranslationResults(long documentId, TranslationWorkspace workspace) {
        super();
        this.documentId = documentId;
        this.workspace = workspace;

        if (workspace.getReloadTranslationResultsCall() == null) {
            workspace.setReloadTranslationResultsCall(this);
            enqueue();
        }
    }

    @Override
    public void onSuccessAfterLog(Document result) {

        workspace.setReloadTranslationResultsCall(null);

        List<TranslationResult> tResults = workspace.getCurrentDocument().getSortedTranslationResults();
        List<TranslationResult> newTResults = result.getSortedTranslationResults();

        if (newTResults.size() > tResults.size()) {
            new ReloadDocumentDialog();
        } else {
            workspace.fillTranslationResults(newTResults);
        }
    }

    @Override
    protected void call() {
        filmTitService.reloadTranslationResults(Gui.getSessionID(), documentId, this);
    }

}
