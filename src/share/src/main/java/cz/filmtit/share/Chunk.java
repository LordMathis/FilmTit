package cz.filmtit.share;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public String startTime;
    public String endTime;
    public String text;
    public String userTranslation;
    public int partNumber;
    public boolean done;
    
    public List<Match> matches;
    
    public Chunk() {
        matches = new ArrayList<Match>();
    }

    public Chunk(String text) {
    	this.text = text;
    	matches = new ArrayList<Match>();
    }
    
    public String getKey() {
        return startTime + "#" + Integer.toString(partNumber);
    }
    
    public int hashCode() {
        return (getKey()).hashCode();
    }
    
    public boolean equals(Object obj) {
        // can be compared just with another chunk
        if (obj.getClass() != this.getClass()) { return false; }
        Chunk other = (Chunk)obj;
        return (this.startTime.equals(other.startTime) &&
                this.partNumber == other.partNumber);
    }
}
