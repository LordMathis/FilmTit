/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author matus
 */
public class LockTranslationResult extends Callable<Void> {

    TranslationResult tResult;
    SubgestBox subgestBox;
    TranslationWorkspace workspace;

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
        Gui.log(LevelLogEnum.DebugNotice, "LockTranslationResult", "Locked Translation Result id: " + String.valueOf(tResult.getChunkId()));

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

        workspace.getLockTranslationResultCalls().remove(tResult.getSourceChunk());

    }

    @Override
    protected void call() {
        filmTitService.lockTranslationResult(tResult, Gui.getSessionID(), this);
    }

}
