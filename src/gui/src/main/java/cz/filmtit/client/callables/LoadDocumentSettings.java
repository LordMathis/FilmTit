/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.dialogs.SettingsDialog;
import cz.filmtit.share.Document;
import cz.filmtit.share.DocumentUserSettings;

/**
 *
 * @author matus
 */
public class LoadDocumentSettings extends Callable<DocumentUserSettings> {

    SettingsDialog settingsDialog;
    Document doc;

    public LoadDocumentSettings(SettingsDialog settingsDialog, Document doc) {
        this.settingsDialog = settingsDialog;
        this.doc = doc;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(DocumentUserSettings result) {
                
        if (result.getPosteditOn() != null) {
            settingsDialog.getSetPostedit().setValue(result.getPosteditOn());
        }
        settingsDialog.setEnabled(true);
    }

    @Override
    protected void call() {
        filmTitService.loadDocumentSettings(Gui.getSessionID(), doc, this);
    }

}
