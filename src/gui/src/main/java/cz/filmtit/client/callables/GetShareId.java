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
import cz.filmtit.client.dialogs.SettingsDialog;
import cz.filmtit.share.Document;

/**
 *
 * @author Matúš Námešný
 */
public class GetShareId extends Callable<String> {

    private SettingsDialog shareDialog;
    private Document doc;

    public GetShareId(Document doc, SettingsDialog shareDialog) {
        super();
        this.shareDialog = shareDialog;
        this.doc = doc;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(String result) {
        shareDialog.getShareIdBox().setText(result);
    }

    @Override
    protected void call() {
        filmTitService.getShareId(Gui.getSessionID(), doc, this);
    }

}
