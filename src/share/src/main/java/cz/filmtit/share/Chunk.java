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
package cz.filmtit.share;

import cz.filmtit.share.annotations.AnnotationType;
import cz.filmtit.share.annotations.Annotation;
import cz.filmtit.share.parsing.Parser;

import com.google.gwt.user.client.rpc.GwtTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a subtitle chunk.
 *
 * @author Joachim Daiber
 */
public class Chunk implements com.google.gwt.user.client.rpc.IsSerializable, Serializable {

    /**
     * Flag if the chunk is active and therefore core should generate
     */
    public boolean isActive = true;

    private volatile String surfaceForm = "";

    protected List<Annotation> annotations;

    @GwtTransient
    private String[] tokens = null;

    /**
     * Gets flag if the tokenization of the chunk has been done.
     *
     * @return
     */
    public boolean isTokenized() {
        return (tokens != null);
    }

    /**
     * Sets the array of chunk tokens if the it has not been set before.
     *
     * @param tokens Array of chunk tokens.
     * @throws Exception Throws an exception if the resetting of tokenization is
     * attempted.
     */
    public void setTokens(String[] tokens) throws Exception {
        if (tokens == null) {
            throw new Exception("Cannot unset tokens");
        }
        if (isTokenized()) {
            throw new Exception("Cannot reset tokens.");
        }
        this.tokens = tokens;
    }

    /**
     * Gets tokenized chunk as an array of tokens.
     *
     * @return Array of tokens.
     * @throws Exception Throws an exception if the chunk hasn't been tokenized.
     */
    public String[] getTokens() throws Exception {
        if (isTokenized()) {
            return tokens;
        } else {
            throw new Exception("cannot get no tokens");

//            return new String[]{};
        }
    }

    /**
     * Default constructor for GWT.
     */
    public Chunk() {
        // nothing
    }

    /**
     * Crates chunk from surface form and annotations
     *
     * @param surfaceForm Chunks surface form
     * @param annotations Annotations of the chunk (e.g. if it is a dialog)
     */
    public Chunk(String surfaceForm, List<Annotation> annotations) {
        this.surfaceForm = surfaceForm.replace('\u0000', ' ');
        this.annotations = annotations;
    }

    /**
     * Creates a chunk from surface form.
     *
     * @param surfaceForm
     */
    public Chunk(String surfaceForm) {
        this.surfaceForm = surfaceForm.replace('\u0000', ' ');
    }

    /**
     * Gets only the string, with no annotations (newlines are turned into
     * spaces).
     *
     * @return Surface form of the chunk
     */
    public String getSurfaceForm() {
        return surfaceForm;
    }

    /**
     * Gets the string with annotations, as in an SRT file (newlines are turned
     * into |).
     *
     * @return Chunk string with annotations
     */
    public String getDatabaseForm() {
        return getFormatedForm("- ", " | ");
    }

    /**
     * Sets the string with annotations, as in an SRT file (newlines are turned
     * into |).
     *
     * @param form Chunk string with annotations
     */
    public void setDatabaseForm(String form) {
        copyFromOther(Parser.getChunkFromText(form));
    }

    /**
     * The string with annotations, as in an SRT file (newlines are turned into
     * |). Deletes previously existing format annotations.
     *
     * @param form Chunk string with annotations
     */
    public void setDatabaseFormForce(String form) {
        //how the heck is this different from the previous one?
        copyFromOther(Parser.getChunkFromText(form));
    }

    /**
     * Copies data from other chunk.
     *
     * @param other Chunk to copy data from
     */
    public void copyFromOther(Chunk other) {
        setSurfaceForm(other.getSurfaceForm());
        setFormattingAnnotations(other.getAnnotations());
    }

