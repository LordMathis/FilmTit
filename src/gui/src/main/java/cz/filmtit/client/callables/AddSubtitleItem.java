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
import cz.filmtit.share.TimedChunk;

/**
 * adds subtitle item to an existing document
 * @author Matúš Námešný
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
