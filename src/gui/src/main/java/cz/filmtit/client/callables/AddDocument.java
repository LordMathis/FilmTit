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
import cz.filmtit.client.dialogs.AddDocumentDialog;
import cz.filmtit.share.User;

/**
 * adds an existing document to the list of users documents
 * @author Matúš Námešný
 */
public class AddDocument extends Callable<Void> {

    String shareId;
    User user;
    AddDocumentDialog dialog;

    public AddDocument(String docShareId, User user, AddDocumentDialog dialog) {
        super();
        this.shareId = docShareId;
        this.user = user;
        this.dialog = dialog;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(Void result) {
        dialog.close();
        Gui.getPageHandler().refresh();
    }

    @Override
    protected void call() {
        filmTitService.addDocument(Gui.getSessionID(), shareId, this);
    }

}
