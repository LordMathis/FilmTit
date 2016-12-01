/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.dialogs;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.callables.GetShareId;
import cz.filmtit.client.callables.LoadDocumentSettings;
import cz.filmtit.client.callables.SaveSettings;
import cz.filmtit.share.Document;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.User;
import org.vectomatic.file.File;
import org.vectomatic.file.FileUploadExt;

/**
 *
 * @author matus
 */
public class SettingsDialog extends Dialog {

    private Document doc;
    private User user;

    private static SettingsDialogUiBinder uiBinder = GWT.create(SettingsDialogUiBinder.class);

    /**
     * @return the shareIdBox
     */
    public TextBox getShareIdBox() {
        return shareIdBox;
    }

    /**
     * @param shareIdBox the shareIdBox to set
     */
    public void setShareIdBox(TextBox shareIdBox) {
        this.shareIdBox = shareIdBox;
    }

    /**
     * @return the setPostedit
     */
    public CheckBox getSetPostedit() {
        return setPostedit;
    }

    /**
     * @param setPostedit the setPostedit to set
     */
    public void setSetPostedit(CheckBox setPostedit) {
        this.setPostedit = setPostedit;
    }

    interface SettingsDialogUiBinder extends UiBinder<Widget, SettingsDialog> {
    }

    public SettingsDialog(User user, Document doc) {
        initWidget(uiBinder.createAndBindUi(this));
        
        this.doc = doc;
        this.user = user;

        shareIdBox.setReadOnly(true);
        setEnabled(false);

        new GetShareId(this.doc, this);
        new LoadDocumentSettings(this, this.doc);

    }

    public SettingsDialog() {
        // do nothing
    }

    @UiField
    TextBox shareIdBox;

    @UiField
    CheckBox setPostedit;

    @UiField
    TextBox ytURL;

    @UiField
    FileUploadExt fileUpload;

    @UiField
    Button btnCancel;

    @UiHandler("btnCancel")
    void onClick(ClickEvent e) {
        this.close();
    }

    @UiField
    SubmitButton btnSave;

    @UiHandler("btnSave")
    void confirmSettings(ClickEvent e) {

        String moviePath = "";
        String remoteURL = ytURL.getValue();
        File localFile = fileUpload.getFiles().getItem(0);

        if (!remoteURL.isEmpty()) {
            moviePath = remoteURL;
        } else if (localFile != null) {
            moviePath = localFile.createObjectURL();
        }

        Boolean posteditOn = getSetPostedit().getValue();

        new SaveSettings(user, doc, moviePath, posteditOn, this);

        this.close();
    }

    public void setEnabled(boolean b) {

        Gui.log(LevelLogEnum.Error, "SettingsDialog.setEnabled", String.valueOf(b));

        shareIdBox.setEnabled(b);
        setPostedit.setEnabled(b);
        ytURL.setEnabled(b);
        fileUpload.setEnabled(b);
        btnSave.setEnabled(b);
    }

}
