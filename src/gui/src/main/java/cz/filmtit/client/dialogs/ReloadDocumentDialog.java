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
package cz.filmtit.client.dialogs;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;

/**
 *
 * @author Matúš Námešný
 */
public class ReloadDocumentDialog extends Dialog {

    private static ReloadDocumentDialogUiBinder uiBinder = GWT.create(ReloadDocumentDialogUiBinder.class);

    interface ReloadDocumentDialogUiBinder extends UiBinder<Widget, ReloadDocumentDialog> {
    }

    public ReloadDocumentDialog() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    Button submitBtn;

    @UiHandler("submitBtn")
    void onClick(ClickEvent e) {
        this.close();
        Gui.getPageHandler().refresh();
    }
}
