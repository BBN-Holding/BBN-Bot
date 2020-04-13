/*
 * Copyright 2018-2020 GregTCLTK and Schlauer-Hax
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bbn.bot.core;

import com.bbn.bot.BBNBot;
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
        httpPost.addHeader("Authorization", "OAuth " + BBNBot.config.getAPIKey());
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
                "https://api.statuspage.io/v1/pages/" + BBNBot.config.getPageID() + "/metrics/" + metric_id + "/data.json",
                new JSONObject().put("data", new JSONObject().put("timestamp", timestamp).put("value", value)).toString()
        );
    }

    private void sendState(String email, boolean online) {

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", BBNBot.config.getSMTPServer());
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", "25");

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(BBNBot.config.getEMail()));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email, false));

            msg.setSubject((online) ? "UP" : "DOWN");
            msg.setText("Gud Email");
            msg.setSentDate(new Date());

            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");

            t.connect(BBNBot.config.getSMTPServer(), BBNBot.config.getUsername(), BBNBot.config.getPassword());
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setState(String email, boolean online) {
        this.sendState(email, online);
    }

}
