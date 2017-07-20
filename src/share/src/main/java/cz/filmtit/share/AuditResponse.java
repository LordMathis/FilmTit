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
