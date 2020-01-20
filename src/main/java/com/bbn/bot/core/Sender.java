package com.bbn.bot.core;

import com.bbn.bot.util.SECRETS;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Sender {

    private void sendPost(String uri, String json) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
        HttpEntity stringEntity = new StringEntity(json,ContentType.APPLICATION_JSON);
        httpPost.addHeader("Authorization", "OAuth " + SECRETS.APIKey);
        httpPost.setEntity(stringEntity);
        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String string = EntityUtils.toString(entity, "UTF-8");
            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMetric(String value, String timestamp, String metric_id) {
        sendPost(
                "https://api.statuspage.io/v1/pages/" + SECRETS.PageID + "/metrics/" + metric_id + "/data.json",
                new JSONObject().put("data", new JSONObject().put("timestamp", timestamp).put("value", value)).toString()
        );
    }

    private void sendState(String email, boolean online) {

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", SECRETS.SMTP_SERVER);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "25");

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(SECRETS.EMAIL));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email, false));

            msg.setSubject((online) ? "UP" : "DOWN");
            msg.setText("Gud Email");
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

            t.connect(SECRETS.SMTP_SERVER, SECRETS.USERNAME, SECRETS.PASSWORD);

            t.sendMessage(msg, msg.getAllRecipients());

            System.out.println("Response: " + t.getLastServerResponse());

            t.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setState(String email, boolean online) {
        this.sendState(email, online);
    }

}
