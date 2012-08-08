package cz.filmtit.userspace;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: josef.cech
 * Date: 7.8.12
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public class LogMessage extends  DatabaseObject {
    private int messageType;
    private String context;
    private String message;
    private Date date;

    private LogMessage(){
        messageType = -1;
        context = new String();
        message = new String();
        date = new Date();
    }

    public LogMessage(int type, String context , String message, Date logTime){
     setMessageType(type);
     setContext(context);
     setDate(logTime);
     setMessage(message);
    }

    public void setMessageType(int type) {
        this.messageType = type;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(Date logTime) {
        this.date = logTime;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }


    @Override
    protected long getSharedClassDatabaseId() {
        return databaseId;
    }

    @Override
    protected void setSharedClassDatabaseId(long databaseId) {
    }

    @Override
    public void saveToDatabase(Session session) {
        saveJustObject(session);
    }

    @Override
    public void deleteFromDatabase(Session session) {
       deleteJustObject(session);
    }
}
