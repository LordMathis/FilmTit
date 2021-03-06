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
package cz.filmtit.client.subgestbox;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import cz.filmtit.client.Gui;
import cz.filmtit.client.callables.LockTranslationResult;
import cz.filmtit.client.callables.ReloadTranslationResults;
import cz.filmtit.client.callables.UnlockTranslationResult;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.LevelLogEnum;

/**
 *
 * @author Matúš Námešný
 */
public class PosteditHandler implements FocusHandler, KeyDownHandler, KeyUpHandler, ChangeHandler {

    private TranslationWorkspace workspace;

    /**
     * Creates a new PosteditHandler within the given workspace.
     *
     * @param workspace - the TranslationWorkspace inside which the handled
     * SubgestBox operates
     */
    public PosteditHandler(TranslationWorkspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void onFocus(FocusEvent event) {

        SubgestBox unlockedSubgestBox = workspace.getUnlockedSubgestBox();

        if (unlockedSubgestBox != null) {
            unlockedSubgestBox.removeStyleDependentName("unlocked");
            unlockedSubgestBox.setEnabled(true);

            PosteditBox unlockedPosteditBox = unlockedSubgestBox.getPosteditBox();
            if (unlockedPosteditBox != null) {
                unlockedPosteditBox.removeStyleDependentName("unlocked");
                unlockedPosteditBox.setEnabled(true);
            }
        }

        if (event.getSource() instanceof PosteditBox) {
            final PosteditBox posteditBox = (PosteditBox) event.getSource();

            if (workspace.getLockedSubgestBox() == null) {
                new LockTranslationResult(posteditBox.getSubgestBox(), workspace);
                new ReloadTranslationResults(workspace.getCurrentDocument().getId(), workspace);
            } else if (workspace.getLockedSubgestBox() != posteditBox.getSubgestBox()) {
                SubgestBox toSaveAndUnlock = workspace.getLockedSubgestBox();
                toSaveAndUnlock.getTranslationResult().setUserTranslation(toSaveAndUnlock.getTextWithNewlines());
                toSaveAndUnlock.getTranslationResult().setPosteditedString(posteditBox.getTextWithNewlines());

                // submitting only when the contents have changed
                if (toSaveAndUnlock.textChanged() || posteditBox.textChanged()) {
                    workspace.submitUserTranslation(toSaveAndUnlock, posteditBox.getSubgestBox(), null, null);
                    toSaveAndUnlock.updateLastText();
                    posteditBox.updateLastText();
                } else {
                    new UnlockTranslationResult(toSaveAndUnlock, workspace, posteditBox.getSubgestBox());
                    new ReloadTranslationResults(workspace.getCurrentDocument().getId(), workspace);
                }

            }

            posteditBox.loadSuggestions();

            workspace.deactivatePosteditWidget();
            workspace.deactivateSuggestionWidget();
            workspace.ensureVisible(posteditBox);

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    posteditBox.showSuggestions();
                    workspace.setActivePosteditWidget(posteditBox.getPosteditWidget());
                }
            });

            if (Window.Navigator.getUserAgent().matches(".*Firefox.*")) {
                // In Firefox - resetting focus needed:
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        posteditBox.setFocus(true);
                    }
                });
            }

            int start = (int) (posteditBox.getSubgestBox().getChunk().getStartTimeLong() / 1000);
            int end = (int) (posteditBox.getSubgestBox().getChunk().getEndTimeLong() / 1000);

            if (workspace.getYtVideoPlayer() != null) {
                workspace.getYtVideoPlayer().playPart(start, end);
            } else if (workspace.getFileVideoPlayer() != null) {
                workspace.getFileVideoPlayer().playPart(start, end);
            }

            posteditBox.updateVerticalSize();
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getSource() instanceof PosteditBox) {
            if (isThisKeyEvent(event, KeyCodes.KEY_DOWN)) {
                event.preventDefault(); // default is to scroll down the page or to move to the next line in the textarea
                PosteditBox posteditBox = (PosteditBox) event.getSource();
                Focusable suggestionsList = ((Focusable) ((SimplePanel) posteditBox.getPosteditWidget()).getWidget());
                Gui.log("setting focus to postedit suggestions");
                suggestionsList.setFocus(true);
            } // pressing Esc:
            else if (isThisKeyEvent(event, KeyCodes.KEY_ESCAPE)) {
                // hide the suggestion widget corresponding to the SubgestBox
                //   which previously had focus (PopupPanel does not hide on keyboard events)
                workspace.deactivateSuggestionWidget();
                workspace.deactivatePosteditWidget();
            } // pressing Tab:
            else if (isThisKeyEvent(event, KeyCodes.KEY_TAB)) {
                event.preventDefault(); // e.g. in Chrome, default is to insert TAB character in the textarea
                workspace.deactivateSuggestionWidget();
                workspace.deactivatePosteditWidget();
                PosteditBox posteditBox = (PosteditBox) event.getSource();
                if (event.isShiftKeyDown()) {
                    workspace.goToPreviousBox(posteditBox);
                } else {
                    workspace.goToNextBox(posteditBox);
                }
            }
        }
    }

    /**
     * Tell if the given event's key corresponds to the given keycode - in a
     * various ways, hopefully compliant with all the major browsers...
     *
     * @param event
     * @param keycode
     * @return true if this KeyDownEvent's key has the given keycode, false
     * otherwise
     */
    private boolean isThisKeyEvent(KeyDownEvent event, int keycode) {
        return ((event.getNativeEvent().getCharCode() == keycode)
                || (event.getNativeKeyCode() == keycode)
                || (event.getNativeEvent().getKeyCode() == keycode));
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getSource() instanceof PosteditBox) {
            final PosteditBox posteditBox = (PosteditBox) event.getSource();

            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    posteditBox.updateVerticalSize();
                }
            });

            workspace.getTimer().schedule(60000);
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        workspace.getTimer().cancel();
        workspace.getTimer().schedule(60000);
    }

}
