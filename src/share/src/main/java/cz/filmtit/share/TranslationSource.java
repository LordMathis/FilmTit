package cz.filmtit.share;
import java.io.Serializable;

/**
 * @author Joachim Daiber
 */
public enum TranslationSource implements com.google.gwt.user.client.rpc.IsSerializable, Serializable {

    //Internal:
    INTERNAL_EXACT ("Exact TM match"),
    INTERNAL_NE    ("NE based TM match"),
    INTERNAL_FUZZY ("Fuzzy TM match"),

    //External:
    EXTERNAL_TM    ("External TM match"),
    EXTERNAL_MT    ("External MT"),

    //Multiple
    MULTIPLE       ("Multiple sources"),

    //Unknown:
    UNKNOWN        ("Unkown source");

    private String description;

    TranslationSource(String description) {
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
