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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Config {

    private Path file;
    private JSONObject config;

    public Config(String path) {
        this.file = Paths.get(path);
    }

    public void load() {
        try {
            config = new JSONObject(new String(Files.readAllBytes(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return config.getString("Token");
    }

    public String getGitHubToken() {
        return config.getString("GitHubToken");
    }

    public JSONArray getBotIDs() {
        return config.getJSONArray("BotIDs");
    }

    public String getAPIKey() {
        return config.getString("APIKey");
    }

    public String getPageID() {
        return config.getString("PageID");
    }

    public String getDCGID() {
        return config.getString("DCG-ID");
    }

    public String getDCRID() {
        return config.getString("DCR-ID");
    }

    public String getSMTPServer() {
        return config.getString("SMTP-Server");
    }

    public String getEMail() {
        return config.getString("E-Mail");
    }

    public String getUsername() {
        return config.getString("Username");
    }

    public String getPassword() {
        return config.getString("Password");
    }

    public String getVoiceChannelID() {
        return config.getString("VCLOG_ID");
    }

    public String getLogChannelID() {
        return config.getString("LOGCHANNEL_ID");
    }

    public String getVerifyMessageID() {
        return config.getString("VERIFYMESSAGE_ID");
    }

    public String getCommunityRoleID() {
        return config.getString("COMMUNITYROLE_ID");
    }

    public String getUnVerifiedRoleID() {
        return config.getString("UNVERIFIEDROLE_ID");
    }

    public String getBotRoleID() {
        return config.getString("BOTROLE_ID");
    }

    public Map<String, Object> getSchoolPws() {
        return config.getJSONObject("SCHOOL_PWS").toMap();
    }

}
