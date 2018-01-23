package org.openshift.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.WriteConcern;
import org.openshift.data.DBConnection;
import org.openshift.model.Character;

/**
 * Created by spousty on 10/26/15.
 */

@Path("/")
public class RequestProcessor {

    @Inject
    private DBConnection dbConnection;

    //get all the characters
    @Path("admin")
    @GET()
    @Produces("application/json")
    public List getAllPlayers(){
        ArrayList allDDPeople = new ArrayList();
        DB db = dbConnection.getDB();
        DBCollection playerListCollection = db.getCollection("players");
        BasicDBObject sortDoc = new BasicDBObject("_id", -1);

        DBCursor cursor = playerListCollection.find().sort(sortDoc );
        try {
            while(cursor.hasNext()) {

                BasicDBObject dataValue = (BasicDBObject) cursor.next();
                dataValue.put("mongoid", dataValue.getObjectId("_id").toString());
                dataValue.remove("_id");
                allDDPeople.add(dataValue);

            }
        } finally {
            cursor.close();
        }

        return allDDPeople;
    }



    @POST
    @Path("players")
    @Consumes("application/json")
    @Produces("application/json")
    public HashMap insertACharacter(Character character){


        DB db = dbConnection.getDB();
        DBCollection playerListCollection = db.getCollection("players");
        BasicDBObject charDBObject = new BasicDBObject(character.toHashMap());
        try{
            playerListCollection.insert(charDBObject, WriteConcern.SAFE);
        } catch (Exception e) {
            System.out.println("threw an exception: " + e.getClass() + " :: " + e.getMessage());
        }

//        charDBObject.put("mongoid",  charDBObject.getObjectId("_id").toString());
//        charDBObject.remove("_id");
//        return charDBObject;

        final HashMap returnMap = new HashMap();
        returnMap.put("id", charDBObject.getObjectId("_id").toString());
        return returnMap;

    }
}
