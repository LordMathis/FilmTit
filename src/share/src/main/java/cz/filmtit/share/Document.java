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
package cz.filmtit.share;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a subtitle file.
 *
 * @author Jindřich Libovický, Karel Bílek, Rudolf Rosa, Jan Václ, Matúš Námešný
 */
public class Document implements IsSerializable, Serializable, Comparable<Document> {

    // Generated by Userspace (i.e. probably by the database)
    /**
     * Database ID of the document used also as an identifier in communication.
     */
    private volatile long id = Long.MIN_VALUE;
    /**
     * The document title
     */
    private volatile String title;
    /**
     * Movie or TV show the documents is subtitle of
     */
    private volatile MediaSource movie;
    /**
     * Source language of the subtitles
     */
    private volatile Language language;
    /**
     * Time of last change of the document
     */
    private volatile long lastChange;
    /**
     * Number of chunks in the document
     */
    private volatile int totalChunksCount;
    /**
     * Number of chunks that has been already translated
     */
    private volatile int translatedChunksCount;
    /**
     * Database ID od the user owning the document
     */
    private long userId = Long.MIN_VALUE;

    /**
     * Map of the translation results the document consists of
     */
    public TreeMap<ChunkIndex, TranslationResult> translationResults = new TreeMap<ChunkIndex, TranslationResult>();

    /**
     * Default constructor required by GWT.
     */
    public Document() {
        // nothing
    }

    /**
     * Gets the document title
     *
     * @return The document title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the document title
     *
     * @param title The document title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Creates a document of given properties.
     *
     * @param title Document title
     * @param langCode Code of the source language
     */
    public Document(String title, String langCode) {
        this.title = title;
        this.language = Language.fromCode(langCode);
    }

    /**
     * Gets the database ID.
     *
     * @return Database ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the database ID if it has not been set before. Throws an excpetion
     * otherwise.
     *
     * @param id Database ID
     */
    public void setId(long id) {
        if (this.id == id) {
            return;
        }
        if (this.id != Long.MIN_VALUE) {
            throw new UnsupportedOperationException("Once the document ID is set, it cannot be changed.");
        }
        this.id = id;
    }

    /**
     * Gets the media source of the document
     *
     * @return Media source of the document
     */
    public MediaSource getMovie() {
        return movie;
    }

    /**
     * Sets the media source of the document
     *
     * @param movie Media source of the document
     */
    public void setMovie(MediaSource movie) {
        this.movie = movie;
    }

    /**
     * Gets the source language of the document
     *
     * @return Source language of the document
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Sets the the source language of the document based on the language code
     *
     * @param languageCode Language code of the source language of the document
     */
    public void setLanguageCode(String languageCode) {
        language = Language.fromCode(languageCode);
    }

    /**
     * Gets ID of the user owning the document
     *
     * @return ID of the owner of the document
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets ID of the user owning the document if it has not been set before,
     * throws an exception otherwise.
     *
     * @param userId ID of the owner of the document
     */
    public void setUserId(long userId) {
        if (this.userId == userId) {
            return;
        }
        if (this.userId != Long.MIN_VALUE) {
            throw new UnsupportedOperationException("Once the owner ID is set, it cannot be changed.");
        }
        this.userId = userId;
    }

    /**
     * Gets time of the documents last change
     *
     * @return Time of the documents last change
     */
    public long getLastChange() {
        return lastChange;
    }

    /**
     * Sets time of the documents last change
     *
     * @param lastChange Time of the documents last change
     */
    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    /**
     * Gets count of the document chunks.
     *
     * @return Count of the document chunks.
     */
    public int getTotalChunksCount() {
        return totalChunksCount;
    }

    /**
     * Sets count of the document chunks.
     *
     * @param totalChunksCount Count of the document chunks.
     */
    public void setTotalChunksCount(int totalChunksCount) {
        this.totalChunksCount = totalChunksCount;
    }

    /**
     * Gets the number of document chunks that has been already translated.
     *
     * @return Number of document chunks that has been already translated.
     */
    public int getTranslatedChunksCount() {
        return translatedChunksCount;
    }

    /**
     * Sets the number of document chunks that has been already translated.
     *
     * @param translatedChunksCount Number of document chunks that has been
     * already translated.
     */
    public void setTranslatedChunksCount(int translatedChunksCount) {
        this.translatedChunksCount = translatedChunksCount;
    }

    /**
     * Gets the translation results the document consist of as list sorted by
     * the chunks timing.
     *
     * @return List of tranlsation results
     */
    public List<TranslationResult> getSortedTranslationResults() {
        List<TranslationResult> res = new ArrayList<TranslationResult>(translationResults.size());
        //sorted because treeset
        for (ChunkIndex i : translationResults.keySet()) {
            res.add(translationResults.get(i));
        }
        return res;
    }

    /**
     * Gets the translation results of the document as map of chunk indexes and
     * translation results.
     *
     * @return Map of translation results
     */
    public Map<ChunkIndex, TranslationResult> getTranslationResults() {
        return translationResults;
    }

    /**
     * Return the document without translation results. If the translation
     * results are loaded in the document, a clone of the document not
     * containing them is created.
     *
     * @return Document without translation results.
     */
    public Document documentWithoutResults() {
        if (translationResults == null || translationResults.size() == 0) {
            return this;
        }

        Document clone = new Document();

        clone.id = id;
        clone.language = language;
        clone.movie = movie;
        clone.title = title;
        clone.userId = userId;
        clone.lastChange = lastChange;
        clone.totalChunksCount = totalChunksCount;
        clone.translatedChunksCount = translatedChunksCount;

        return clone;
    }

    /**
     * Compares the document with a different one to order them by the time of
     * the last change in the descending order.
     *
     * @param other Document to be compared
     */
    @Override
    public int compareTo(Document other) {
        if (this.lastChange > other.lastChange) {
            return -1;
        }
        if (this.lastChange < other.lastChange) {
            return 1;
        }
        return 0;
    }

    /**
     * Sorts Translation Results by their timing
     * @return sorted Translation Results
     */
    public List<TranslationResult> getSortedResultsByTime() {
        List<TranslationResult> preSorted = new ArrayList<TranslationResult>();
        preSorted.addAll(translationResults.values());

        Collections.sort(preSorted, new Comparator<TranslationResult>() {
            @Override
            public int compare(TranslationResult o1, TranslationResult o2) {
                return o1.getSourceChunk().compareTo(o2.getSourceChunk());
            }
        });

        return preSorted;
    }

}
