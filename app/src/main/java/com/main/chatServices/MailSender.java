package com.main.chatServices;

import android.util.Log;
import android.widget.Toast;

import java.security.AccessController;
import java.security.Provider;
import java.security.Security;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender extends javax.mail.Authenticator {

    String userName, password;
    Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MailSender(String userName, String password) {
        this.userName = userName;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName , password);
            }
        });
    }

    public synchronized boolean sendMail(String ... message) {
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setSubject(message[0]);
            msg.setText(message[1]);
            msg.setFrom(new InternetAddress(message[2]));
            msg.setRecipients(Message.RecipientType.TO , message[3]);
            Transport.send(msg);
        } catch (Exception e) {
            Log.d("Email error", "sendMail: " + e.getMessage());
            return false;
        }
        return true;
    }


    public static final class JSSEProvider extends Provider {

        public JSSEProvider() {
            super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
            AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
                public Void run() {
                    put("SSLContext.TLS",
                            "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                    put("Alg.Alias.SSLContext.TLSv1", "TLS");
                    put("KeyManagerFactory.X509",
                            "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                    put("TrustManagerFactory.X509",
                            "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                    return null;
                }
            });
        }
    }
}

