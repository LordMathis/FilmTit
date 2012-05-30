package cz.filmtit.share;

/**
 * Created with IntelliJ IDEA.
 * User: josef.cech
 * Date: 17.5.12
 * Time: 6:31
 * To change this template use File | Settings | File Templates.
 */


import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TestDocument {
    private static Document doc;
    @BeforeClass
    public static void InitializeDoc() {
        doc = new Document("TestMovie","2012","cs");
        doc.setId(2012);
    }

    @Test
    public void TestSetId() {

        try{
          doc.setId(2013);
        }
        catch (Exception e) {
           assertEquals("Once the document ID is set, it cannot be changed.",e.getMessage());
        };


    }

    @Test
    public void TestGetters()
    {
       assertEquals("TestMovie",doc.getMovie().getTitle());
       assertEquals("2012",doc.getMovie().getYear());
       assertEquals("Czech",doc.getLanguage().getName());

    }


}