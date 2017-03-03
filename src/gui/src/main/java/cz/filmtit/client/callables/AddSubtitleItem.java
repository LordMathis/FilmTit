/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.TimedChunk;

/**
 *
 * @author matus
 */
public class AddSubtitleItem extends Callable<Void>{

    TimedChunk chunk;
    TranslationWorkspace workspace;
    
    public AddSubtitleItem(TimedChunk chunk, TranslationWorkspace workspace) {
        this.chunk = chunk;
        this.workspace = workspace;
        enqueue();
    }
    
    @Override
    protected void call() {
        filmTitService.addSubtitleItem(Gui.getSessionID(), chunk, workspace.getCurrentDocument(), this);
    }
    
}
