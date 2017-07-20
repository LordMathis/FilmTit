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
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.YoutubeUrlParser;
import cz.filmtit.client.callables.GetShareId;
import cz.filmtit.client.callables.LoadDocumentSettings;
import cz.filmtit.client.callables.SaveSettings;
import cz.filmtit.share.Document;
import cz.filmtit.share.User;
import org.vectomatic.file.File;
import org.vectomatic.file.FileUploadExt;

/**
 *
 * @author Matúš Námešný
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
     * @return the posteditOn
     */
    public CheckBox getSetPostedit() {
        return posteditOn;
    }

    /**
     * @param setPostedit the posteditOn to set
     */
    public void setSetPostedit(CheckBox setPostedit) {
        this.posteditOn = setPostedit;
    }

    /**
     * @return the ytURL
     */
    public TextBox getYtURL() {
        return ytURL;
    }

    /**
     * @param ytURL the ytURL to set
     */
    public void setYtURL(TextBox ytURL) {
        this.ytURL = ytURL;
    }

    /**
     * @return the fileUpload
     */
    public FileUploadExt getFileUpload() {
        return fileUpload;
    }

    /**
     * @param fileUpload the fileUpload to set
     */
    public void setFileUpload(FileUploadExt fileUpload) {
        this.fileUpload = fileUpload;
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
    CheckBox posteditOn;

    @UiField
    CheckBox autoplay;

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
    Button btnClear;

    @UiHandler("btnClear")
    void clearText(ClickEvent e) {
        ytURL.setText("");
    }

    @UiField
    SubmitButton btnSave;

    @UiHandler("btnSave")
    void confirmSettings(ClickEvent e) {

        Boolean isLocalFile = false;
        String moviePath = "";
        String remoteURL = getYtURL().getValue();
        File localFile = getFileUpload().getFiles().getItem(0);

        if (!remoteURL.isEmpty()) {
            moviePath = YoutubeUrlParser.parse(remoteURL);
            if (moviePath == null) {
                Window.alert("The URL you have entered is not valid");
                return;
            }
            isLocalFile = false;

        } else if (localFile != null) {
            moviePath = localFile.createObjectURL();
            isLocalFile = true;
        }

        Boolean posteditOn = getSetPostedit().getValue();
        Boolean autoplayOn = getAutoplay().getValue();

        new SaveSettings(user, doc, moviePath, posteditOn, isLocalFile, autoplayOn, this);

        this.close();
    }

    public void setEnabled(boolean b) {
        shareIdBox.setEnabled(b);
        posteditOn.setEnabled(b);
        getYtURL().setEnabled(b);
        getFileUpload().setEnabled(b);
        btnSave.setEnabled(b);
    }

    /**
     * @return the autoplay
     */
    public CheckBox getAutoplay() {
        return autoplay;
    }

    /**
     * @param autoplay the autoplay to set
     */
    public void setAutoplay(CheckBox autoplay) {
        this.autoplay = autoplay;
    }

}