    /**
     * The string with annotations, newlines turned into newlineString, dialogue
     * dashes into dashString.
     *
     * @return
     */
    public String getFormatedForm(String dashString, String newlineString) {
        String displayForm = getSurfaceForm();

        //we are doing annots from left to right
        //and if we move the string, we have to move the positions too
        int movedAlready = 0;

        if (annotations != null) {
            Collections.sort(annotations);
            for (Annotation annotation : annotations) {
                int pos = annotation.getBegin() + movedAlready;
                switch (annotation.getType()) {
                    case DIALOGUE:
                        displayForm = displayForm.substring(0, pos) + dashString
                                + displayForm.substring(pos);
                        movedAlready += dashString.length();
                        break;
                    case LINEBREAK:
                        displayForm = displayForm.substring(0, pos) + newlineString
                                + displayForm.substring(pos + 1);
                        //+1 / -1, because in surfaceform, there is space after linebreak
                        //but we don't want the space here
                        movedAlready += newlineString.length() - 1;
                        break;
                }
            }
        }

        return displayForm;
    }

    public boolean isDialogue() {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.getType() == AnnotationType.DIALOGUE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Only the string, with no annotations (newlines are turned into spaces).
     *
     * @return Surface form of the chunk
     */
    public void setSurfaceForm(String surfaceform) {
        this.surfaceForm = surfaceform.replace('\u0000', ' ');
        /*if (this.surfaceForm.equals(surfaceForm)) { return; }
        if (this.surfaceForm == null || this.surfaceForm.equals("")) {
            this.surfaceForm = surfaceForm.replace('\u0000', ' ');
        }
        else {
            //throw new IllegalAccessException("The chunk surface form can be set just once.");
        	throw new IllegalArgumentException("The chunk surface form can be set just once.");
        }*/
    }

    /**
     * Displays the surface form of the Chunk.
     */
    @Override
    public String toString() {
        return "Chunk[" + surfaceForm + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Chunk)) {
            return false;
        }

        Chunk chunk = (Chunk) o;

        return surfaceForm.equals(chunk.surfaceForm);
    }

    @Override
    public int hashCode() {
        return surfaceForm.hashCode();
    }

    public List<Annotation> getAnnotations() {
        return annotations == null ? Collections.<Annotation>emptyList() : annotations;
    }

    public void clearAnnotations() {
        this.annotations.clear();
    }

    public void addAnnotation(Annotation annotation) {
        if (this.annotations == null) {
            this.annotations = new ArrayList<Annotation>();
        }

        this.annotations.add(annotation);
    }

    /**
     * Gets the string with annotations in HTML form (newlines are turned into
     * <br />).
     *
     * @return GUI form of the chunk text
     */
    public String getGUIForm() {
        return getFormatedForm("- ", "<br />");
    }

//    intended to be used where the text is displayed in a textarea but not used at the moment
//
//    /**
//     * The string with annotations in text form (newlines are turned into \n).
//     * @return
//     */
//    public String getTextForm(){
//        return getFormatedForm("- ", "\n");
//    }
//    
//    /**
//     * The string with annotations in text form (newlines are turned into \n).
//     * @return
//     */
//    public void setTextForm(String form){
//    	RegExp newline = RegExp.compile("[\n\r]+", "g");
//    	String dbForm = newline.replace(form, " | ");
//        setDatabaseForm(dbForm);
//    }
    /**
     * Adds new annotations, keeping the original ones.
     *
     * @param annotations Annotations to be added
     */
    public void addAnnotations(Collection<Annotation> annotations) {
        if (this.annotations == null) {
            this.annotations = new ArrayList<Annotation>();
        }

        this.annotations.addAll(annotations);
    }

    /**
     * Set new formatting annotations, deleting the previous ones.
     *
     * @param annotations Annotations to be set
     */
    public void setFormattingAnnotations(Collection<Annotation> annotations) {
        // remove old formatting annotations = readd non-formatting annotations
        List<Annotation> newAnnotations = new ArrayList<Annotation>();

        if (this.annotations != null) {
            for (Annotation annotation : this.annotations) {
                if (annotation.getType() == AnnotationType.ORGANIZATION
                        || annotation.getType() == AnnotationType.PERSON
                        || annotation.getType() == AnnotationType.PLACE) {

                    annotations.add(annotation);
                }
            }
        }
        // add new format annotations
        newAnnotations.addAll(annotations);
        // set
        this.annotations = newAnnotations;
    }

    /**
     * Removes annotation of given index in the list of annotations.
     *
     * @param index Annotation index
     */
    public void removeAnnotation(int index) {
        if (this.annotations != null) {
            this.annotations.remove(index);
        }
    }
}
