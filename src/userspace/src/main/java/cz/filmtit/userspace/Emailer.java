package cz.filmtit.userspace;

/**
 * Created with IntelliJ IDEA.
 * User: josef.cech
 * Date: 28.7.12
 * Time: 18:07
 *
 */


import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.util.Properties;
public class Emailer {
     private static Properties configuration = new Properties();

     private String email;
     private String header;
     private String message;

 public Emailer(String email , String subject , String message)
 {
     fetchConfig();
     setData(email,subject,message);
 }
 public Emailer()
 {
     fetchConfig();
 }

 public void setData(String email,String subject ,String message){
     this.email= email;
     this.message= message;
     this.header = subject;
 }

 public boolean  send(){
     if (isFilled())
     {
         // send mail;
         javax.mail.Session session;


         System.out.println("Create session for mail login "+(String)configuration.getProperty("mail.filmtit.address") +" password"+ (String)configuration.getProperty("mail.filmtit.password"));
         session = javax.mail.Session.getDefaultInstance(configuration, new javax.mail.Authenticator() {
             protected PasswordAuthentication getPasswordAuthentication() {
                 return new PasswordAuthentication((String)configuration.getProperty("mail.filmtit.address"), (String)configuration.getProperty("mail.filmtit.password"));
             }
         });
         session.setDebug(true);
         javax.mail.internet.MimeMessage message = new  javax.mail.internet.MimeMessage(session);
         try {

             message.addRecipient(
                     Message.RecipientType.TO ,  new InternetAddress(this.email)
             );
             message.setFrom(new InternetAddress((String)configuration.getProperty("mail.filmtit.address")));
             message.setSubject(this.header);
             message.setText(this.message);

             //TestGmail.send();

             Transport transportSSL = session.getTransport();
             transportSSL.connect((String)configuration.getProperty("mail.smtps.host"), Integer.parseInt(configuration.getProperty("mail.smtps.port")), (String)configuration.getProperty("mail.filmtit.address"), (String)configuration.getProperty("mail.filmtit.password")); // account used
             transportSSL.sendMessage(message, message.getAllRecipients());
             transportSSL.close();
         } catch (MessagingException ex)
         {
             System.err.println("Cannot send mail " + ex);

         }
         return true;

     }
     System.out.println("Not all data filed");
     return false;
 }


 public boolean sendRegistrationMail(String recipier , String login , String pass){
     String messageTemp = (String)configuration.getProperty("mail.filmtit.registrationBody");
     String message = messageTemp.replace("%userlogin%",login).replace("%userpass%",pass);
     String subject = (String)configuration.getProperty("mail.filmtit.registrationSubject");
     return sendMail(recipier, subject, message);
 }

 public boolean sendForgottenPassMail(String recipier , String login , String url){
        String messageTemp = (String)configuration.getProperty("mail.filmtit.forgottenPassBody");
        String message = messageTemp.replace("%userlogin%",login).replace("%changeurl%",url);
        String subject = (String)configuration.getProperty("mail.filmtit.forgottenPassSubject");
        return sendMail(recipier,subject,message);
  }




 public boolean sendMail(String recipier ,  String header , String bodyMessage)
 {
     setData(recipier,header,bodyMessage);
     return send();
 }

 private boolean isFilled()
 {
       if (this.email == null || this.message==null || this.header==null)
       {
           return false;
       }
      return  !(this.email.isEmpty() || this.message.isEmpty() || this.header.isEmpty());
 }


    private void fetchConfig() {
        java.io.InputStream input = null;

        try {
            // read configuration file
            input = Emailer.class.getResourceAsStream("/cz/filmtit/userspace/configmail.xml");
            System.out.println(input);
            // setting config properties
            configuration.loadFromXML(input);
            } catch (IOException e) {
              System.err.println(e.toString());
              System.err.println("Can`t open configmail.xml with mail configuration");
            }

         finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.print("Can`t close file with mail configuration");
                }
            }
        }


    }

}