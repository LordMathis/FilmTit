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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.SubtitleSynchronizer;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.ChunkStringGenerator;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.TranslationResult;
import java.util.Collection;
import java.util.HashSet;
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
 * @author Matúš Námešný
 */
public class YoutubeVideoWidget extends Composite implements VideoWidget {

    private static YoutubeVideoWidgetUiBinder uiBinder = GWT.create(YoutubeVideoWidgetUiBinder.class);

    interface YoutubeVideoWidgetUiBinder extends UiBinder<Widget, YoutubeVideoWidget> {
    }

    /**
     * Youtube player
     */
    YouTubePlayer player;

    /**
     * displays source language subtitles
     */
    Label leftLabel;

    /**
     * displays target language subtitles
     */
    Label rightLabel;

    /**
     * Time of a video currently playing
     */
    float currentTime;

    /**
     * timer that handles changing subtitles in labels
     */
    Timer timer;

    /**
     * holds the currently displayed subtitle chunks
     */
    SubtitleSynchronizer synchronizer;

    /**
     * holds currently loaded subtitle chunks
     */
    Collection<TranslationResult> currentLoaded;

    ButtonPanel buttonPanel;

    Boolean autoplay;

    /**
     * wrapper panel which holds the ui
     */
    @UiField
    HorizontalPanel videoWrapper;

    @UiField
    VerticalPanel panelWrapper;

    /**
     * Creates Youtube video player widget
     *
     * @param src Youtube video id
     */
    public YoutubeVideoWidget(final String src, SubtitleSynchronizer synchronizer, Boolean autoplay) {

        initWidget(uiBinder.createAndBindUi(this));

        if (src != null && !src.isEmpty()) {

            leftLabel = new Label();
            leftLabel.setWidth("292px");
            leftLabel.setHeight("100%");
            leftLabel.addStyleName("subtitleDisplayedLeft");

            rightLabel = new Label();
            rightLabel.setWidth("292px");
            rightLabel.setHeight("100%");
            rightLabel.addStyleName("subtitleDisplayedRight");

            this.autoplay = autoplay;

            this.synchronizer = synchronizer;

            currentTime = 0;
            currentLoaded = new HashSet<TranslationResult>();

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

        buttonPanel = new ButtonPanel(TranslationWorkspace.getCurrentWorkspace());
        panelWrapper.add(buttonPanel);
    }

    /**
     * plays video at given time
     *
     * @param position positions at which to play video
     */
    @Override
    public void playPart(int start, final int end) {
        if (player != null) {

            player.getPlayer().seekTo(start - 1, true);

            if (autoplay != null && autoplay) {

                player.getPlayer().playVideo();

                new Timer() {
                    @Override
                    public void run() {
                        if ((currentTime / 1000) > end + 1) {
                            player.getPlayer().pauseVideo();
                            this.cancel();
                        }
                    }
                }.scheduleRepeating(1000);

            }
        }
    }

    /**
     * updates subtitle labels when the video is playing
     */
    private void updateLabels() {

        float newTime = player.getPlayer().getCurrentTime() * 1000;
        if (newTime != currentTime) {

            cleanList(currentTime);
            Collection<TranslationResult> translationResultsByTime = synchronizer.getTranslationResultsByTime(currentTime, newTime + 1000);
            if (translationResultsByTime != null) {
                currentLoaded.addAll(translationResultsByTime);
            }

            List<TranslationResult> correct = getCorrect(currentLoaded, newTime);

            String source = "";
            String target = "";

            if (correct != null) {
                source = ChunkStringGenerator.listWithSameTimeToString(correct, ChunkStringGenerator.SOURCE_SIDE);
                target = ChunkStringGenerator.listWithSameTimeToString(correct, ChunkStringGenerator.TARGET_SIDE);

            }

            leftLabel.setText(source);
            rightLabel.setText(target);

            currentTime = newTime;
        }
    }

    /**
     * gets correct subtitle chunks at given time
     *
     * @param subset subset of subtitle chunks to get correct from
     * @param time time of subtitles
     * @return
     */
    public List<TranslationResult> getCorrect(Collection<TranslationResult> subset, double time) {
        //subset should be already sorted by starting times
        List<TranslationResult> res = null;
        Long correctTime = null;

        for (TranslationResult tr : subset) {

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

        return res;
    }

    /**
     * removes subtitle chunks with wrong time from currentLoaded
     *
     * @param currentTime
     */
    private void cleanList(float currentTime) {

        if (currentLoaded == null) {
            return;
        }

        for (TranslationResult translationResult : currentLoaded) {
            if (translationResult.getSourceChunk().getEndTimeLong() < currentTime) {
                currentLoaded.remove(translationResult);
            }
        }
    }

}
