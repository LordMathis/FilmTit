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

package cz.filmtit.share.exceptions;

/**
 * Exception is thrown if source for translating  (eg. TM, Mosses...) is missing
 * @author Joachim Daiber
 */
public class SearcherNotAvailableException extends Exception {

    public SearcherNotAvailableException(String message) {
        super(message);
    }

    public SearcherNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }


}
