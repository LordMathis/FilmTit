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
package cz.filmtit.client;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import cz.filmtit.share.LevelLogEnum;

/**
 * extracts YouTube video ID from URL
 * @author Matúš Námešný
 */
public class YoutubeUrlParser {

    public static String parse(String ytUrl) {
        String videoId = null;

        RegExp regexp = RegExp.compile("^.*(youtu.be\\/|v\\/|e\\/|u\\/\\w+\\/|embed\\/|v=)([^#\\&\\?]*).*", "i");
        MatchResult match = regexp.exec(ytUrl);

        if (match != null) {
            videoId = match.getGroup(2);
        }

        Gui.log(LevelLogEnum.Notice, "YoutubeUrlParser", ytUrl + " " + videoId);

        return videoId;
    }
}
