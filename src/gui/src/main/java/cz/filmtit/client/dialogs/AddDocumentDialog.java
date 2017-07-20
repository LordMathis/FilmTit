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
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.callables.AddDocument;

/**
 *
 * @author Matúš Námešný
 */
public class AddDocumentDialog extends Dialog {

    private static AddDocumentDialogUiBinder uiBinder = GWT.create(AddDocumentDialogUiBinder.class);

    interface AddDocumentDialogUiBinder extends UiBinder<Widget, AddDocumentDialog> {
    }

    public AddDocumentDialog() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        submitBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String docShareId = shareIdBox.getText();
                if (!docShareId.isEmpty()) {
                    new AddDocument(docShareId, Gui.getUser(), AddDocumentDialog.this);
                }
            }
        });
    }

    @UiField
    TextBox shareIdBox;

    @UiField
    Button submitBtn;
}
