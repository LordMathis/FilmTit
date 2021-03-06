/*Copyright 2012 FilmTit authors - Karel Bílek, Josef Čech, Joachim Daiber, Jindřich Libovický, Rudolf Rosa, Jan Václ
Copyright 2017 Matúš Námešný

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
package cz.filmtit.client.pages;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import cz.filmtit.client.Gui;
import cz.filmtit.client.PageHandler.Page;
import cz.filmtit.client.SubtitleSynchronizer;
import cz.filmtit.client.callables.*;
import cz.filmtit.client.dialogs.TimeEditDialog;
import cz.filmtit.client.subgestbox.PosteditBox;
import cz.filmtit.client.subgestbox.PosteditHandler;
import cz.filmtit.client.subgestbox.SubgestBox;
import cz.filmtit.client.subgestbox.SubgestHandler;
import cz.filmtit.client.widgets.FileVideoWidget;
import cz.filmtit.client.widgets.YoutubeVideoWidget;
import cz.filmtit.share.*;
import cz.filmtit.share.parsing.Parser;
import java.util.*;

/**
 * The main page of the application where the actual translations take place.
 *
 * @author rur, Matúš Námešný
 *
 */
public class TranslationWorkspace extends Composite {

    // Variable definitions
    /**
     * GWT UiBinder for TranslationWorkspace
     */
    private static TranslationWorkspaceUiBinder uiBinder = GWT.create(TranslationWorkspaceUiBinder.class);

    /**
     * Document currently opened in this TranslationWorkspace
     */
    private Document currentDocument;

    /**
     * True if postedit API is on
     */
    private boolean posteditOn;

    /**
     * the currently active workspace
     */
    private static TranslationWorkspace currentWorkspace;

    /**
     * Currently locked SubgestBox
     */
    private SubgestBox lockedSubgestBox;

    /**
     * SubgestBox that was previously locked but is now unlocked
     */
    private SubgestBox unlockedSubgestBox;

    /**
     * Timer that automatically unlocks subgestBox after 60s of inactivity
     */
    private com.google.gwt.user.client.Timer subgestBoxTimer;

    /**
     * Handles events for all {@link SubgestBox} instances in this workspace.
     */
    public SubgestHandler subgestHandler;

    /**
     *
     */
    public PosteditHandler posteditHandler;

    /**
     * List of Fake SubgestBoxes which are more lightweight and will be replaced
     * by real SubgestBoxes
     */
    private List<SubgestBox.FakeSubgestBox> targetBoxes;

    /**
     * List of Fake PosteditBoxes which are more lightweight and will be
     * replaced by real PosteditBoxes
     */
    private List<PosteditBox.FakePosteditBox> posteditBoxes;

    /**
     * Currently active SubgestBox
     */
    private Widget activeSuggestionWidget = null;

    /**
     * Currently active PosteditBox
     */
    private Widget activePosteditWidget = null;

    /*
    * Loaded revisions of subtitle items
     */
    private Map<ChunkIndex, Number> loadedRevisions;

    /*
    * automatic video playback
     */
    private Boolean autoplay;

    /**
     * column numbers in the subtitle-table
     */
    private static final int TIMES_COLNUMBER = 0; // subtitle timing
    private static final int SOURCETEXT_COLNUMBER = 2; // source text
    private static final int TARGETBOX_COLNUMBER = 4; // translated text
    private static final int POSTEDIT_COLNUMBER = 6; // postedit column

    // DIALOGMARK indicates that two subtitle chunks will be displayed at the same time
    private static final int SOURCE_DIALOGMARK_COLNUMBER = 1;
    private static final int TARGET_DIALOGMARK_COLNUMBER = 3;
    private static final int POSTEDIT_DIALOGMARK_COLNUMBER = 5;

    // Undo Button
    private static int UNDO_BUTTON_COLNUMBER;

    /**
     * True if user selected MediaSource
     */
    private boolean sourceSelected = false;

    /**
     * Youtube Video Player Widget
     */
    private YoutubeVideoWidget ytVideoPlayer;

    /**
     * Local File Video Player Widget
     */
    private FileVideoWidget fileVideoPlayer;

    /**
     * List of RPC calls to unlock a TranslationResult For one TranslationResult
     * there can be only one call
     */
    private Map<TimedChunk, UnlockTranslationResult> unlockTranslationResultCalls = new HashMap<TimedChunk, UnlockTranslationResult>();

    /**
     * List of RPC calls to lock TranslationResult For one TranslationResult
     * there can be only one call
     */
    private Map<TimedChunk, LockTranslationResult> lockTranslationResultCalls = new HashMap<TimedChunk, LockTranslationResult>();

    private ReloadTranslationResults reloadTranslationResultsCall = null;

    /**
     * Current SendChunksCommand
     */
    private SendChunksCommand sendChunksCommand;

    /**
     * Indicates that the user moved away from this workspace and that the
     * loading of TranslationResults should be stopped
     */
    private boolean stopLoading = false;

    /**
     * True if TranslationWorkspace has started receiving Translation Result
     * from backend
     */
    private boolean translationStarted = false;

    /**
     * List of RPC calls to get TranslationResults from Translation Memory
     */
    private Map<Integer, GetTranslationResults> sentGetTranslationsResultsCalls;

    /**
     * Handles the subtitles of the current document.
     */
    SubtitleSynchronizer synchronizer;

    private Map<ChunkIndex, Label> timeLabels;

    private String moviePath;

    private Boolean isLocalFile;

    // UiBinder fields
    @UiField
    SimplePanel panelForVideo;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    SimplePanel emptyPanel;

    @UiField
    FlexTable table;

    @UiField
    HorizontalPanel translationHPanel;

