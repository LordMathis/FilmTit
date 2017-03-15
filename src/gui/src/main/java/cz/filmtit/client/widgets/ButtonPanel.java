/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.dialogs.AddSubtitleItemDialog;
import cz.filmtit.client.dialogs.LoadPreviousVersionsDialog;
import cz.filmtit.client.pages.TranslationWorkspace;

/**
 *
 * @author matus
 */
public class ButtonPanel extends Composite {
    
    TranslationWorkspace workspace;
    
    private static ButtonPanelUiBinder uiBinder = GWT.create(ButtonPanelUiBinder.class);
    
    interface ButtonPanelUiBinder extends UiBinder<Widget, ButtonPanel> {
    }
    
    public ButtonPanel(TranslationWorkspace workspace) {
        initWidget(uiBinder.createAndBindUi(this));
        this.workspace = workspace;
        addSubtitleItemBtn.addStyleName("btn btn-primary");
        previousVersionsBtn.addStyleName("btn btn-primary");
    }
    
    @UiField
    Button addSubtitleItemBtn; 
    
    @UiField
    Button previousVersionsBtn;
    
    @UiHandler("addSubtitleItemBtn")
    void click(ClickEvent e) {
        new AddSubtitleItemDialog(workspace);
    }
    
    @UiHandler("previousVersionsBtn")
    void getPrevVersionsDialog(ClickEvent e) {
        new LoadPreviousVersionsDialog(workspace);
    }
}
