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

import java.io.Serializable;

/**
 * A unique identifier of a chunk within a document.
 */
public class ChunkIndex implements com.google.gwt.user.client.rpc.IsSerializable, Serializable, Comparable<ChunkIndex> {

    /**
     * Number indicating which part of subtitle item is the chunk part of.
     */
    private volatile int partNumber;

    /**
     * Gets which part of the subtitle item is the chunk part of.
     *
     * @return Number of part of the subtitle item is the chunk part of
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * Id of a subtitle item.
     */
    private volatile int id;

    /**
     * Gets the subtitle item ID.
     *
     * @return Subtitle item ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Default constructor for GWT.
     */
    public ChunkIndex() {
        partNumber = 0;
        id = 0;
    }

    /**
     * Creates a chunk index of given properties.
     *
     * @param partNumber Number which part of subtitle item is the chunk part
     * of.
     * @param id Subtitle item id.
     */
    public ChunkIndex(Integer partNumber, Integer id) {
        if (partNumber == null) {
            partNumber = 0;
        }
        if (id == null) {
            id = 0;
        }

        this.partNumber = partNumber;
        this.id = id;
    }

    /**
     * Gets chunk index of a given timed chunk
     *
     * @param tc A timed chunk
     */
    public ChunkIndex(TimedChunk tc) {
        this.partNumber = tc.getPartNumber();
        this.id = tc.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != getClass()) {
            return false;
        }

        ChunkIndex pol = (ChunkIndex) o;
        return (pol.partNumber == partNumber && pol.id == id);

    }

    @Override
    public String toString() {
        return new Integer(partNumber).toString() + " | " + new Integer(id).toString();
    }

    /**
     * Gets hash code as a linear combination of part number and subtitle item
     * id.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        Integer r = partNumber * 13 + id * 53;
        return r.hashCode();
    }

    /**
     * Compares two chunk indexes first by their subtitle items ids and then by
     * the part number.
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(ChunkIndex other) {
        if (other.id == id) {
            return partNumber - other.partNumber;
        } else {
            return id - other.id;
        }
    }

}