    // Getters and Setters
    /**
     * @return the lockedSubgestBox
     */
    public SubgestBox getLockedSubgestBox() {
        return lockedSubgestBox;
    }

    /**
     * @param lockedSubgestBox the lockedSubgestBox to set
     */
    public void setLockedSubgestBox(SubgestBox lockedSubgestBox) {
        this.lockedSubgestBox = lockedSubgestBox;
    }

    /**
     * @return the subgestBoxTimer
     */
    public com.google.gwt.user.client.Timer getTimer() {
        return subgestBoxTimer;
    }

    /**
     * @param timer the subgestBoxTimer to set
     */
    public void setTimer(com.google.gwt.user.client.Timer timer) {
        this.subgestBoxTimer = timer;
    }

    /**
     * @return the currentDocument
     */
    public Document getCurrentDocument() {
        return currentDocument;
    }

    /**
     * @param currentDocument Document to set
     */
    private void setCurrentDocument(Document currentDocument) {
        this.currentDocument = currentDocument;
        Gui.getPageHandler().setDocumentId(currentDocument.getId());
    }

    /**
     * @return the unlockTranslationResultCalls
     */
    public Map<TimedChunk, UnlockTranslationResult> getUnlockTranslationResultCalls() {
        return unlockTranslationResultCalls;
    }

    /**
     * @return the lockTranslationResultCalls
     */
    public Map<TimedChunk, LockTranslationResult> getLockTranslationResultCalls() {
        return lockTranslationResultCalls;
    }

    public Number getLoadedRevision(ChunkIndex index) {
        return loadedRevisions.get(index);
    }

    public void addLoadedRevision(ChunkIndex index, Number number) {
        loadedRevisions.put(index, number);
    }

    /**
     * @return the unlockedSubgestBox
     */
    public SubgestBox getUnlockedSubgestBox() {
        return unlockedSubgestBox;
    }

    /**
     * @param unlockedSubgestBox the unlockedSubgestBox to set
     */
    public void setUnlockedSubgestBox(SubgestBox unlockedSubgestBox) {
        this.unlockedSubgestBox = unlockedSubgestBox;
    }

    /**
     * the currently active workspace
     */
    public static TranslationWorkspace getCurrentWorkspace() {
        return currentWorkspace;
    }

    /**
     * Stops loading of translation suggestions into this workspace.
     *
     * @param stopLoading
     */
    public void setStopLoading(boolean stopLoading) {
        for (GetTranslationResults getTranslationResults : sentGetTranslationsResultsCalls.values()) {
            getTranslationResults.stop();
        }

        this.stopLoading = true;
        Gui.log("stopLoading set for the workspace");
    }

    /**
     * Checks whether loading of translation suggestions has been stopped.
     */
    public boolean getStopLoading() {
        return stopLoading;
    }

    /**
     * Sets the subgestbox that is currently active.
     */
    public void setActiveSuggestionWidget(Widget w) {
        this.activeSuggestionWidget = w;
    }

    /**
     * Called when the user selects the media source.
     */
    public void setSourceSelectedTrue() {
        this.sourceSelected = true;
    }

    /**
     * @return the posteditOn
     */
    public boolean isPosteditOn() {
        return posteditOn;
    }

    /**
     * @param posteditOn the posteditOn to set
     */
    public void setPosteditOn(boolean posteditOn) {
        this.posteditOn = posteditOn;
    }

    /**
     * @return the moviePath
     */
    public String getMoviePath() {
        return moviePath;
    }

    /**
     * @param moviePath the moviePath to set
     */
    public void setMoviePath(String moviePath) {
        this.moviePath = moviePath;
    }

    /**
     * @return the isLocalFile
     */
    public Boolean getIsLocalFile() {
        return isLocalFile;
    }

    /**
     * @param isLocalFile the isLocalFile to set
     */
    public void setIsLocalFile(Boolean isLocalFile) {
        this.isLocalFile = isLocalFile;
    }

    /**
     * @return the ytVideoPlayer
     */
    public YoutubeVideoWidget getYtVideoPlayer() {
        return ytVideoPlayer;
    }

    /**
     * @return the fileVideoPlayer
     */
    public FileVideoWidget getFileVideoPlayer() {
        return fileVideoPlayer;
    }

    /**
     * @param activePosteditWidget the activePosteditWidget to set
     */
    public void setActivePosteditWidget(Widget activePosteditWidget) {
        this.activePosteditWidget = activePosteditWidget;
    }

    /**
     * @return the reloadTranslationResultsCall
     */
    public ReloadTranslationResults getReloadTranslationResultsCall() {
        return reloadTranslationResultsCall;
    }

    /**
     * @param reloadTranslationResultsCall the reloadTranslationResultsCall to
     * set
     */
    public void setReloadTranslationResultsCall(ReloadTranslationResults reloadTranslationResultsCall) {
        this.reloadTranslationResultsCall = reloadTranslationResultsCall;
    }

    /**
     * @return the autoplay
     */
    public Boolean getAutoplay() {
        return autoplay;
    }

    /**
     * @param autoplay the autoplay to set
     */
    public void setAutoplay(Boolean autoplay) {
        this.autoplay = autoplay;
    }

    /**
     * UiBinder Interface
     */
    interface TranslationWorkspaceUiBinder extends UiBinder<Widget, TranslationWorkspace> {
    }

