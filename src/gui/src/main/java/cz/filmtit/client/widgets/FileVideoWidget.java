/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.media.client.Video;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author matus
 */
public class FileVideoWidget extends Composite implements VideoWidget {

    private static FileVideoWidgetUiBinder uiBinder = GWT.create(FileVideoWidgetUiBinder.class);

    interface FileVideoWidgetUiBinder extends UiBinder<Widget, FileVideoWidget> {
    }
    
    Video player; 

    public FileVideoWidget(String src) {
        initWidget(uiBinder.createAndBindUi(this));

        player = Video.createIfSupported();
        player.setWidth("400px");
        player.setHeight("260px");
        player.addSource(src);
        player.setControls(true);
        player.load();
        
        videoWrapper.add(player);
    }

    @UiField
    HorizontalPanel videoWrapper;
}
