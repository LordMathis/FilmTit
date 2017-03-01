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

/**
 *
 * @author matus
 */
public class FileVideoWidget extends Composite implements VideoWidget {

    Video player;

    Label leftLabel;

    Label rightLabel;

    SubtitleSynchronizer synchronizer;

    float currentTime;

    Collection<TranslationResult> currentLoaded;

    Timer timer;

    ButtonPanel buttonPanel;

    private static FileVideoWidgetUiBinder uiBinder = GWT.create(FileVideoWidgetUiBinder.class);

    interface FileVideoWidgetUiBinder extends UiBinder<Widget, FileVideoWidget> {
    }

    public FileVideoWidget(String src, SubtitleSynchronizer synchronizer) {
        initWidget(uiBinder.createAndBindUi(this));

        if (src != null && !src.isEmpty()) {

            player = Video.createIfSupported();
            player.setWidth("400px");
            player.setHeight("260px");
            player.addSource(src);
            player.setControls(true);
            player.load();

            this.synchronizer = synchronizer;

            currentTime = 0;
            currentLoaded = new HashSet<TranslationResult>();

            timer = new Timer() {
                @Override
                public void run() {
                    if (!player.isPaused() && !player.hasEnded() && (player.getCurrentTime() > 0)) {
                        updateLabels();
                    }
                }
            };

            timer.scheduleRepeating(100);

            leftLabel = new Label("Left Label");
            leftLabel.setWidth("292px");
            leftLabel.setHeight("100%");
            leftLabel.addStyleName("subtitleDisplayedLeft");

            rightLabel = new Label("Right Label");
            rightLabel.setWidth("292px");
            rightLabel.setHeight("100%");
            rightLabel.addStyleName("subtitleDisplayedRight");

            videoWrapper.add(leftLabel);
            videoWrapper.add(player);
            videoWrapper.add(rightLabel);

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
    public void playPart(int position) {
        if (player != null) {
            player.setCurrentTime(position);
            player.play();
        }
    }

    /**
     * updates subtitle labels when the video is playing
     */
    private void updateLabels() {

        float newTime = (float) (player.getCurrentTime() * 1000);
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

    @UiField
    HorizontalPanel videoWrapper;

    @UiField
    VerticalPanel panelWrapper;

}
