/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.callables;

import static cz.filmtit.client.Callable.filmTitService;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.Settings;

/**
 *
 * @author matus
 */
public class SetPostedit extends SetSetting<Boolean> {

    public SetPostedit(Boolean setting, Settings settingsPage) {
        super(setting, settingsPage);
    }

    @Override
    protected void call() {
        filmTitService.setPostedit(Gui.getSessionID(), setting, this);
    }

}
