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
public class LockTranslationResult extends Callable<Void> {

    TranslationResult tResult;
    SubgestBox subgestBox;
    TranslationWorkspace workspace;
    TranslationWorkspace.SourceChangeHandler sourceChangeHandler = null;
    TranslationWorkspace.TimeChangeHandler timeChangeHandler = null;

    public LockTranslationResult() {
        // do nothing
    }

    public LockTranslationResult(SubgestBox subgestBox, TranslationWorkspace workspace) {
        super();

        retries = 0;
        this.subgestBox = subgestBox;
        this.tResult = subgestBox.getTranslationResult();
        this.workspace = workspace;
        this.subgestBox.setEnabled(false);

        PosteditBox posteditBox = this.subgestBox.getPosteditBox();
        if (posteditBox != null) {
            posteditBox.setEnabled(false);
        }

        if (!workspace.getLockTranslationResultCalls().containsKey(tResult.getSourceChunk())) {
            workspace.getLockTranslationResultCalls().put(tResult.getSourceChunk(), this);
            enqueue();
        }
    }

    public LockTranslationResult(SubgestBox subgestBox, TranslationWorkspace workspace, TranslationWorkspace.SourceChangeHandler sourceChangeHandler) {
        super();

        retries = 0;
        this.subgestBox = subgestBox;
        this.tResult = subgestBox.getTranslationResult();
        this.workspace = workspace;
        this.subgestBox.setEnabled(false);
        this.sourceChangeHandler = sourceChangeHandler;

        PosteditBox posteditBox = this.subgestBox.getPosteditBox();
        if (posteditBox != null) {
            posteditBox.setEnabled(false);
        }

        if (!workspace.getLockTranslationResultCalls().containsKey(tResult.getSourceChunk())) {
            workspace.getLockTranslationResultCalls().put(tResult.getSourceChunk(), this);
            enqueue();

        }
    }

    public LockTranslationResult(SubgestBox subgestBox, TranslationWorkspace workspace, TranslationWorkspace.TimeChangeHandler timeChangeHandler) {
        super();

        retries = 0;
        this.subgestBox = subgestBox;
        this.tResult = subgestBox.getTranslationResult();
        this.workspace = workspace;
        this.subgestBox.setEnabled(false);
        this.timeChangeHandler = timeChangeHandler;

        PosteditBox posteditBox = this.subgestBox.getPosteditBox();
        if (posteditBox != null) {
            posteditBox.setEnabled(false);
        }

        if (!workspace.getLockTranslationResultCalls().containsKey(tResult.getSourceChunk())) {
            workspace.getLockTranslationResultCalls().put(tResult.getSourceChunk(), this);
            enqueue();

        }
    }

    @Override
    protected void onFinalError(String message) {

        subgestBox.addStyleDependentName("unlocked");
        subgestBox.setEnabled(false);

        PosteditBox posteditBox = subgestBox.getPosteditBox();
        if (posteditBox != null) {
            posteditBox.addStyleDependentName("unlocked");
            posteditBox.setEnabled(false);
        }

        workspace.getLockTranslationResultCalls().remove(tResult.getSourceChunk());
        workspace.setUnlockedSubgestBox(subgestBox);

    }

    @Override
    public void onSuccessAfterLog(Void result) {
        //Gui.log(LevelLogEnum.DebugNotice, "LockTranslationResult", "Locked Translation Result id: " + String.valueOf(tResult.getChunkId()));

        subgestBox.setEnabled(true);
        PosteditBox posteditBox = subgestBox.getPosteditBox();
        if (posteditBox != null) {
            subgestBox.getPosteditBox().setEnabled(true);
        }

        workspace.setLockedSubgestBox(subgestBox);
        workspace.getTimer().schedule(60000);

        subgestBox.addStyleDependentName("locked");
        if (posteditBox != null) {
            posteditBox.addStyleDependentName("locked");
        }



        if (this.sourceChangeHandler != null) {
            sourceChangeHandler.changeSource();
        }

        if (this.timeChangeHandler != null) {
            timeChangeHandler.changeTiming();
        }

        workspace.getLockTranslationResultCalls().remove(tResult.getSourceChunk());

    }

    @Override
    protected void call() {
        filmTitService.lockTranslationResult(tResult, Gui.getSessionID(), this);
    }

}
