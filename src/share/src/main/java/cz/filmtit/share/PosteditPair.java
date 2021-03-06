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
package cz.filmtit.share;

import java.io.Serializable;

/**
 * Represents pair of chunks used in Postedit API
 * @author Matúš Námešný
 */
public class PosteditPair implements com.google.gwt.user.client.rpc.IsSerializable, Serializable {

    private Long id = (long) - 1;

    private Chunk originChunk;

    private Chunk posteditedChunk;

    private PosteditSource source;

    public PosteditPair() {
        // do nothing
    }

    public PosteditPair(Chunk chunk1, Chunk chunk2) {
        this.originChunk = chunk1;
        this.posteditedChunk = chunk2;
    }

    public PosteditPair(String userTranslation, String posteditedString) {
        this(new Chunk(userTranslation), new Chunk(posteditedString));
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the originChunk
     */
    public Chunk getOriginChunk() {
        return originChunk;
    }

    /**
     * @param originChunk the originChunk to set
     */
    public void setOriginChunk(Chunk originChunk) {
        this.originChunk = originChunk;
    }

    /**
     * @return the posteditedChunk
     */
    public Chunk getPosteditedChunk() {
        return posteditedChunk;
    }

    /**
     * @param posteditedChunk the posteditedChunk to set
     */
    public void setPosteditedChunk(Chunk posteditedChunk) {
        this.posteditedChunk = posteditedChunk;
    }

    public String getString2() {
        if (posteditedChunk == null) {
            return "";
        }
        return posteditedChunk.getSurfaceForm();
    }

    public String getString1() {
        if (originChunk == null) {
            return "";
        }
        return originChunk.getSurfaceForm();
    }

    public Double getScore() {
        return 0d;
    }

    /**
     * @return the source
     */
    public PosteditSource getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(PosteditSource source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "[PosteditPair: " + originChunk.getSurfaceForm() + " -> " + posteditedChunk.getSurfaceForm() + "]";
    }

}
