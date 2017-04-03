/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.subgestbox;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.*;
import java.util.List;

/**
 * Variant of a TextBox with pop-up postedit suggestions taken from the given
 * TranslationResult.
 *
 * The Postedit Box provides a textbox-like interface and visualizes the
 * postedit results, offering a variety of means of navigation through them. It
 * is based on the IFrame HTML element to support also multi-line and formatted
 * inputs.
 *
 * The postedit results are shown as a pop-up suggestion list when the textbox
 * is focused in a {@link SubgestPopupStructure}.
 *
 * Another features like auto-scrolling to a certain place of the screen and
 * height auto-adjustment for multi-line inputs were added to improve the user
 * experience. The behaviour and features mentioned requires a custom event
 * handling, this is provided by a {@link PosteditHandler} instance (common to
 * all the PosteditBoxes within a {@link TranslationWorkspace}).
 *
 * @author Matúš Námešný
 */
public class PosteditBox extends RichTextArea implements Comparable<PosteditBox> {

    private FakePosteditBox substitute = null;
    private TimedChunk chunk;
    private TranslationResult translationResult;
    private TranslationWorkspace workspace;
    private PopupPanel posteditPanel;
    private Widget posteditWidget;
    private boolean loadedSuggestions = false;
    String lastText = "";
    private SubgestBox subgestBox;

    public PosteditBox(TimedChunk chunk, TranslationWorkspace workspace, int tabIndex) {
        this.chunk = chunk;
        this.workspace = workspace;
        if (this.workspace == null) {
            Gui.log("workspace for subgestbox is null!!!");
        }

        this.subgestBox = null;

        this.setHeight("36px");
        this.setHTML(posteditBoxHTML(""));

        this.addFocusHandler(this.workspace.posteditHandler);
        this.addKeyDownHandler(this.workspace.posteditHandler);
        this.addKeyUpHandler(this.workspace.posteditHandler);
        this.setTabIndex(tabIndex);

        this.addStyleName("posteditwidth");

        final RichTextArea richtext = this;
        richtext.addInitializeHandler(new InitializeHandler() {
            public void onInitialize(InitializeEvent ie) {
                IFrameElement fe = (IFrameElement) richtext.getElement().cast();
                Style s = fe.getContentDocument().getBody().getStyle();
                s.setProperty("fontFamily", "Arial Unicode MS,Arial,sans-serif");
                s.setProperty("fontSize", "small");
                s.setColor("#333");
            }
        });
    }

    @Override
    public int compareTo(PosteditBox o) {
        return this.getTranslationResult().compareTo(o.getTranslationResult());
    }

    public void replaceFakeWithReal() {
        workspace.replaceFakeSubgestBox(chunk, subgestBox.getSubstitute(), subgestBox);
    }

    private String posteditBoxHTML(String content) {
        content = content.replaceAll("\n", "<br>");
        return content;
    }

    /**
     * @return the subgestBox
     */
    public SubgestBox getSubgestBox() {
        return subgestBox;
    }

    /**
     * @param subgestBox the subgestBox to set
     */
    public void setSubgestBox(SubgestBox subgestBox) {
        this.subgestBox = subgestBox;
    }

    /**
     * @return the substitute
     */
    public FakePosteditBox getSubstitute() {
        return substitute;
    }

    /**
     * Replace the text remembered as the last saved text with the current text.
     * To be used when the text is sent to be saved as the user translation via
     * SetUserTranslation.
     */
    public void updateLastText() {
        this.lastText = this.getTextWithNewlines();
    }

    /**
     * Returns the SubgestBox' contents as text with newlines unified as "\n"
     * (also trimmed on the beginning and end and removed duplicate newlines)
     *
     * @return the text contents with unified newlines
     */
    public String getTextWithNewlines() {
        String text = this.getHTML();
        RegExp newlineTags = RegExp.compile("<p>|<div>|<br>", "g");
        RegExp toClean = RegExp.compile("</p>|</div>|&nbsp;", "g");
        RegExp newlineSequence = RegExp.compile("\n*");
        text = newlineTags.replace(text, "\n");
        text = toClean.replace(text, "");
        text = newlineSequence.replace(text, "\n");
        text = text.trim();
        return text;
    }

