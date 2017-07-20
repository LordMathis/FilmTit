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
package cz.filmtit.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.dialogs.AddSubtitleItemDialog;
import cz.filmtit.client.dialogs.LoadPreviousVersionsDialog;
import cz.filmtit.client.dialogs.SearchAndReplaceDialog;
import cz.filmtit.client.pages.TranslationWorkspace;

/**
 *
 * @author Matúš Námešný
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
        searchReplaceBtn.addStyleName("btn btn-primary");
        buttonPanel.addStyleName("text-center");
        searchReplaceBtn.setText("Search & Replace");
    }

    @UiField
    Button addSubtitleItemBtn;

    @UiField
    Button previousVersionsBtn;

    @UiField
    Button searchReplaceBtn;

    @UiHandler("addSubtitleItemBtn")
    void click(ClickEvent e) {
        new AddSubtitleItemDialog(workspace);
    }

    @UiHandler("previousVersionsBtn")
    void getPrevVersionsDialog(ClickEvent e) {
        new LoadPreviousVersionsDialog(workspace);
    }

    @UiHandler("searchReplaceBtn")
    void searchAndReplace(ClickEvent e) {
        new SearchAndReplaceDialog(workspace);
    }

    @UiField
    HTMLPanel buttonPanel;

}
