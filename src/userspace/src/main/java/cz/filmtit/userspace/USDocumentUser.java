/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.userspace;

import java.io.Serializable;

/**
 * Holds User Id and User's settings for a particular document
 * @author Matúš Námešný
 */
public class USDocumentUser implements Serializable {

    // Id of the USDocumentUser object
    private volatile Long id;
    
    // Id of the user
    private volatile Long userId;
    
    /*
        User's settings
    */
    
    // Path to video file
    private volatile String moviePath;
    
    // true if the postedit API is turned on (third column)
    private volatile Boolean posteditOn;
    
    // true if video file is on User's computer, false otherwise
    private volatile Boolean localFile;
    
    /*
        Constructors
    */
    
    // default constructor
    public USDocumentUser() {
        //nothing;
    }

    public USDocumentUser(Long userId) {
        this.userId = userId;
        this.moviePath = "";
        this.localFile = true;
        this.posteditOn = false;
    }

    public USDocumentUser(Long userId, String moviePath, Boolean posteditOn, Boolean localFile) {
        this.userId = userId;
        this.moviePath = moviePath;
        this.posteditOn = posteditOn;
        this.localFile = localFile;
    }
    
    /*
        Getters and setters
    */
    

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