    /**
     * @param posteditWidget the posteditWidget to set
     */
    public void setPosteditWidget(Widget posteditWidget) {
        this.posteditWidget = posteditWidget;
    }

    /**
     * @return the posteditWidget
     */
    public Widget getPosteditWidget() {
        return posteditWidget;
    }

    /**
     * @return the translationResult
     */
    public TranslationResult getTranslationResult() {
        return translationResult;
    }

    /**
     * @param translationResult the translationResult to set
     */
    public void setTranslationResult(TranslationResult translationResult) {

        this.translationResult = translationResult;
        String posteditedString = translationResult.getPosteditedString();

        if (posteditedString != null && !posteditedString.equals("")) {
            getSubstitute().setText(posteditedString);
            this.setHTML(posteditBoxHTML(posteditedString));
            updateLastText();
        }
    }

    /**
     * Lightweight input area serving as a substitute for the PosteditBox before
     * it is focused (and worked with)
     */
    public class FakePosteditBox extends TextArea implements Comparable<FakePosteditBox> {

        private boolean replaced;

        public FakePosteditBox(int tabIndex) {
            PosteditBox.this.substitute = PosteditBox.FakePosteditBox.this;
            replaced = false;

            this.addFocusHandler(new FocusHandler() {
                @Override
                public void onFocus(FocusEvent event) {
                    if (event.getSource() instanceof FakePosteditBox) { // should be
                        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                            @Override
                            public void execute() {
                                replaceFakeWithReal();
                            }
                        });
                    }
                }
            });
            this.setTabIndex(tabIndex);
            this.setStyleName("pre_subgestbox");
            this.addStyleName("loading");
            this.setHeight("36px");

