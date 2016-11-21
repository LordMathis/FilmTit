/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.dialogs;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.callables.GetShareId;
import cz.filmtit.client.callables.SetPostedit;
import cz.filmtit.share.Document;
import cz.filmtit.share.LevelLogEnum;

/**
 *
 * @author matus
 */
public class SettingsDialog extends Dialog {

    private Document doc;

    private static SettingsDialogUiBinder uiBinder = GWT.create(SettingsDialogUiBinder.class);

    interface SettingsDialogUiBinder extends UiBinder<Widget, SettingsDialog> {
    }

    public SettingsDialog(Document doc) {
        super();
        initWidget(uiBinder.createAndBindUi(this));

        this.doc = doc;

        if (doc == null) {
            shareIdBox.setText("null");
        } else {
            shareIdBox.setText("Not null");
        }
        
        shareIdBox.setReadOnly(true);        
        new GetShareId(this.doc, this);
        
        
    }

    public SettingsDialog() {
        // nothing
    }

    @UiField
    public TextBox shareIdBox;
    
    @UiField
    public TextBox ytURL;
        
    @UiField
    public Button btnLocalFile;
    
    @UiField
    public Button btnConfirm;
    
    @UiHandler("btnConfirm")
    void confirmSettings(ClickEvent e) {
        //new SetPostedit(doc, posteditCheckBox.getValue());
        this.close();
    }

}
