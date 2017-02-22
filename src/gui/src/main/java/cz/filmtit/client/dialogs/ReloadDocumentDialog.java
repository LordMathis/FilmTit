/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author matus
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
