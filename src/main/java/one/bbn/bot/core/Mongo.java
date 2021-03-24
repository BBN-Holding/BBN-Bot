package one.bbn.bot.core;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Mongo {

    MongoClient client;
    Config config;

    public Mongo(Config config) {
        this.config = config;
    }

    public void connect() {
        client = MongoClients.create("mongodb://" + config.getDBUsername() + ":" + config.getDBPassword() + "@"+config.getDBHost()+":" + config.getDBPort() + "/?authSource=admin&authMechanism=MONGODB-CR");
    }

    public List<JSONObject> getVoicestatsData(String guildid) {
        MongoCollection<Document> collection = client.getDatabase("VoiceAnalyzer").getCollection("members");

        BasicDBObject where = new BasicDBObject();
        where.put("guildid", guildid);
        FindIterable<Document> it = collection.find(where);

        List<JSONObject> documents = new ArrayList<>();
        it.cursor().forEachRemaining(document -> documents.add(new JSONObject(document.toJson())));

        return documents;
    }

}

