package cz.filmtit.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import open.pandurang.gwt.youtube.client.ApiReadyEvent;
import open.pandurang.gwt.youtube.client.ApiReadyEventHandler;
import open.pandurang.gwt.youtube.client.PlayerConfiguration;
import open.pandurang.gwt.youtube.client.YouTubePlayer;

/**
 *
 * @author Matus Namesny
 */
public class YoutubeVideoWidget extends Composite implements VideoWidget {

    private static YoutubeVideoWidgetUiBinder uiBinder = GWT.create(YoutubeVideoWidgetUiBinder.class);

    interface YoutubeVideoWidgetUiBinder extends UiBinder<Widget, YoutubeVideoWidget> {
    }

    /**
     *
     */
    YouTubePlayer player;

    /**
     * 
     */
    Label leftLabel;

    /**
     * 
     */
    Label rightLabel;

    public YoutubeVideoWidget(final String src) {

        initWidget(uiBinder.createAndBindUi(this));

        leftLabel = new Label("Left Label");
        leftLabel.setWidth("292px");
        leftLabel.setHeight("100%");

        rightLabel = new Label("Right Label");
        rightLabel.setWidth("292px");
        rightLabel.setHeight("100%");

        YouTubePlayer.loadYouTubeIframeApi();
        YouTubePlayer.addApiReadyHandler(new ApiReadyEventHandler() {
            @Override
            public void onApiReady(ApiReadyEvent event) {

                PlayerConfiguration config = (PlayerConfiguration) PlayerConfiguration.createObject();
                config.setVideoId(src);
                config.setWidth("400");
                config.setHeight("260");

                player = new YouTubePlayer(config);

                videoWrapper.add(leftLabel);
                videoWrapper.add(player);
                videoWrapper.add(rightLabel);
            }
        });
    }
    
    

    @UiField
    HorizontalPanel videoWrapper;

}
