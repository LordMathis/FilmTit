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
import static cz.filmtit.client.Callable.filmTitService;
import cz.filmtit.client.Gui;
import cz.filmtit.share.TimedChunk;
import java.util.List;

/**
 *
 * @author Matúš Námešný
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
