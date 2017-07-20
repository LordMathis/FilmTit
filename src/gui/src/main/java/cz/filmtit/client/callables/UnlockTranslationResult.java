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
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.client.subgestbox.PosteditBox;
import cz.filmtit.client.subgestbox.SubgestBox;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.TranslationResult;

/**
 *
 * @author Matúš Námešný
 */
public class UnlockTranslationResult extends Callable<Void> {

    TranslationResult tResult;
    TranslationWorkspace workspace;
    SubgestBox subgestBox;

    boolean toLock = false;
    SubgestBox toLockBox;

    public UnlockTranslationResult() {
        // do nothing
    }

    public UnlockTranslationResult(SubgestBox subgestBox, TranslationWorkspace workspace) {
        super();

        retries = 0;
        this.tResult = subgestBox.getTranslationResult();
        this.subgestBox = subgestBox;
        this.workspace = workspace;

        if (!workspace.getUnlockTranslationResultCalls().containsKey(tResult.getSourceChunk())) {
            workspace.getUnlockTranslationResultCalls().put(tResult.getSourceChunk(), this);
            enqueue();
        }
    }

    public UnlockTranslationResult(SubgestBox subgestBox, TranslationWorkspace workspace, SubgestBox toLockBox) {
        super();

        retries = 0;
        this.tResult = subgestBox.getTranslationResult();
        this.subgestBox = subgestBox;
        this.workspace = workspace;

        this.toLock = true;
        this.toLockBox = toLockBox;

        if (!workspace.getUnlockTranslationResultCalls().containsKey(tResult.getSourceChunk())) {
            workspace.getUnlockTranslationResultCalls().put(tResult.getSourceChunk(), this);
            enqueue();
        }
    }

    @Override
    public void onSuccessAfterLog(Void result) {
        Gui.log(LevelLogEnum.DebugNotice, "UnlockTranslationResult", "Unlocked Translation Result id: " + String.valueOf(tResult.getChunkId()));

        workspace.getUnlockTranslationResultCalls().remove(tResult.getSourceChunk());

        workspace.setLockedSubgestBox(null);
        workspace.setUnlockedSubgestBox(subgestBox);
        subgestBox.setFocus(false);
        subgestBox.removeStyleDependentName("locked");

        PosteditBox posteditBox = subgestBox.getPosteditBox();
        if (posteditBox != null) {
            posteditBox.removeStyleDependentName("locked");
        }

        if (toLock) {
            new LockTranslationResult(toLockBox, workspace);
        }
    }

    @Override
    public void onFinalError(String message) {
        workspace.getUnlockTranslationResultCalls().remove(tResult.getSourceChunk());
    }

    @Override
    protected void call() {
        filmTitService.unlockTranslationResult(tResult.getSourceChunk().getChunkIndex(),
                tResult.getDocumentId(), Gui.getSessionID(), this);

    }
}
