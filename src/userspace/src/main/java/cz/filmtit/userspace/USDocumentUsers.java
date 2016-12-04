/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.userspace;

import java.io.Serializable;

/**
 *
 * @author matus
 */
public class USDocumentUsers implements Serializable {

    private volatile Long id;
    private volatile Long userId;
    private volatile String moviePath;
    private volatile Boolean posteditOn;
    private volatile Boolean localFile;

    public USDocumentUsers() {
        //nothing;
    }

    public USDocumentUsers(Long userId) {
        this.userId = userId;
        this.moviePath = "";
        this.localFile = true;
        this.posteditOn = false;
    }

    public USDocumentUsers(Long userId, String moviePath, Boolean posteditOn, Boolean localFile) {
        this.userId = userId;
        this.moviePath = moviePath;
        this.posteditOn = posteditOn;
        this.localFile = localFile;
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
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return the moviePath
     */
    public String getMoviePath() {
        return this.moviePath;
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
        return this.posteditOn;
    }

    /**
     * @param posteditOn the posteditOn to set
     */
    public void setPosteditOn(Boolean posteditOn) {
        this.posteditOn = posteditOn;
    }

    /**
     * @return the localFile
     */
    public Boolean getLocalFile() {
        return localFile;
    }

    /**
     * @param localFile the localFile to set
     */
    public void setLocalFile(Boolean localFile) {
        this.localFile = localFile;
    }

}
