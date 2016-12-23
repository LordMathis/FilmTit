/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import cz.filmtit.client.Callable;
import static cz.filmtit.client.Callable.filmTitService;
import cz.filmtit.client.Gui;
import cz.filmtit.client.dialogs.SettingsDialog;
import cz.filmtit.share.Document;

/**
 *
 * @author matus
 */
public class GetShareId extends Callable<String> {

    private SettingsDialog shareDialog;
    private Document doc;

    public GetShareId(Document doc, SettingsDialog shareDialog) {
        super();
        this.shareDialog = shareDialog;
        this.doc = doc;
        enqueue();
    }

    @Override
    public void onSuccessAfterLog(String result) {
        shareDialog.getShareIdBox().setText(result);
    }

    @Override
    protected void call() {
        filmTitService.getShareId(Gui.getSessionID(), doc, this);
    }

}
