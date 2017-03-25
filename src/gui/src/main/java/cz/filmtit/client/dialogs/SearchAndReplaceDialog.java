/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.dialogs;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.LevelLogEnum;

/**
 *
 * @author matus
 */
public class SearchAndReplaceDialog extends Dialog {

    private static SearchAndReplaceDialogUiBinder uiBinder = GWT.create(SearchAndReplaceDialogUiBinder.class);

    interface SearchAndReplaceDialogUiBinder extends UiBinder<Widget, SearchAndReplaceDialog> {
    }

    public SearchAndReplaceDialog(TranslationWorkspace workspace) {
        initWidget(uiBinder.createAndBindUi(this));
        this.workspace = workspace;
    }

    TranslationWorkspace workspace;

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

        if (((searchFirstClm.getValue() || searchSecondClm.getValue())                
                && (replaceSecondClm.getValue() || replaceThirdClm.getValue())                
                && !searchBox.getValue().isEmpty() && !replaceBox.getValue().isEmpty())) {
            try {
                RegExp searchExp = RegExp.compile(searchBox.getValue(),  "g" );
                workspace.searchAndReplace(searchExp, replaceBox.getValue(),
                        searchFirstClm.getValue(), searchSecondClm.getValue(),
                        replaceSecondClm.getValue(), replaceThirdClm.getValue());
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

    @UiField
    RadioButton searchFirstClm;

    @UiField
    RadioButton searchSecondClm;

    @UiField
    RadioButton replaceSecondClm;

    @UiField
    RadioButton replaceThirdClm;
}
