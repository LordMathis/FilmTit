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
import cz.filmtit.share.TranslationResult;
import java.util.Date;
import java.util.List;

/**
 *
 * @author matus
 */
public class LoadPreviousVersions extends Callable<List<TranslationResult>>{
    
    List<TranslationResult> currentResults;
    Date date;
    TranslationWorkspace workspace;
    
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
