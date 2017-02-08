/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.share;

import java.io.Serializable;

/**
 *
 * @author matus
 */
public class PosteditSource implements com.google.gwt.user.client.rpc.IsSerializable, Serializable {

    private String description;

    /**
     * Default GWT constructor
     */
    public PosteditSource() {
    }  
    
    public PosteditSource(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TranslationSource[" + description + "]";
    }

    public String getDescription() {
        return description;
    }
}
