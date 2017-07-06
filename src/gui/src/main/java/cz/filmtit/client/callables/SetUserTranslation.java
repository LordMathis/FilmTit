/*Copyright 2012 FilmTit authors - Karel Bílek, Josef Čech, Joachim Daiber, Jindřich Libovický, Rudolf Rosa, Jan Václ

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
package cz.filmtit.client.callables;

import cz.filmtit.client.*;

import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.client.subgestbox.PosteditBox;
import cz.filmtit.client.subgestbox.SubgestBox;
import cz.filmtit.share.*;

/**
 * Save the user translation of the given chunk (no matter whether it is user's
 * own translation or a suggestion taken over or post-edited). The ID of the
 * TranslationPair chosen for post-editing is also sent, providing feedback
 * which then can be used to improve future suggestions. If in Offline Mode, the
 * user translation is not sent to User Space but is saved in the Local Storage.
 *
 * @author rur, matus n
 *
 */
public class SetUserTranslation extends Callable<Void> {

    // parameters
    private ChunkIndex chunkIndex;
    private long documentId;
    private String userTranslation;
    private long chosenTranslationPair;

    private boolean toLockNext = false;

    private SubgestBox toUnlockBox;
    private SubgestBox toLockBox;

    private TranslationWorkspace workspace;

    private String posteditedString;
    private long chosenPosteditPairID;

    private TranslationWorkspace.SourceChangeHandler sourceChangeHandler;
    private TranslationWorkspace.TimeChangeHandler timeChangeHandler;

    @Override
    public String getName() {
        return getNameWithParameters(chunkIndex, documentId, userTranslation, chosenTranslationPair);
    }

    @Override
    public void onSuccessAfterLog(Void o) {

        Gui.log(LevelLogEnum.Notice, "SetUserTranslation", "Set User Translation " + userTranslation);

        workspace.setLockedSubgestBox(null);
        toUnlockBox.setFocus(false);

        // remove styling from SubgestBoxes and PosteditBoxes
        toUnlockBox.removeStyleDependentName("locked");
        PosteditBox posteditUnlockBox = toUnlockBox.getPosteditBox();
        if (posteditUnlockBox != null) {
            posteditUnlockBox.removeStyleDependentName("locked");
        }

        // Setting user translation also unlocks the translation result so we only need to lock a new one
        if (toLockNext) {
            if (sourceChangeHandler != null) {
                new LockTranslationResult(toLockBox, workspace, sourceChangeHandler);
            } else if (timeChangeHandler != null) {
                new LockTranslationResult(toUnlockBox, workspace, timeChangeHandler);
            }
        }
        
        new ReloadTranslationResults(workspace.getCurrentDocument().getId(), workspace);

    }

    /**
     * Save the user translation of the given chunk (no matter whether it is
     * user's own translation or a suggestion taken over or post-edited). The ID
     * of the TranslationPair chosen for post-editing is also sent, providing
     * feedback which then can be used to improve future suggestions. If in
     * Offline Mode, the user translation is not sent to User Space but is saved
     * in the Local Storage.
     */
    public SetUserTranslation(ChunkIndex chunkIndex, long documentId,
            String userTranslation, long chosenTranslationPair, SubgestBox toUnlockBox, TranslationWorkspace workspace,
            String posteditedString, long chosenPosteditPairID, TranslationWorkspace.SourceChangeHandler sourceChangeHandler, TranslationWorkspace.TimeChangeHandler timeChangeHandler) {
        super();

        this.chunkIndex = chunkIndex;
        this.documentId = documentId;
        this.userTranslation = userTranslation;
        this.chosenTranslationPair = chosenTranslationPair;

        this.toUnlockBox = toUnlockBox;
        this.workspace = workspace;

        this.posteditedString = posteditedString;
        this.chosenPosteditPairID = chosenPosteditPairID;

        this.sourceChangeHandler = sourceChangeHandler;
        this.timeChangeHandler = timeChangeHandler;

        enqueue();
    }

    public SetUserTranslation(ChunkIndex chunkIndex, long documentId,
            String userTranslation, long chosenTranslationPair, SubgestBox toUnlockBox, TranslationWorkspace workspace,
            SubgestBox toLockBox, String posteditedString, long chosenPosteditPairID,
            TranslationWorkspace.SourceChangeHandler sourceChangeHandler, TranslationWorkspace.TimeChangeHandler timeChangeHandler) {
        super();

        this.chunkIndex = chunkIndex;
        this.documentId = documentId;
        this.userTranslation = userTranslation;
        this.chosenTranslationPair = chosenTranslationPair;

        this.toUnlockBox = toUnlockBox;

        this.toLockNext = true;
        this.toLockBox = toLockBox;

        this.workspace = workspace;

        this.posteditedString = posteditedString;
        this.chosenPosteditPairID = chosenPosteditPairID;

        this.sourceChangeHandler = sourceChangeHandler;
        this.timeChangeHandler = timeChangeHandler;

        enqueue();
    }

    @Override
    protected void call() {

        filmTitService.setUserTranslation(Gui.getSessionID(), chunkIndex,
                documentId, userTranslation, chosenTranslationPair, posteditedString, chosenPosteditPairID,
                this);
    }
}
