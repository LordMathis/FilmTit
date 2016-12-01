/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.share;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 *
 * @author matus
 */
public class DocumentUserSettings implements Serializable, IsSerializable {

    private volatile Long userId;
    private volatile String moviePath;
    private volatile Boolean posteditOn;

    public DocumentUserSettings() {
        // do nothing
    }
    
    public DocumentUserSettings(Long userId, String moviePath, Boolean posteditOn) {
        this.userId = userId;
        this.moviePath = moviePath;
        this.posteditOn = posteditOn;
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

}
