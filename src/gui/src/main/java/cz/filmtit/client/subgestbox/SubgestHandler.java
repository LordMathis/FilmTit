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
package cz.filmtit.client.subgestbox;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
 * Universal event-handler for all {@link SubgestBox} instances in one
 * {@link TranslationWorkspace} instance.
 * @author FilmTit, Matúš Námešný
 */
public class SubgestHandler implements FocusHandler, KeyDownHandler, KeyUpHandler, ChangeHandler {

    private TranslationWorkspace workspace;

    /**
     * Creates a new SubgestHandler within the given workspace.
     *
     * @param workspace - the TranslationWorkspace inside which the handled
     * SubgestBox operates
     */
    public SubgestHandler(TranslationWorkspace workspace) {
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

        if (event.getSource() instanceof SubgestBox) {
            final SubgestBox subbox = (SubgestBox) event.getSource();

            if (workspace.getLockedSubgestBox() == null) {

                new LockTranslationResult(subbox, workspace);
                new ReloadTranslationResults(workspace.getCurrentDocument().getId(), workspace);

            } else if (workspace.getLockedSubgestBox() != subbox) {
                SubgestBox toSaveAndUnlock = workspace.getLockedSubgestBox();
                toSaveAndUnlock.getTranslationResult().setUserTranslation(toSaveAndUnlock.getTextWithNewlines());
                boolean posteditTextChanged = false;

                if (workspace.isPosteditOn()) {
                    toSaveAndUnlock.getTranslationResult().setPosteditedString(toSaveAndUnlock.getPosteditBox().getTextWithNewlines());
                    posteditTextChanged = toSaveAndUnlock.getPosteditBox().textChanged();
                }

                // submitting only when the contents have changed
                if (toSaveAndUnlock.textChanged() || posteditTextChanged) {
                    workspace.submitUserTranslation(toSaveAndUnlock, subbox, null, null);
                    toSaveAndUnlock.updateLastText();
                    if (workspace.isPosteditOn()) {
                        toSaveAndUnlock.getPosteditBox().updateLastText();
                    }
                } else {
                    new UnlockTranslationResult(toSaveAndUnlock, workspace, subbox);
                    new ReloadTranslationResults(workspace.getCurrentDocument().getId(), workspace);
                }

            }

            subbox.loadSuggestions(); // if not already loaded - the check is inside

            // hide the suggestion widget corresponding to the SubgestBox
            //   which previously had focus (if any)
            workspace.deactivateSuggestionWidget();
            workspace.deactivatePosteditWidget();
            workspace.ensureVisible(subbox);
            // and show a new one for the current SubgestBox
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    subbox.showSuggestions();
                    workspace.setActiveSuggestionWidget(subbox.getSuggestionWidget());
                }
            });

            if (Window.Navigator.getUserAgent().matches(".*Firefox.*")) {
                // In Firefox - resetting focus needed:
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        subbox.setFocus(true);
                    }
                });
            }

            int start = (int) (subbox.getChunk().getStartTimeLong() / 1000);
            int end = (int) (subbox.getChunk().getEndTimeLong() / 1000);
            if (workspace.getYtVideoPlayer() != null) {
                workspace.getYtVideoPlayer().playPart(start, end);
            } else if (workspace.getFileVideoPlayer() != null) {
                workspace.getFileVideoPlayer().playPart(start, end);
            }

            subbox.updateVerticalSize();
        }
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        if (event.getSource() instanceof SubgestBox) {
            // pressing the Down arrow - setting focus to the suggestions:
            if (isThisKeyEvent(event, KeyCodes.KEY_DOWN)) {
                event.preventDefault(); // default is to scroll down the page or to move to the next line in the textarea
                SubgestBox subbox = (SubgestBox) event.getSource();
                Focusable suggestionsList = ((Focusable) ((SimplePanel) subbox.getSuggestionWidget()).getWidget());
                Gui.log("setting focus to suggestions");
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
                SubgestBox subbox = (SubgestBox) event.getSource();
                if (event.isShiftKeyDown()) {
                    workspace.goToPreviousBox(subbox);
                } else {
                    workspace.goToNextBox(subbox);
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
        if (event.getSource() instanceof SubgestBox) { // should be
            final SubgestBox subbox = (SubgestBox) event.getSource();

            // auto-resize, if necessary:
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    subbox.updateVerticalSize();
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