    ///////////////////////////////////////
    //                                   //
    //      Initialization               //
    //                                   //
    ///////////////////////////////////////
    /**
     * Creates and shows the workspace.
     */
    public TranslationWorkspace(Document doc, DocumentOrigin documentOrigin, DocumentUserSettings userSettings) {

        initWidget(uiBinder.createAndBindUi(this));

        // Variables initialization
        posteditOn = userSettings.getPosteditOn();
        moviePath = userSettings.getMoviePath();
        isLocalFile = userSettings.isLocalFile();
        autoplay = userSettings.getAutoplay();

        synchronizer = new SubtitleSynchronizer();

        sentGetTranslationsResultsCalls = new HashMap<Integer, GetTranslationResults>();

        currentWorkspace = this;
        currentDocument = doc;

        targetBoxes = new ArrayList<SubgestBox.FakeSubgestBox>();
        posteditBoxes = new ArrayList<PosteditBox.FakePosteditBox>();
        timeLabels = new HashMap<ChunkIndex, Label>();

        loadedRevisions = new HashMap<ChunkIndex, Number>();

        // Gui initialization
        Gui.getPageHandler().setPageUrl(Page.TranslationWorkspace);
        Gui.getGuiStructure().activateMenuItem(Page.TranslationWorkspace);

        switch (documentOrigin) {
            case NEW:
                // wait for everything to load and for selectSource to return
                sourceSelected = false;
                break;
            case FROM_DB:
                // only wait for everything to load
                sourceSelected = true;
                break;
            default:
                assert false;
                break;
        }

        if (!getIsLocalFile()) {
            ytVideoPlayer = new YoutubeVideoWidget(moviePath, synchronizer, autoplay);
            panelForVideo.setWidget(ytVideoPlayer);
        } else {
            fileVideoPlayer = new FileVideoWidget(moviePath, synchronizer, autoplay);
            panelForVideo.setWidget(fileVideoPlayer);
        }

        scrollPanel.setStyleName("scrollPanel");
        // hiding the suggestion popup when scrolling the subtitle panel
        Gui.getGuiStructure().contentPanel.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {
                deactivateSuggestionWidget();
                deactivatePosteditWidget();
            }
        });

        // Translation table initialization
        table.setWidth("100%");
        translationHPanel.setCellWidth(scrollPanel, "100%");
        this.subgestHandler = new SubgestHandler(this);
        this.posteditHandler = new PosteditHandler(this);

        table.setWidget(0, TIMES_COLNUMBER, new Label("Timing"));
        table.setWidget(0, SOURCETEXT_COLNUMBER, new Label("Original"));
        table.setWidget(0, TARGETBOX_COLNUMBER, new Label("Translation"));

        table.setWidget(0, SOURCE_DIALOGMARK_COLNUMBER, new Label(""));
        table.setWidget(0, TARGET_DIALOGMARK_COLNUMBER, new Label(""));

        table.getRowFormatter().setStyleName(0, "header");

        if (isPosteditOn()) {
            UNDO_BUTTON_COLNUMBER = 7;
            table.setWidget(0, POSTEDIT_COLNUMBER, new Label("Post-edit"));
        } else {
            UNDO_BUTTON_COLNUMBER = 5;
        }

        Gui.getGuiStructure().contentPanel.setWidget(this);
        Gui.getGuiStructure().contentPanel.setStyleName("translating");
        Gui.getGuiStructure().contentPanel.addStyleName("parsing");

        /*
        * After 60s of inactivity Timer submits and unlocks TranslationResult
         */
        subgestBoxTimer = new com.google.gwt.user.client.Timer() {
            @Override
            public void run() {
                lockedSubgestBox.getTranslationResult().setUserTranslation(lockedSubgestBox.getTextWithNewlines());
                boolean posteditTextChanged = false;

                if (isPosteditOn()) {
                    lockedSubgestBox.getTranslationResult().setPosteditedString(lockedSubgestBox.getPosteditBox().getTextWithNewlines());
                    posteditTextChanged = lockedSubgestBox.getPosteditBox().textChanged();
                }

                if (lockedSubgestBox != null) {
                    lockedSubgestBox.addStyleDependentName("unlocked");
                    if (isPosteditOn()) {
                        lockedSubgestBox.getPosteditBox().addStyleDependentName("unlocked");
                    }
                }

                // submitting only when the contents have changed
                if (lockedSubgestBox.textChanged() || posteditTextChanged) {
                    submitUserTranslation(lockedSubgestBox, null, null, null);
                    lockedSubgestBox.updateLastText();
                    if (isPosteditOn()) {
                        lockedSubgestBox.getPosteditBox().updateLastText();
                    }
                } else {
                    new UnlockTranslationResult(lockedSubgestBox, currentWorkspace);
                }

                this.cancel();
            }
        };
    }

    ///////////////////////////////////////
    //                                   //
    //      Un-initialization            //
    //                                   //
    ///////////////////////////////////////
    @Override
    public void onUnload() {
        setStopLoading(true);
        sourceSelected = false;
        translationStarted = false;
        panelForVideo.setWidget(null);
        getTimer().cancel();
        Gui.getGuiStructure().contentPanel.removeStyleName("parsing");
    }

    ///////////////////////////////////////
    //                                   //
    //      Member methods               //
    //                                   //
    ///////////////////////////////////////
    public void addGetTranslationsResultsCall(int id, GetTranslationResults call) {
        sentGetTranslationsResultsCalls.put(id, call);
    }

    public void removeGetTranslationsResultsCall(int id) {
        sentGetTranslationsResultsCalls.remove(id);
    }

    /**
     * Goes through TranslationResults of an already existing document and loads
     * translation results for the untranslated ones.
     */
    public void processTranslationResultList(List<TranslationResult> translations) {

        if (stopLoading) {
            return;
        }

        List<TimedChunk> unedited = new LinkedList<TimedChunk>();
        List<TranslationResult> allResults = new LinkedList<TranslationResult>();
        List<TranslationResult> results = new LinkedList<TranslationResult>();

        for (TranslationResult tr : translations) {
            TimedChunk sourceChunk = tr.getSourceChunk();
            synchronizer.putTranslationResult(tr);
            synchronizer.putSourceChunk(sourceChunk, -1, false);
            String userTranslation = tr.getUserTranslation();
            String posteditedString = tr.getPosteditedString();

            ChunkIndex chunkIndex = sourceChunk.getChunkIndex();

            this.getCurrentDocument().translationResults.put(chunkIndex, tr);

            allResults.add(tr);

            if (userTranslation == null || userTranslation.isEmpty()) {
                unedited.add(sourceChunk);
            } else {
                results.add(tr);
                if (isPosteditOn() && ((posteditedString == null) || (posteditedString.isEmpty()))) {
                    unedited.add(sourceChunk);
                }
            }
        }

        dealWithChunks(allResults, results, unedited);

    }

    /**
     * Fills new TranslationResults received from backend
     *
     * @param translations = new translations
     */
    public void fillTranslationResults(List<TranslationResult> translations) {

        List<TranslationResult> unedited = new ArrayList<TranslationResult>();

        for (TranslationResult tr : translations) {

            TimedChunk sChunk = tr.getSourceChunk();
            ChunkIndex chunkIndex = sChunk.getChunkIndex();
            String userTranslation = tr.getUserTranslation();
            String posteditedString = tr.getPosteditedString();

            TranslationResult getTr = currentDocument.translationResults.get(chunkIndex);

            tr.setTmSuggestions(getTr.getTmSuggestions());
            tr.setPosteditSuggestions(getTr.getPosteditSuggestions());

            this.getCurrentDocument().translationResults.put(chunkIndex, tr);

            synchronizer.putTranslationResult(tr);
            synchronizer.putSourceChunk(sChunk, -1, false);

            if (userTranslation != null && !userTranslation.isEmpty()) {
                unedited.add(tr);
            } else if (posteditedString != null && !posteditedString.isEmpty()) {
                unedited.add(tr);
            }

        }

        Scheduler.get().scheduleIncremental(new ShowUserTranslatedCommand(unedited));

    }

    /**
     * Parse the given text in the subtitle format of choice (by the
     * radiobuttons) into this.chunklist (List<TimedChunk>). Currently verbosely
     * outputting both input text, format and output chunks into the debug-area,
     * also "reloads" the CellBrowser interface accordingly.
     *
     * Might return prematurely if there is a parsing error. In such case, the
     * document is deleted and the user is redirected back to DocumentCreator.
     *
     * @param subtext - multiline text (of the whole subtitle file, typically)
     * to parse
     * @param createDocumentCall reference to the call that created the document
     * and now probably holds a reference to an open MediaSelector
     */
    public void processText(String subtext, String subformat, CreateDocument createDocumentCall) {

        // choose the appropriate parser:
        Parser subtextparser;
        if (subformat == "sub") {
            subtextparser = Parser.PARSER_SUB;
        } else {
            assert subformat == "srt" : "One of the subtitle formats must be chosen.";
            subtextparser = Parser.PARSER_SRT;
        }
        Gui.log("subtitle format chosen: " + subformat);

        // parse:
        Gui.log("starting parsing");
        long startTime = System.currentTimeMillis();
        List<TimedChunk> chunklist = null;
        try {
            chunklist = subtextparser.parse(subtext, this.getCurrentDocument().getId(), Language.EN);
        } catch (Exception e) {
            // user interaction
            createDocumentCall.hideMediaSelector();
            Window.alert("There was an error parsing the subtitle file:\n" + e.getMessage());
            // logging
            Gui.log("There was an error parsing the subtitle file!");
            //Gui.exceptionCatcher(e, false);
            // action
            Gui.getPageHandler().loadPage(Page.DocumentCreator, true);
            new DeleteDocumentSilently(getCurrentDocument().getId());
            // return prematurely
            return;
        }
        long endTime = System.currentTimeMillis();
        long parsingTime = endTime - startTime;
        Gui.log("parsing finished in " + parsingTime + "ms");

        List<TranslationResult> tResults = new ArrayList<TranslationResult>();

        int order = 0;
        for (TimedChunk chunk : chunklist) {
            chunk.setOrder(order);
            order++;
            ChunkIndex chunkIndex = chunk.getChunkIndex();
            TranslationResult tr = new TranslationResult(chunk);
            tResults.add(tr);
            this.getCurrentDocument().translationResults.put(chunkIndex, tr);
            synchronizer.putTranslationResult(tr);
            synchronizer.putSourceChunk(tr, -1, false);
        }

        // save the chunks
        new SaveSourceChunks(tResults, this, createDocumentCall);
        // now the user can close the browser, chunks are safely saved
    }

    /**
     * Creates the SendChunksCommand and, if possible, executes it
     *
     * @param chunklist
     */
    private void prepareSendChunkCommand(List<TimedChunk> chunklist) {
        sendChunksCommand = new SendChunksCommand(chunklist);
    }

    /**
     * Starts requesting trabslation suggestions if not waiting for anything.
     */
    public void startShowingTranslationsIfReady() {
        if (sourceSelected) {
            if (sendChunksCommand != null) {
                if (!translationStarted) {
                    sendChunksCommand.execute();
                    translationStarted = true;
                }
            }
        }
    }

    /**
     * Send the given translation result as a "user-feedback" to the userspace
     *
     * @param transresult
     * @param toSaveAndUnlock
     * @param toLock
     */
    public void submitUserTranslation(SubgestBox toSaveAndUnlock, SubgestBox toLock, SourceChangeHandler sourceChangeHandler, TimeChangeHandler timeChangeHandler) {
        TranslationResult transResult = toSaveAndUnlock.getTranslationResult();
        String combinedTRId = transResult.getDocumentId() + ":" + transResult.getSourceChunk().getChunkIndex();
        Gui.log("sending user feedback with values: " + combinedTRId + ", " + transResult.getUserTranslation() + ", " + transResult.getSelectedTranslationPairID());

        ChunkIndex chunkIndex = transResult.getSourceChunk().getChunkIndex();

        // TODO add setUserTranslation for TimeChangeHandler and SourceChangeHandler
        if (toLock != null) {
            new SetUserTranslation(chunkIndex, transResult.getDocumentId(),
                    transResult.getUserTranslation(), transResult.getSelectedTranslationPairID(),
                    lockedSubgestBox, this, toLock, transResult.getPosteditedString(), transResult.getSelectedPosteditPairID(),
                    sourceChangeHandler, timeChangeHandler);
        } else {
            new SetUserTranslation(chunkIndex, transResult.getDocumentId(),
                    transResult.getUserTranslation(), transResult.getSelectedTranslationPairID(),
                    lockedSubgestBox, this, transResult.getPosteditedString(), transResult.getSelectedPosteditPairID(),
                    sourceChangeHandler, timeChangeHandler);
        }

        synchronizer.putTranslationResult(transResult);
    }

    private void dealWithChunks(List<TranslationResult> original, List<TranslationResult> translated, List<TimedChunk> unedited) {

        Scheduler.get().scheduleIncremental(new ShowOriginalCommand(original));
        Scheduler.get().scheduleIncremental(new ShowUserTranslatedCommand(translated));

        prepareSendChunkCommand(unedited);
        startShowingTranslationsIfReady();
    }

    /**
     * Shows the source chunks.
     *
     * @param chunks
     */
    public void showSources(List<TranslationResult> results) {
        List<TimedChunk> chunks = new ArrayList<TimedChunk>();
        for (TranslationResult result : results) {
            chunks.add(result.getSourceChunk());
        }
        dealWithChunks(results, new LinkedList<TranslationResult>(), chunks);
    }

    /**
     * Display the whole row for the given (source-language) chunk in the table,
     * i.e. the timing, the chunk text and an empty (fakeSubgetsBox)subgestbox.
     * We have to suppose these are coming in the same order as they appear in
     * the source.
     *
     * @param chunk - source-language chunk to show
     */
    public void showSource(final TranslationResult result) {

        final TimedChunk chunk = result.getSourceChunk();
        final ChunkIndex chunkIndex = chunk.getChunkIndex();
        int order = chunk.getOrder();
        // create label
        Label timeslabel = new Label(chunk.getDisplayTimeInterval());
        timeslabel.setStyleName("chunk_timing");
        timeslabel.setTitle("double-click to change the timing");
        // add label to map
        timeLabels.put(chunkIndex, timeslabel);

        //int index = lastIndex;
        //lastIndex++;
        synchronizer.putSourceChunk(chunk, order, true);

        //+1 because of the header
        table.setWidget(order + 1, TIMES_COLNUMBER, timeslabel);

        //html because of <br />
        Label sourcelabel = new HTML(chunk.getSurfaceForm());
        sourcelabel.setStyleName("chunk_l1");
        sourcelabel.setTitle("double-click to change this text");
        table.setWidget(order + 1, SOURCETEXT_COLNUMBER, sourcelabel);

        // initializing targetbox - fakeSubgetsBox
        SubgestBox targetbox = new SubgestBox(chunk, this, order + 1);
        SubgestBox.FakeSubgestBox fakeSubgetsBox = targetbox.new FakeSubgestBox(order + 1);
        targetBoxes.add(fakeSubgetsBox);
        table.setWidget(order + 1, TARGETBOX_COLNUMBER, fakeSubgetsBox);

        sourcelabel.addDoubleClickHandler(new SourceChangeHandler(chunk, sourcelabel, targetbox));
        timeslabel.addDoubleClickHandler(new TimeChangeHandler(chunk, targetbox));

        // initializing posteditbox - fakeSubgetsBox
        if (isPosteditOn()) {
            PosteditBox posteditBox = new PosteditBox(chunk, this, order + 1);
            PosteditBox.FakePosteditBox fakePosteditBox = posteditBox.new FakePosteditBox(order + 1);
            posteditBoxes.add(fakePosteditBox);
            table.setWidget(order + 1, POSTEDIT_COLNUMBER, fakePosteditBox);

            targetbox.setPosteditBox(posteditBox);
            posteditBox.setSubgestBox(targetbox);

        }

        // chunk-marking (dialogs):
        // setting sourcemarks:
        HTML sourcemarks = new HTML();
        sourcemarks.setStyleName("chunkmark");
        sourcemarks.setTitle("This line is part of the one-screen dialog.");
        if (chunk.isDialogue()) {
            sourcemarks.setHTML(sourcemarks.getHTML() + " &ndash; ");
        }
        if (!sourcemarks.getHTML().isEmpty()) {
            table.setWidget(order + 1, SOURCE_DIALOGMARK_COLNUMBER, sourcemarks);
            // and copying the same to the targets (GWT does not have .clone()):
            HTML targetmarks = new HTML(sourcemarks.getHTML());
            targetmarks.setStyleName(sourcemarks.getStyleName());
            targetmarks.setTitle(sourcemarks.getTitle());
            table.setWidget(order + 1, TARGET_DIALOGMARK_COLNUMBER, targetmarks);
        }

        com.github.gwtbootstrap.client.ui.Button undoButton = new com.github.gwtbootstrap.client.ui.Button("", IconType.UNDO, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Number revisionNumber = getLoadedRevision(result.getSourceChunk().getChunkIndex());
                new LoadOldSubtitleItem(result, currentWorkspace, revisionNumber);
            }
        });
        table.setWidget(order + 1, UNDO_BUTTON_COLNUMBER, undoButton);

        // grouping:
        // alignment because of the grouping:
        //table.getRowFormatter().setVerticalAlign(index + 1, HasVerticalAlignment.ALIGN_BOTTOM);
        if (chunk.getPartNumber() > 1) {
            table.getRowFormatter().addStyleName(order + 1, "row_group_continue");
        } else {
            table.getRowFormatter().addStyleName(order + 1, "row_group_begin");
        }

    }

    /**
     * Called when a time of some chunks gets changed by the TimeEditDialog.
     * Changes the labels in the workspace to match the new values.
     */
    public void changeTimeLabels(List<TimedChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return;
        }

        String newLabelValue = chunks.get(0).getDisplayTimeInterval();
        for (TimedChunk chunk : chunks) {
            Label label = timeLabels.get(chunk.getChunkIndex());
            assert label != null : "Each chunk has its timelabel";
            label.setText(newLabelValue);
        }
    }

    /**
     * Shows the real subgestbox instead of the fakeSubgetsBox one.
     */
    public void replaceFakeSubgestBox(TimedChunk chunk, SubgestBox.FakeSubgestBox fake, SubgestBox real) {
        table.remove(fake);
        int id = synchronizer.getIndexOf(chunk);
        table.setWidget(id + 1, TARGETBOX_COLNUMBER, real);
        fake.setReplaced(true);

        real.setFocus(true);
        if (isPosteditOn()) {
            replaceFakePosteditBox(chunk, real.getPosteditBox().getSubstitute(), real.getPosteditBox());
        }
    }

    /**
     * Shows the real posteditbox instead of the fakeSubgetsBox one.
     */
    public void replaceFakePosteditBox(TimedChunk chunk, PosteditBox.FakePosteditBox fake, PosteditBox real) {
        table.remove(fake);
        int id = synchronizer.getIndexOf(chunk);
        table.setWidget(id + 1, POSTEDIT_COLNUMBER, real);
        fake.setReplaced(true);
        real.setFocus(true);
    }

    /**
     * Add the given TranslationResult to the current listing interface.
     *
     * @param transresult - the TranslationResult to be shown
     */
    public void showResult(final TranslationResult transresult) {

        if (!synchronizer.isChunkDisplayed(transresult)) {
            //try it again after some time
            new com.google.gwt.user.client.Timer() {
                @Override
                public void run() {
                    showResult(transresult);
                }
            }.schedule(400);
        } else {

            //index is there -> insert result
            int index = synchronizer.getIndexOf(transresult);

            targetBoxes.get(index).getFather().setTranslationResult(transresult);
            targetBoxes.get(index).removeStyleName("loading");

            if (isPosteditOn()) {
                posteditBoxes.get(index).removeStyleName("loading");
            }
        }
    }

    /**
     * Called to tell workspace that there will be no translation result.
     * Removes the "loading" style from the boxes.
     *
     * @param chunkIndex
     */
    public void noResult(final ChunkIndex chunkIndex) {

        if (!synchronizer.isChunkDisplayed(chunkIndex)) {
            //try it again after some time
            new com.google.gwt.user.client.Timer() {
                @Override
                public void run() {
                    noResult(chunkIndex);
                }
            }.schedule(400);
        } else {
            //index is there -> insert result
            int index = synchronizer.getIndexOf(chunkIndex);
            targetBoxes.get(index).removeStyleName("loading");
            if (isPosteditOn()) {
                posteditBoxes.get(index).removeStyleName("loading");
            }
        }
    }

    /**
     * Set the focus to the next SubgestBox in order. If there is not any, stay
     * in the current one and return false.
     *
     * @param currentBox - the SubgestBox relative to which is the "next"
     * determined
     * @return false if the currentBox is the last one (and therefore nothing
     * has changed), true otherwise
     */
    public boolean goToNextBox(SubgestBox currentBox) {
        int currentIndex = synchronizer.getIndexOf(currentBox.getChunk());
        //final int nextIndex = (currentIndex < targetBoxes.size()-1) ? (currentIndex + 1) : currentIndex;
        final int nextIndex = currentIndex + 1;
        if (nextIndex >= targetBoxes.size()) {
            return false;
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                SubgestBox.FakeSubgestBox targetbox = targetBoxes.get(nextIndex);
                if (targetbox.isAttached()) {
                    targetbox.setFocus(true);
                } else { // there is already a real box instead of the fakeSubgetsBox one
                    targetbox.getFather().setFocus(true);
                }
            }
        });
        return true;
    }

    /**
     * Set the focus to the next SubgestBox in order. If there is not any, stay
     * in the current one and return false.
     *
     * @param currentBox - the SubgestBox relative to which is the "next"
     * determined
     * @return false if the currentBox is the last one (and therefore nothing
     * has changed), true otherwise
     */
    public boolean goToNextBox(PosteditBox currentBox) {
        int currentIndex = synchronizer.getIndexOf(currentBox.getSubgestBox().getChunk());
        //final int nextIndex = (currentIndex < targetBoxes.size()-1) ? (currentIndex + 1) : currentIndex;
        final int nextIndex = currentIndex + 1;
        if (nextIndex >= posteditBoxes.size()) {
            return false;
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                PosteditBox.FakePosteditBox targetbox = posteditBoxes.get(nextIndex);
                if (targetbox.isAttached()) {
                    targetbox.setFocus(true);
                } else { // there is already a real box instead of the fakeSubgetsBox one
                    targetbox.getFather().setFocus(true);
                }
            }
        });
        return true;
    }

    /**
     * Set the focus to the previous SubgestBox in order. If there is not any,
     * stay in the current one and return false.
     *
     * @param currentBox - the SubgestBox relative to which is the "previous"
     * determined
     * @return false if the currentBox is the first one (and therefore nothing
     * has changed), true otherwise
     */
    public boolean goToPreviousBox(SubgestBox currentBox) {
        int currentIndex = synchronizer.getIndexOf(currentBox.getChunk());
        //final int prevIndex = (currentIndex > 0) ? (currentIndex - 1) : currentIndex;
        final int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            return false;
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                SubgestBox.FakeSubgestBox targetbox = targetBoxes.get(prevIndex);
                if (targetbox.isAttached()) {
                    targetbox.setFocus(true);
                } else { // there is already a real box instead of the fakeSubgetsBox one
                    targetbox.getFather().setFocus(true);
                }
            }
        });
        return true;
    }

    /**
     * Set the focus to the previous SubgestBox in order. If there is not any,
     * stay in the current one and return false.
     *
     * @param currentBox - the SubgestBox relative to which is the "previous"
     * determined
     * @return false if the currentBox is the first one (and therefore nothing
     * has changed), true otherwise
     */
    public boolean goToPreviousBox(PosteditBox currentBox) {
        int currentIndex = synchronizer.getIndexOf(currentBox.getSubgestBox().getChunk());
        //final int prevIndex = (currentIndex > 0) ? (currentIndex - 1) : currentIndex;
        final int prevIndex = currentIndex - 1;
        if (prevIndex < 0) {
            return false;
        }
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                PosteditBox.FakePosteditBox targetbox = posteditBoxes.get(prevIndex);
                if (targetbox.isAttached()) {
                    targetbox.setFocus(true);
                } else { // there is already a real box instead of the fakePosteditBox one
                    targetbox.getFather().setFocus(true);
                }
            }
        });
        return true;
    }

    /**
     * Scrolls the page so that the subgestbox isvisible to the user.
     */
    public void ensureVisible(SubgestBox subbox) {
        Window.scrollTo(
                Window.getScrollLeft(),
                getScrollOffsetY(subbox.getElement())
                - getVideoHeight()
                - (Window.getClientHeight() - getVideoHeight()) * 2 / 5
        );
    }

    /**
     * Scrolls the page so that the subgestbox isvisible to the user.
     */
    public void ensureVisible(PosteditBox posteditBox) {
        Window.scrollTo(
                Window.getScrollLeft(),
                getScrollOffsetY(posteditBox.getElement())
                - getVideoHeight()
                - (Window.getClientHeight() - getVideoHeight()) * 2 / 5
        );
    }

    private int getVideoHeight() {
        return panelForVideo.getOffsetHeight();
    }

    /**
     * Hide the currently active (visible) popup with suggestions
     */
    public void deactivateSuggestionWidget() {
        Widget w = this.activeSuggestionWidget;
        if (w != null) {
            if (w instanceof PopupPanel) {
                //((PopupPanel)w).hide();
                w.setVisible(false);
            } else {
                ((Panel) (w.getParent())).remove(w);
            }
            setActiveSuggestionWidget(null);
        }
    }

    public void deactivatePosteditWidget() {
        Widget w = this.activePosteditWidget;
        if (w != null) {
            if (w instanceof PopupPanel) {
                ((PopupPanel) w).setVisible(false);
            } else {
                ((Panel) w.getParent()).remove(w);
            }
            setActivePosteditWidget(null);
        }
    }

    private List<TimedChunk> translationResultsAChunks(List<TranslationResult> results) {
        List<TimedChunk> chunks = new ArrayList<TimedChunk>();

        for (TranslationResult result : results) {
            chunks.add(result.getSourceChunk());
        }

        return chunks;
    }

    public void searchAndReplace(RegExp searchExp, String replace) {

        Map<ChunkIndex, TranslationResult> translationResults = currentDocument.getTranslationResults();

        Collection<TranslationResult> results = translationResults.values();

        for (TranslationResult result : results) {
            MatchResult matchResult = searchExp.exec(result.getUserTranslation());

            if (matchResult == null) {
                continue;
            }

            if (matchResult.getGroupCount() > 0) {
                String replacedString = searchExp.replace(result.getUserTranslation(), replace);

                PosteditPair replacePair = new PosteditPair(result.getUserTranslation(), replacedString);
                replacePair.setSource(PosteditSource.SEARCHANDREPLACE);

                result.getPosteditSuggestions().add(replacePair);
                int index = synchronizer.getIndexOf(result);
                PosteditBox.FakePosteditBox posteditBox = posteditBoxes.get(index);
                if (posteditBox.isReplaced()) {
                    posteditBox.getFather().addStyleDependentName("replace");
                } else {
                    posteditBox.addStyleDependentName("replace");
                }
            }
        }
    }
    ////////////////////////////
    //                        //
    //   Native Methods       //
    //                        //
    ////////////////////////////

    private native int getScrollOffsetY(Element e) /*-{
        if (!e)
          return;


        var item = e;
        var realOffset = 0;
        while (item && (item != document.body)) {
            realOffset += item.offsetTop;
            item = item.offsetParent;
        }

        return realOffset;
    }-*/;

    ////////////////////////////
    //                        //
    //   Inner Classes        //
    //                        //
    ////////////////////////////
    /**
     * The document can be either newly created or loaded from the database.
     */
    public enum DocumentOrigin {
        NEW, FROM_DB
    }

    /**
     * Requests TranslationResults for the chunks, sending them in groups to
     * compromise between responsiveness and effectiveness.
     */
    public class SendChunksCommand {

        LinkedList<TimedChunk> chunks;

        public SendChunksCommand(List<TimedChunk> chunks) {
            this.chunks = new LinkedList<TimedChunk>(chunks);
        }

        /**
         * exponential window a "trick" - first subtitle goes in a single
         * request so it's here soonest without wait then the next two then the
         * next four then next eight so the first 15 subtitles arrive as quickly
         * as possible but we also want as little requests as possible -> the
         * "window" is exponentially growing
         */
        int exponential = 1;

        /**
         * The maximum size of the window
         */
        int expMax = 64;

        public boolean execute() {
            if (stopLoading) {
                return false;
            }

            if (chunks.isEmpty()) {
                return false;
            } else {
                List<TimedChunk> sentTimedchunks = new ArrayList<TimedChunk>(exponential);
                for (int i = 0; i < exponential; i++) {
                    if (!chunks.isEmpty()) {
                        TimedChunk timedchunk = chunks.removeFirst();
                        sentTimedchunks.add(timedchunk);
                    }
                }
                sendChunks(sentTimedchunks);
                exponential = exponential * 2;
                if (exponential > expMax) {
                    exponential = expMax;
                }
                return true;
            }
        }

        /**
         * Called to tell that there will be no more results, probably because
         * the browser is offline, so that for all the remaining chunks there is
         * no point in requesting them or waiting for them.
         */
        public void noMoreResults() {
            for (TimedChunk chunk : chunks) {
                noResult(chunk.getChunkIndex());
            }
            chunks.clear();
        }

        private void sendChunks(List<TimedChunk> timedchunks) {
            new GetTranslationResults(timedchunks, SendChunksCommand.this, TranslationWorkspace.this);
        }
    }

    /**
     * Shows original subtitle chunks
     */
    private class ShowOriginalCommand implements RepeatingCommand {

        LinkedList<TranslationResult> resultsToDisplay = new LinkedList<TranslationResult>();

        /**
         * for a new document (all chunks are sent to be translated, none of the
         * chunks has a translation yet)
         *
         * @param chunks all chunks
         */
        public ShowOriginalCommand(List<TranslationResult> results) {
            this.resultsToDisplay.addAll(results);
        }

        @Override
        public boolean execute() {
            if (stopLoading) {
                return false;
            }

            if (!resultsToDisplay.isEmpty()) {
                TranslationResult tResult = resultsToDisplay.removeFirst();
                showSource(tResult);
                return true;
            }
            Gui.getGuiStructure().contentPanel.removeStyleName("parsing");
            return false;
        }
    }

    /**
     * Shows user translated subtitle chunks
     */
    private class ShowUserTranslatedCommand implements RepeatingCommand {

        LinkedList<TranslationResult> resultsToDisplay = new LinkedList<TranslationResult>();

        public ShowUserTranslatedCommand(List<TranslationResult> chunks) {
            this.resultsToDisplay.addAll(chunks);
        }

        @Override
        public boolean execute() {
            if (stopLoading) {
                return false;
            }

            if (!resultsToDisplay.isEmpty()) {
                TranslationResult result = resultsToDisplay.removeFirst();
                showResult(result);
                return true;
            }
            return false;
        }
    }

    /**
     * Used to change the source of a chunk. Rough and probably TODO now.
     */
    public class SourceChangeHandler implements DoubleClickHandler {

        private ChunkIndex chunkIndex;
        private Label label;
        private SubgestBox subbox;

        private SourceChangeHandler(TimedChunk chunk, Label label, SubgestBox subbox) {
            this.chunkIndex = chunk.getChunkIndex();
            this.label = label;
            this.subbox = subbox;
        }

        @Override
        public void onDoubleClick(DoubleClickEvent event) {
            // TODO probably something nicer than the prompt

            if (lockedSubgestBox != subbox) {
                if (lockedSubgestBox != null) {
                    currentWorkspace.submitUserTranslation(lockedSubgestBox, subbox, this, null);
                } else {
                    new LockTranslationResult(subbox, currentWorkspace, this);
                }
            } else {
                changeSource();
            }

        }

        public void changeSource() {
            // init
            TimedChunk chunk = synchronizer.getChunkByIndex(chunkIndex);
            String oldSource = chunk.getDatabaseForm();

            // ask user for new value, showing the old one
            String newSource = Window.prompt("Source text for this chunk. "
                    + "The pipe sign  |  (surrounded by spaces) denotes a new line.",
                    oldSource);

            if (newSource == null || newSource.equals(oldSource)) {
                // cancel or no change
                return;
            } else {
                // change the values
                chunk.setDatabaseFormForce(newSource);
                label.getElement().setInnerHTML(chunk.getGUIForm());
                // this call brings a fresh translation result on return :-)
                // which is then given directly to showResult()
                new ChangeSourceChunk(chunk, newSource, TranslationWorkspace.this);
            }
        }
    }

    /**
     * Used to change the time of a chunk. Also changes of all chunks with the
     * same id (i.e. which are parts of the same chunk actually). Very rough and
     * very TODO now.
     */
    public class TimeChangeHandler implements DoubleClickHandler {

        private TimedChunk chunk;
        private SubgestBox subbox;

        // computed and cached when invoked for the first time
        private List<TimedChunk> chunks = null;

        private TimeChangeHandler(TimedChunk chunk, SubgestBox subbox) {
            this.chunk = chunk;
            this.subbox = subbox;
        }

        @Override
        public void onDoubleClick(DoubleClickEvent event) {
            if (lockedSubgestBox != subbox) {
                if (lockedSubgestBox != null) {
                    currentWorkspace.submitUserTranslation(lockedSubgestBox, subbox, null, this);
                } else {
                    new LockTranslationResult(subbox, currentWorkspace, this);
                }
            } else {
                changeTiming();
            }
        }

        public void changeTiming() {
            if (chunks == null) {
                chunks = synchronizer.getChunksById(chunk.getId());
            }
            // the chunks are directly modified by the TimeEditDialog
            new TimeEditDialog(chunks, TranslationWorkspace.this);
        }
    }
}
