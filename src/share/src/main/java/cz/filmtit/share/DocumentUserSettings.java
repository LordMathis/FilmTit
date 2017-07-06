/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
