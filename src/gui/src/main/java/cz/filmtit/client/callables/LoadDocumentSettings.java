/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import cz.filmtit.client.Callable;
import cz.filmtit.client.Gui;
import cz.filmtit.client.dialogs.SettingsDialog;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.Document;
import cz.filmtit.share.DocumentUserSettings;
import cz.filmtit.share.LevelLogEnum;

/**
 *
 * @author matus
 */
public class LoadDocumentSettings extends Callable<DocumentUserSettings> {

    SettingsDialog settingsDialog;
    TranslationWorkspace workspace;
    Document doc;

    public LoadDocumentSettings(SettingsDialog settingsDialog, Document doc) {
        this.settingsDialog = settingsDialog;
        this.doc = doc;
        enqueue();
    }

    public LoadDocumentSettings(TranslationWorkspace workspace, Document doc) {
        this.workspace = workspace;
        this.doc = doc;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(DocumentUserSettings result) {

        Integer maxChar = result.getMaxNumChar() != null ? result.getMaxNumChar() : 42;
        Gui.log(LevelLogEnum.Error, this.toString(), maxChar.toString());

        if (settingsDialog != null) {
            settingsDialog.getSetPostedit().setValue(result.getPosteditOn());

            if (!result.isLocalFile() && !result.getMoviePath().isEmpty()) {

                settingsDialog.getYtURL().setValue("https://www.youtube.com/watch?v=" + result.getMoviePath());
            }
            settingsDialog.getMaxChar().setValue(String.valueOf(maxChar));

            settingsDialog.setEnabled(true);

        } else if (workspace != null) {
            workspace.setPosteditOn(result.getPosteditOn());
            workspace.setMoviePath(result.getMoviePath());
            workspace.setIsLocalFile(result.isLocalFile());
            workspace.setMaxNumChar(maxChar);

        }
    }

    @Override
    protected void call() {
        filmTitService.loadDocumentSettings(Gui.getSessionID(), doc, this);
    }

}
