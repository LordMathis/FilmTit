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
package cz.filmtit.client.dialogs;

import java.util.ArrayList;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import cz.filmtit.client.callables.AddSubtitleItem;
import cz.filmtit.client.pages.TranslationWorkspace;
import cz.filmtit.share.ChunkIndex;
import cz.filmtit.share.Language;
import cz.filmtit.share.SrtTime;
import cz.filmtit.share.TimedChunk;
import cz.filmtit.share.TranslationResult;
import cz.filmtit.share.exceptions.InvalidValueException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matúš Námešný
 */
public class AddSubtitleItemDialog extends Dialog {

    private SrtTime startTimeWorking;
    private SrtTime endTimeWorking;

    private TranslationWorkspace workspace;

    private TimedChunk newChunk;

    private static AddSubtitleItemDialogUiBinder uiBinder = GWT.create(AddSubtitleItemDialogUiBinder.class);

    interface AddSubtitleItemDialogUiBinder extends UiBinder<Widget, AddSubtitleItemDialog> {
    }

    public AddSubtitleItemDialog(TranslationWorkspace workspace) {

        initWidget(uiBinder.createAndBindUi(this));

        this.workspace = workspace;

        try {
            startTimeWorking = new SrtTime(0, 0, 0, 0);
            endTimeWorking = new SrtTime(0, 0, 0, 0);

        } catch (InvalidValueException ex) {
            Logger.getLogger(AddSubtitleItemDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        initTable();
    }

    /**
     * create the table
     */
    private void initTable() {

        // create columns
        Column<SrtTime, String> hColumn = new Column<SrtTime, String>(new TextInputCell()) {
            @Override
            public String getValue(SrtTime time) {
                return time.getStringH();
            }
        };
        Column<SrtTime, String> mColumn = new Column<SrtTime, String>(new TextInputCell()) {
            @Override
            public String getValue(SrtTime time) {
                return time.getStringM();
            }
        };
        Column<SrtTime, String> sColumn = new Column<SrtTime, String>(new TextInputCell()) {
            @Override
            public String getValue(SrtTime time) {
                return time.getStringS();
            }
        };
        Column<SrtTime, String> tColumn = new Column<SrtTime, String>(new TextInputCell()) {
            @Override
            public String getValue(SrtTime time) {
                return time.getStringT();
            }
        };

        // add column styles
        hColumn.setCellStyleNames("numerical2digits");
        mColumn.setCellStyleNames("numerical2digits");
        sColumn.setCellStyleNames("numerical2digits");
        tColumn.setCellStyleNames("numerical3digits");

        // add column update handlers
        hColumn.setFieldUpdater(new FieldUpdater<SrtTime, String>() {
            @Override
            public void update(int index, SrtTime time, String value) {
                try {
                    time.setH(value);
                } catch (InvalidValueException e) {
                    Window.alert(e.getLocalizedMessage());
                }
            }
        });
        mColumn.setFieldUpdater(new FieldUpdater<SrtTime, String>() {
            @Override
            public void update(int index, SrtTime time, String value) {
                try {
                    time.setM(value);
                } catch (InvalidValueException e) {
                    Window.alert(e.getLocalizedMessage());
                }
            }
        });
        sColumn.setFieldUpdater(new FieldUpdater<SrtTime, String>() {
            @Override
            public void update(int index, SrtTime time, String value) {
                try {
                    time.setS(value);
                } catch (InvalidValueException e) {
                    Window.alert(e.getLocalizedMessage());
                }
            }
        });
        tColumn.setFieldUpdater(new FieldUpdater<SrtTime, String>() {
            @Override
            public void update(int index, SrtTime time, String value) {
                try {
                    time.setT(value);
                } catch (InvalidValueException e) {
                    Window.alert(e.getLocalizedMessage());
                }
            }
        });

        // add columns to table
        //timesTable = new CellTable<SrtTime>();
        timesTable.addColumn(hColumn, "hour");
        timesTable.addColumn(mColumn, "minute");
        timesTable.addColumn(sColumn, "second");
        timesTable.addColumn(tColumn, "milisecond");

        // add the data
        ArrayList<SrtTime> rowData = new ArrayList<SrtTime>(2);
        rowData.add(startTimeWorking);
        rowData.add(endTimeWorking);
//		timesTable.setRowData(rowData);
//		timesTable.setRowCount(2, true);
        //timesTable.setVisibleRange(new Range(0, 2));
        timesTable.setRowData(0, rowData);

        // show the table
        timesTable.redraw();
    }

    @UiField
    Form timesForm;

    @UiField
    TextBox sourceText;

    @UiField
    CellTable<SrtTime> timesTable;

    @UiField
    Paragraph timeValue;

    @UiField
    Button cancelButton;

    @UiHandler("cancelButton")
    void cancel(ClickEvent e) {
        dialogBox.hide();
    }

    @UiField
    SubmitButton submitButton;

    @UiHandler("timesForm")
    void submit(Form.SubmitEvent e) {

        deactivateBtn();

        if (checkTimes() && checkSourceString()) {

            int newId = getMaxId() + 1;
            Language language = workspace.getCurrentDocument().getLanguage();

            newChunk = new TimedChunk(startTimeWorking.toString().replaceAll(" ", ""),
                    endTimeWorking.toString().replaceAll(" ", ""),
                    1, sourceText.getValue(),
                    newId, workspace.getCurrentDocument().getId());

            newChunk.setDocumentId(workspace.getCurrentDocument().getId());

            new AddSubtitleItem(newChunk, workspace);

            dialogBox.hide();
        } else {
            reactivateBtn();
        }

    }

    private int getMaxId() {
        Map<ChunkIndex, TranslationResult> translationResults = workspace.getCurrentDocument().getTranslationResults();

        ChunkIndex max = null;

        for (ChunkIndex key : translationResults.keySet()) {
            if (max == null) {
                max = key;
            } else {
                if (key.compareTo(max) > 0) {
                    max = key;
                }
            }
        }

        if (max == null) {
            return 0;
        }

        return max.getId();
    }

    /**
     * Check whether the timing is OK.
     *
     * @return
     */
    private boolean checkTimes() {
        if (startTimeWorking.compareTo(endTimeWorking) == -1) {
            return true;
        } else {
            Window.alert("The beginning time must be earlier than the end time!");
            return false;
        }
    }

    private boolean checkSourceString() {
        if (sourceText.getValue().isEmpty()) {
            Window.alert("Source text cannot be empty");
            return false;
        } else {
            return true;
        }
    }

    private void deactivateBtn() {
        submitButton.setEnabled(false);
    }

    private void reactivateBtn() {
        submitButton.setEnabled(true);
    }
}
