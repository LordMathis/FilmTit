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
public enum PosteditSource implements com.google.gwt.user.client.rpc.IsSerializable, Serializable {

    SEARCHANDREPLACE ("Search and Replace"),
    USERTRANSLATION ("User translation");
    
    private String description;

    
    PosteditSource(String description) {
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
