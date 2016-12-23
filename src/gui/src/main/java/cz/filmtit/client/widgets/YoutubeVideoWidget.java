package cz.filmtit.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.SubtitleSynchronizer;
import cz.filmtit.share.ChunkStringGenerator;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.TranslationResult;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import open.pandurang.gwt.youtube.client.ApiReadyEvent;
import open.pandurang.gwt.youtube.client.ApiReadyEventHandler;
import open.pandurang.gwt.youtube.client.PlayerConfiguration;
import open.pandurang.gwt.youtube.client.StateChangeEvent;
import open.pandurang.gwt.youtube.client.StateChangeEventHandler;
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

    /**
     *
     */
    float currentTime;

    /**
     *
     */
    Timer timer;

    /**
     *
     */
    SubtitleSynchronizer synchronizer;

    /**
     *
     */
    Collection<TranslationResult> currentLoaded;

    /**
     *
     * @param src
     */
    public YoutubeVideoWidget(final String src, SubtitleSynchronizer synchronizer) {

        initWidget(uiBinder.createAndBindUi(this));

        leftLabel = new Label("Left Label");
        leftLabel.setWidth("292px");
        leftLabel.setHeight("100%");

        rightLabel = new Label("Right Label");
        rightLabel.setWidth("292px");
        rightLabel.setHeight("100%");

        this.synchronizer = synchronizer;

        timer = new Timer() {
            @Override
            public void run() {
                updateLabels();
            }
        };

        YouTubePlayer.loadYouTubeIframeApi();
        YouTubePlayer.addApiReadyHandler(new ApiReadyEventHandler() {
            @Override
            public void onApiReady(ApiReadyEvent event) {

                PlayerConfiguration config = (PlayerConfiguration) PlayerConfiguration.createObject();
                config.setVideoId(src);
                config.setWidth("400");
                config.setHeight("260");

                player = new YouTubePlayer(config);

                player.addStateChangedHandler(new StateChangeEventHandler() {
                    @Override
                    public void onStateChange(StateChangeEvent event) {
                        int stateChangeValue = event.getPlayerEvent().getData();
                        if (stateChangeValue == 1) {
                            timer.scheduleRepeating(100);
                        } else {
                            timer.cancel();
                        }
                    }
                });

                videoWrapper.add(leftLabel);
                videoWrapper.add(player);
                videoWrapper.add(rightLabel);
            }
        });
    }

    @Override
    public void playPart(int position) {
        player.getPlayer().seekTo(position - 1, true);
        player.getPlayer().playVideo();
    }

    private void updateLabels() {

        float newTime = player.getPlayer().getCurrentTime() * 1000;
        if (newTime != currentTime) {

            currentLoaded = synchronizer.getTranslationResultsByTime(currentTime, newTime + 1000);

            //Gui.log(LevelLogEnum.Error, "YoutubeVideoWidget.updateLabels()", currentLoaded.toString());
            List<TranslationResult> correct = getCorrect(currentLoaded, newTime);

            String source = null;
            String target = null;

            if (correct != null) {
                source = ChunkStringGenerator.listWithSameTimeToString(correct, ChunkStringGenerator.SOURCE_SIDE);
                target = ChunkStringGenerator.listWithSameTimeToString(correct, ChunkStringGenerator.TARGET_SIDE);
            }

            if (source != null && !source.isEmpty()) {
                leftLabel.setText(source);
            }
            if (target != null && !target.isEmpty()) {
                rightLabel.setText(target);
            }
            currentTime = newTime;
        }
    }

    public List<TranslationResult> getCorrect(Collection<TranslationResult> subset, double time) {
        //subset should be already sorted by starting times
        List<TranslationResult> res = null;
        Long correctTime = null;

        //Gui.log(LevelLogEnum.Error, "YoutubeVideoWidget.getCorrect1()", subset.toString());
        for (TranslationResult tr : subset) {
            if (tr == null) {
                Gui.log(LevelLogEnum.Error, "YoutubeVideoWidget", "TranslationResult null!");
            }

            long start = (tr.getSourceChunk().getStartTimeLong());
            long end = (tr.getSourceChunk().getEndTimeLong());
            if (start > time) {
                return res;
            }
            if (end > time) {
                if (correctTime == null) {
                    correctTime = start;
                    res = new LinkedList();
                    res.add(tr);
                } else if (correctTime != start) {
                    return res;
                } else {
                    res.add(tr);
                }
            }
        }

        //Gui.log(LevelLogEnum.Error, "YoutubeVideoWidget.getCorrect2()", r);
        return res;
    }

    @UiField
    HorizontalPanel videoWrapper;

}
