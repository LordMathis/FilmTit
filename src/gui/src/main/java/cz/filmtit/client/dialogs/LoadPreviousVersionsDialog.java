/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.dialogs;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBoxAppended;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.callables.LoadPreviousVersions;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.TranslationResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author matus
 */
public class LoadPreviousVersionsDialog extends Dialog {
    
    private static LoadPreviousVersionsDialogUiBinder uiBinder = GWT.create(LoadPreviousVersionsDialogUiBinder.class);
    
    interface LoadPreviousVersionsDialogUiBinder extends UiBinder<Widget, LoadPreviousVersionsDialog> {
    }
    
    public LoadPreviousVersionsDialog(TranslationWorkspace workspace) {
        initWidget(uiBinder.createAndBindUi(this));
        this.workspace = workspace;
    }
    
    TranslationWorkspace workspace;
    
    @UiField
    Form dateForm;
    
    @UiField
    DateTimeBoxAppended dateTimeBox;
    
    @UiField
    Button cancelButton;

    @UiHandler("cancelButton")
    void cancel(ClickEvent e) {
        close();
    }

    @UiField
    SubmitButton submitButton;

    @UiHandler("dateForm")
    void submit(Form.SubmitEvent e) {
        
        List<TranslationResult> results = workspace.getCurrentDocument().getSortedTranslationResults();
        Date date = dateTimeBox.getValue();
        
        new LoadPreviousVersions(results, date, workspace);
        
        close();
    }
}
