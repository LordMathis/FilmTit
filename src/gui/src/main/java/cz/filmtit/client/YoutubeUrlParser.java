/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.client;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import cz.filmtit.share.LevelLogEnum;

/**
 *
 * @author Matus Namesny
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
