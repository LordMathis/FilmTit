package cz.filmtit.client;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavWidget;
import com.google.gwt.core.client.*;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import cz.filmtit.share.*;
import cz.filmtit.share.parsing.*;
import org.vectomatic.file.File;
import org.vectomatic.file.FileList;
import org.vectomatic.file.FileReader;
import org.vectomatic.file.events.LoadEndEvent;
import org.vectomatic.file.events.LoadEndHandler;
import cz.filmtit.client.SubgestBox.FakeSubgestBox;
import com.google.gwt.cell.client.FieldUpdater;

import java.util.*;

/**
 * Entry point for the FilmTit GWT web application,
 * including the GUI creation.
 *
 * @author Honza Václ
 *
 */

public class Gui implements EntryPoint {

    ///////////////////////////////////////
    //                                   //
    //      Data fields                  //
    //                                   //
    ///////////////////////////////////////
    
	/**
	 * a singleton representing the Gui instance
	 */
	private static Gui gui;
	
	/**
	 * a singleton representing the Gui instance
	 */
	public static Gui getGui() {
		return gui;
	}

	/**
	 * handles especially the menu
	 */
    GuiStructure guiStructure;

 	/**
 	 * handles RPC calls
 	 */
    FilmTitServiceHandler rpcHandler;

 	/**
 	 * handles page switching
 	 */
    PageHandler pageHandler;
    
    
    // Login state fields

    boolean loggedIn = false;
    
    private String username;

    private String sessionID;
    
    private static final String SESSIONID = "sessionID";
    
    // persistent session ID via cookies (set to null to unset)
    protected void setSessionID(String newSessionID) {
         if (newSessionID == null) {
              Cookies.removeCookie(SESSIONID);
         } else {
              Cookies.setCookie(SESSIONID, newSessionID, getDateIn1Year());
         }
         sessionID = newSessionID;
    }

    // persistent session ID via cookies (null if not set)
    protected String getSessionID() {
         if (sessionID == null) {
              sessionID = Cookies.getCookie(SESSIONID);
         }
         return sessionID;
    }
    
    // Other fields

    /**
     * id of the active workspace
     */
    int currentWorkspaceId = -1;
    
    @SuppressWarnings("deprecation")
    public static Date getDateIn1Year() {
        // cookies should be valid for 1 year (GWT does not support anything better than the deprecated things it seems)
        Date in1year = new Date();
        in1year.setYear(in1year.getYear() + 1);
    	return in1year;
    }

    ///////////////////////////////////////
    //                                   //
    //      The "main()" of GUI          //
    //                                   //
    ///////////////////////////////////////

    @Override
    public void onModuleLoad() {
    	
    	// set the Gui singleton
    	Gui.gui = this;

		// RPC:
		rpcHandler = new FilmTitServiceHandler();

		// page loading and switching
		pageHandler = new PageHandler();

        // check whether user is logged in or not
        rpcHandler.checkSessionID();
    }

    
    ///////////////////////////////////////
    //                                   //
    //      Logging                      //
    //                                   //
    ///////////////////////////////////////
    
    long start=0;
    private StringBuilder sb = new StringBuilder();
    /**
     * Output the given text in the debug textarea
    * with a timestamp relative to the first logging.
     * @param logtext
     */
    public void log(String logtext) {
   	 if (guiStructure != null) {
	        if (start == 0) {
	            start = System.currentTimeMillis();
	        }
	        long diff = (System.currentTimeMillis() - start);
	        sb.append(diff);
	        sb.append(" : ");

	        sb.append(logtext);
	        sb.append("\n");
	        guiStructure.txtDebug.setText(sb.toString());
   	 }
   	 else {
       	 // txtDebug not created
   		 // but let's at least display the message in the statusbar
   		 // (does not work in Firefox according to documentation)
   		 Window.setStatus(logtext);
   	 }
    }

    void error(String errtext) {
         log(errtext);
    }
    
    /**
     * log and alert the exception
     * @param e the exception
     * @return the logged string
     */
    String exceptionCatcher(Throwable e) {
    	// TODO: for production, this should be:
    	// return exceptionCatcher(e, false, true)
    	return exceptionCatcher(e, true, true);    	
    }
    
    String exceptionCatcher(Throwable e, boolean alertIt) {
    	return exceptionCatcher(e, alertIt, true);
    }
    
    String exceptionCatcher(Throwable e, boolean alertIt, boolean logIt) {
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append(e.getLocalizedMessage());
    	sb.append('\n');
    	sb.append(e.toString());
    	sb.append('\n');
		StackTraceElement[] st = e.getStackTrace();
		for (StackTraceElement stackTraceElement : st) {
	    	sb.append(stackTraceElement);
	    	sb.append('\n');
		}
		
		String result = sb.toString();
		
		if (logIt) {
			log(result);
		}
		
		if (alertIt) {
			Window.alert("Exception caught!\n" + e + '\n' + e.getLocalizedMessage());
		}
		
		return result;
    }


    ///////////////////////////////////////
    //                                   //
    //      Login - logout methods       //
    //                                   //
    ///////////////////////////////////////
    
    /**
     * show a dialog enabling the user to
     * log in directly or [this line maybe to be removed]
     * via OpenID services
     */
	protected void showLoginDialog() {
		showLoginDialog("");
	}
     
    /**
     * show a dialog enabling the user to
     * log in directly or [this line maybe to be removed]
     * via OpenID services
     * @param username
     */
	protected void showLoginDialog(String username) {
	    LoginDialog loginDialog = new LoginDialog(username);
	}

    /**
     * show the registration dialog
     */
    protected void showRegistrationForm() {
        RegistrationForm registrationForm = new RegistrationForm();
    }

    protected void please_log_in () {
        logged_out ();
        Window.alert("Please log in first.");
        showLoginDialog();
    }
    
    protected void please_relog_in () {
        logged_out ();
        Window.alert("You have not logged in or your session has expired. Please log in.");
        showLoginDialog();
    }

    protected void logged_in (String username) {
    	// login state fields
    	loggedIn = true;
    	this.username = username;
        // actions
    	guiStructure.logged_in(username);
    	pageHandler.loadPage();
    }

    protected void logged_out () {
    	// login state fields
    	loggedIn = false;
        username = null;
        setSessionID(null);
        // actions
        guiStructure.logged_out();
        pageHandler.loadPage();
    }
    
}
