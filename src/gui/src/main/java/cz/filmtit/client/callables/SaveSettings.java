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
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.User;

/**
 *
 * @author Matus Namesny
 */
public class SaveSettings extends Callable<Void>{
    
    private User user;
    private Document doc;
    private String moviePath;
    private Boolean posteditOn;
    private Boolean localFile;
    private Boolean autoplay;
    private SettingsDialog settingsDialog;
    private Integer maxChar;
    
    public SaveSettings(User user, Document doc, String moviePath, Boolean postediOn, Boolean localFile, Boolean autoplay, SettingsDialog settingsDialog) {
        this.user = user;
        this.doc = doc;
        this.moviePath = moviePath;
        this.posteditOn = postediOn;
        this.localFile = localFile;
        this.settingsDialog = settingsDialog;
        this.autoplay = autoplay;
        enqueue();
    }
    
    @Override
    public void onSuccessAfterLog(Void result) {
        settingsDialog.close();
    }
    
    @Override
    public void onFinalError(String message) {
        super.onFinalError("ERROR: Couldn't save settings \n" + message);
    }

    @Override
    protected void call() {
        filmTitService.saveSettings(Gui.getSessionID(), doc, moviePath, posteditOn, localFile, autoplay, this);
    }
    
}
