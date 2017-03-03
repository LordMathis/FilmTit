/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import cz.filmtit.client.Callable;
import static cz.filmtit.client.Callable.filmTitService;
import cz.filmtit.client.Gui;
import cz.filmtit.share.TimedChunk;
import java.util.List;

/**
 *
 * @author matus
 */
public class StopPosteditSuggestions extends Callable<Void> {

    List<TimedChunk> chunks;

    @Override
    public String getName() {
        return "StopPosteditSuggestions (chunks size: " + chunks.size() + ")";
    }

    // ignore the errors, it does not matter that much if this one fails
    @Override
    protected void onInvalidSession() {
        // nothing
    }

    @Override
    protected void onFinalError(String message) {
        // nothing
    }

    /**
     * Stop generating translation results for the given chunks (to be called
     * after getTranslationResults has been called with the given chunks but the
     * results are suddenly not needed anymore).
     */
    public StopPosteditSuggestions(List<TimedChunk> chunks) {
        super();

        this.chunks = chunks;

        // + 0.1s for each chunk
        callTimeOut += 100 * chunks.size();

        enqueue();
    }

    @Override
    protected void call() {
        filmTitService.stopPosteditSuggestions(Gui.getSessionID(), chunks, this);

    }

}
