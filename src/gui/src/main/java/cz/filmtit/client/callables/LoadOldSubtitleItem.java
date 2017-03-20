/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import com.google.gwt.user.client.Window;
import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.AuditResponse;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.TranslationResult;

/**
 *
 * @author matus
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
        
        workspace.addLoadedRevision(translationResult, number);
        workspace.showResult(translationResult);
    }

    @Override
    protected void call() {
        Gui.log(LevelLogEnum.Error, this.getClassName(), result + " " + revisionNumber);
        filmTitService.loadOldSubtitleItem(Gui.getSessionID(), result, revisionNumber, this);
    }
    
}
