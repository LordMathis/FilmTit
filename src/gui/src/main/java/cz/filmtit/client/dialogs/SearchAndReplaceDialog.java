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

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.pages.TranslationWorkspace;

/**
 *
 * @author Matúš Námešný
 */
public class SearchAndReplaceDialog extends Dialog {

    private static SearchAndReplaceDialogUiBinder uiBinder = GWT.create(SearchAndReplaceDialogUiBinder.class);

    interface SearchAndReplaceDialogUiBinder extends UiBinder<Widget, SearchAndReplaceDialog> {
    }

    public SearchAndReplaceDialog(TranslationWorkspace workspace) {
        initWidget(uiBinder.createAndBindUi(this));
        this.workspace = workspace;

        if (!workspace.isPosteditOn()) {
            submitButton.setEnabled(false);
            alertPostedit.setText("WARNING: Post-edit API is turned off. Search & Replace will only work when post-edit API is turned on.");
            alertPostedit.setVisible(true);
        }
    }

    TranslationWorkspace workspace;

    @UiField
    Alert alertPostedit;

    @UiField
    Form form;

    @UiField
    Button cancelButton;

    @UiHandler("cancelButton")
    void cancel(ClickEvent e) {
        close();
    }

    @UiField
    SubmitButton submitButton;

    @UiHandler("form")
    void submit(Form.SubmitEvent e) {

        if (!searchBox.getValue().isEmpty() && !replaceBox.getValue().isEmpty()) {
            try {
                RegExp searchExp = RegExp.compile(searchBox.getValue(), "g");
                workspace.searchAndReplace(searchExp, replaceBox.getValue());
                close();
            } catch (RuntimeException exception) {
                Window.alert("Search pattern is not valid regular expression\n" + searchBox.getValue());
            }

        } else {
            return;
        }

    }

    @UiField
    TextBox searchBox;

    @UiField
    TextBox replaceBox;

}
