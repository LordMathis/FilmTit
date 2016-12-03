package cz.filmtit.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.pages.Blank;
import cz.filmtit.client.pages.TranslationWorkspace;
import open.pandurang.gwt.youtube.client.ApiReadyEvent;
import open.pandurang.gwt.youtube.client.ApiReadyEventHandler;
import open.pandurang.gwt.youtube.client.PlayerConfiguration;
import open.pandurang.gwt.youtube.client.YouTubePlayer;
import org.vectomatic.file.FileUploadExt;

/**
 *
 * @author Matus Namesny
 */
public class VideoWidget extends Composite {
    
    private static VideoWidgetUiBinder uiBinder = GWT.create(VideoWidgetUiBinder.class);

    interface VideoWidgetUiBinder extends UiBinder<Widget, VideoWidget> {
    }


    YouTubePlayer player;
    
    public VideoWidget(final String src) {
        
        initWidget(uiBinder.createAndBindUi(this));
        
        YouTubePlayer.loadYouTubeIframeApi();
        YouTubePlayer.addApiReadyHandler(new ApiReadyEventHandler() {
            @Override
            public void onApiReady(ApiReadyEvent event) {
                
                
                
                PlayerConfiguration config = (PlayerConfiguration) PlayerConfiguration.createObject();
                config.setVideoId(src);
                config.setWidth("400");
                config.setHeight("260");
                
                player = new YouTubePlayer(config);
                
                videoWrapper.add(player);
            }
        });
    }
    

    @UiField
    HorizontalPanel videoWrapper;

}
