/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client.subgestbox;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.InitializeEvent;
import com.google.gwt.event.logical.shared.InitializeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.Gui;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.LevelLogEnum;
import cz.filmtit.share.TimedChunk;
import cz.filmtit.share.TranslationResult;

/**
 *
 * @author matus
 */
public class PosteditBox extends RichTextArea implements Comparable<PosteditBox> {

    private FakePosteditBox substitute = null;
    private TimedChunk chunk;
    private TranslationResult translationResult;
    private TranslationWorkspace workspace;
    /*private PopupPanel suggestPanel;
    private Widget suggestionWidget;
    private boolean loadedSuggestions = false;*/
    String lastText = "";
    private SubgestBox subgestBox;

    public PosteditBox(TimedChunk chunk, TranslationWorkspace workspace, int tabIndex) {
        this.chunk = chunk;
        this.translationResult = new TranslationResult(chunk);
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
    }

    public class FakePosteditBox extends TextArea implements Comparable<FakePosteditBox> {

        public FakePosteditBox(int tabIndex) {
            PosteditBox.this.substitute = PosteditBox.FakePosteditBox.this;

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

    }

    public int getCorrectVerticalSize() {
        FrameElement frameElement = (FrameElement) this.getElement().cast();
        int newHeight = frameElement.getContentDocument().getScrollHeight();
        Gui.log(LevelLogEnum.Error, "PosteditBox.getCorrectVerticalSize()", String.valueOf(newHeight));
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
    }

}
