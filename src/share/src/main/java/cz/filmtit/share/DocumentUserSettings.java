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

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 * Holds user§s settings for a given document
 * @author Matúš Námešný
 */
public class DocumentUserSettings implements Serializable, IsSerializable {

    private volatile Long userId;
    private volatile String moviePath;
    private volatile Boolean posteditOn;
    private volatile Boolean isLocalFile;
    private volatile Boolean autoplay;

    public DocumentUserSettings() {
        // do nothing
    }

    public DocumentUserSettings(Long userId, String moviePath, Boolean posteditOn, Boolean isLocalFile, Boolean autoplay) {
        this.userId = userId;
        this.moviePath = moviePath;
        this.posteditOn = posteditOn;
        this.isLocalFile = isLocalFile;
        this.autoplay = autoplay;
    }

    /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @return the moviePath
     */
    public String getMoviePath() {
        return moviePath;
    }

    /**
     * @param moviePath the moviePath to set
     */
    public void setMoviePath(String moviePath) {
        this.moviePath = moviePath;
    }

    /**
     * @return the posteditOn
     */
    public Boolean getPosteditOn() {
        return posteditOn;
    }

    /**
     * @param posteditOn the posteditOn to set
     */
    public void setPosteditOn(Boolean posteditOn) {
        this.posteditOn = posteditOn;
    }

    /**
     * @return the isLocalFile
     */
    public Boolean isLocalFile() {
        return isLocalFile;
    }

    /**
     * @param localFile the isLocalFile to set
     */
    public void setLocalFile(Boolean localFile) {
        this.isLocalFile = localFile;
    }

    /**
     * @return the autoplay
     */
    public Boolean getAutoplay() {
        return autoplay;
    }

    /**
     * @param autoplay the autoplay to set
     */
    public void setAutoplay(Boolean autoplay) {
        this.autoplay = autoplay;
    }

}