            this.addStyleName("posteditwidth");
        }

        /**
         * Returns the "real" SubgestBox which is substituted by this fake one.
         *
         * @return the corresponding "real" SubgestBox
         */
        public PosteditBox getFather() {
            return PosteditBox.this;
        }

        /**
         * Comparison according to the underlying "real" SubgestBoxes
         *
         * @param that
         * @return
         */
        @Override
        public int compareTo(FakePosteditBox that) {
            return getFather().compareTo(that.getFather());
        }

        /**
         * @return the replaced
         */
        public boolean isReplaced() {
            return replaced;
        }

        /**
         * @param replaced the replaced to set
         */
        public void setReplaced(boolean replaced) {
            this.replaced = replaced;
        }

    }

    /**
     * True if the user translation text has changed since the last submitting
     * (or update) (except for the trimmed newlines).
     *
     * @return
     */
    public boolean textChanged() {
        return !this.getTextWithNewlines().equals(this.lastText);
    }

    public int getCorrectVerticalSize() {
        FrameElement frameElement = (FrameElement) this.getElement().cast();
        int newHeight = frameElement.getContentDocument().getScrollHeight();
        return newHeight;
    }

    /**
     * Adjust the height of the input area according to the height of its
     * contents.
     */
    public void updateVerticalSize() {

        int verticalSize = getCorrectVerticalSize();
        int subgestHeight = subgestBox.getCorrectVerticalSize();

        int newHeight = Math.max(36, Math.max(verticalSize, subgestHeight));
        setHeight(newHeight + "px");

        if (newHeight != subgestHeight) {
            subgestBox.updateVerticalSize();
        }

        showSuggestions();
    }

    /**
     * Prepare the suggestions from the underlying TranslationResult to be
     * displayed (if they are already fetched and not loaded yet).
     */
    public void loadSuggestions() {

        if (loadedSuggestions == true) {
            return;
        }

        loadedSuggestions = true;
        // creating the suggestions pop-up panel:
        posteditPanel = new PopupPanel();
        posteditPanel.setAutoHideEnabled(true);
        posteditPanel.setStylePrimaryName("suggestionsPopup");

        final SingleSelectionModel<PosteditPair> selectionModel = new SingleSelectionModel<PosteditPair>();
        CellList<PosteditPair> cellList = new CellList<PosteditPair>(new PosteditBox.PosteditCell(selectionModel, posteditPanel));
        cellList.setWidth(Integer.toString(this.getOffsetWidth()) + "px");
        cellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);

        // setting tabindex so that the suggestions are focused between this box and the next one
        cellList.setTabIndex(this.getTabIndex());

        cellList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                PosteditPair selected = selectionModel.getSelectedObject();
                if (selected != null) {

                    Gui.log(LevelLogEnum.Error, "PosteditBox.onSelectionChange", selected.getOriginChunk().getSurfaceForm() + " " + selected.getPosteditedChunk().getSurfaceForm());

                    //translationResult.setSelectedTranslationPairID(selected.getId());
                    // copy the selected suggestion into the richtextarea with the annotation highlighting:
                    setHTML(posteditBoxHTML(selected.getPosteditedChunk().getSurfaceForm()));
                    // contents have changed - resize if necessary:
                    updateVerticalSize();

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            PosteditBox.this.setFocus(true);
                        }
                    });

                }
            }
        });
        cellList.setRowData(this.getSuggestions());
        posteditPanel.setWidget(cellList);

        this.setPosteditWidget(posteditPanel);
    }

    /**
     * Returns the list of suggestions from the underlying TranslationResult.
     *
     * @return
     */
    public List<PosteditPair> getSuggestions() {
        //Gui.log(LevelLogEnum.Error, "PosteditBox.getSuggestions()", String.valueOf(this.translationResult.getPosteditSuggestions().size()));
        return this.translationResult.getPosteditSuggestions();
    }

    /**
     * Display the postedit suggestion widget with the suggestions from the
     * underlying TranslationResult.
     */
    public void showSuggestions() {

        if (this.getSuggestions().size() > 0) {

            // showing the suggestions always below this SubgestBox:
            final UIObject relativeObject = this;
            posteditPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                @Override
                public void setPosition(int offsetWidth, int offsetHeight) {
                    // Calculate left position for the popup
                    int left = relativeObject.getAbsoluteLeft();
                    // Calculate top position for the popup
                    int top = relativeObject.getAbsoluteTop();
                    // Position below the textbox:
                    top += relativeObject.getOffsetHeight();
                    posteditPanel.setPopupPosition(left, top);
                }
            });
            posteditWidget.setWidth(this.getOffsetWidth() + "px");
        }
    }

    /**
     * The Cell used to render the list of suggestions from the current
     * TranslationPair.
     */
    static class PosteditCell extends AbstractCell<PosteditPair> {

        // for explicitly setting the selection after Enter key press
        private SingleSelectionModel<PosteditPair> selectionModel;
        private PopupPanel parentPopup;

        public PosteditCell(SingleSelectionModel<PosteditPair> selectionModel, PopupPanel parentPopup) {
            super("keydown"); // tells the AbstractCell that we want to catch the keydown events
            this.selectionModel = selectionModel;
            this.parentPopup = parentPopup;
        }

        @Override
        public void render(Cell.Context context, PosteditPair value, SafeHtmlBuilder sb) {
            // Value can be null, so do a null check:
            if (value == null) {
                return;
            }
            SubgestPopupStructure struct = new SubgestPopupStructure(value);
            // TODO after switching to GWT 2.5 - use UiRenderer for doing this;
            // (this is probably neither the safest, nor the best way...)
            sb.append(SafeHtmlUtils.fromTrustedString(struct.toString()));
        }

        @Override
        protected void onEnterKeyDown(Cell.Context context, Element parent, PosteditPair value,
                NativeEvent event, ValueUpdater<PosteditPair> valueUpdater) {
            // selecting also by Enter is automatic in Opera only, others use only Spacebar
            // (and we want also Enter everywhere)
            event.preventDefault();
            selectionModel.setSelected(value, true);
        }

        @Override
        public void onBrowserEvent(
                com.google.gwt.cell.client.Cell.Context context,
                Element parent, PosteditPair value, NativeEvent event,
                ValueUpdater<PosteditPair> valueUpdater) {
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
            // handle the tabbing out from the selecting process (by keyboard) - hiding the popup:
            if ("keydown".equals(event.getType())) {
                if (event.getKeyCode() == KeyCodes.KEY_TAB) {
                    parentPopup.setVisible(false);
                    // (we cannot hide it because the list would lose its proper TabIndex)
                }
            }
        }

    }

}
