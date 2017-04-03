/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.share;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 * Represents response to Envers query
 * @author Matúš Námešný
 */
public class AuditResponse implements Serializable, IsSerializable{
    
    // Old version of Translation Result
    private TranslationResult translationResult;
    
    // Revision Number at which the old Translation Result was saved
    private Number number;

    public AuditResponse(TranslationResult result, Number number) {
        this.translationResult = result;       
        this.number = number;
    }
    
    public AuditResponse() {
        // do nothing
    }
    
    /**
     * @return the translationResult
     */
    public TranslationResult getTranslationResult() {
        return translationResult;
    }

    /**
     * @param translationResult the translationResult to set
     */
    public void setTranslationResult(TranslationResult translationResult) {
        this.translationResult = translationResult;
    }

    /**
     * @return the revision number
     */
    public Number getNumber() {
        return number;
    }

    /**
     * @param number the revision number to set
     */
    public void setNumber(Number number) {
        this.number = number;
    }
    
    @Override
    public String toString() {
        return translationResult.toString() + " revision number: " + number;
    }
}
