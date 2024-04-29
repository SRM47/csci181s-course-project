package org.healthhaven.model;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

	public static String sendDefaultPasswordEmail(String email, String password, String userType) {
		String subject = "Create Account";
		String body = String.format("This is to confirm that you are authorized to make an account as a %s. "
	            + "Your default password is %s. Please go to the login page and use this email and password.", userType, password);
		return sendEmail(email, subject, body);
	}
	
	public static String sendTOTPEmail(String email, String totp) {
		String subject = "Your One Time Passcode!";
		String body = "This is your One Time Passcode: \n" + totp;
		return sendEmail(email, subject, body);
		
	}
	
	private static String sendEmail(String toEmail, String subject, String body) {
        String fromEmail = "healthhaven845@gmail.com"; //requires valid gmail id
        String password = "fsnx gvno zvwk goip"; // correct password for gmail id

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", "465"); //SMTP Port

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        System.out.println("Session created");
        return sendEmail(session, toEmail, subject, body);

    }

    private static String sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("no_reply@healthhaven.com", "NoReply-healthhaven"));

            msg.setReplyTo(InternetAddress.parse("no_reply@healthhaven.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new java.util.Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);  

            return "SUCCESS";
            
        }
        catch (Exception e) {
            e.printStackTrace();
            return "FAILURE";
        }
    }
    
    public static void main(String args) {
    	EmailSender.sendEmail("saesae1005@icloud.com", "Test", "Test");
    }
}
