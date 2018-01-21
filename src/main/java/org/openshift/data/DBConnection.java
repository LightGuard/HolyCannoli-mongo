package org.openshift.data;

import com.mongodb.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.net.UnknownHostException;
import java.util.Arrays;


/**
 * Created by spousty on 10/26/15.
 */

@Named
@ApplicationScoped
public class DBConnection {

    private DB mongoDB;
    private String test = "why greetings to you";


    public DBConnection() {
        super();

    }

    @PostConstruct
    public void afterCreate() {
        System.out.println("just see if we can say anything");

        String mongoHost = System.getenv("MONGODB_SERVICE_HOST");

        if (mongoHost == null || "".equals(mongoHost)){
            //we are not on openshift
            Mongo mongo = null;
            try {
                mongo = new Mongo( "localhost" , 27017 );
                mongoDB = mongo.getDB("javaws");
            } catch (UnknownHostException e) {
                System.out.println("Could not connect to Mongo on Localhost: " + e.getMessage());
            }


        } else {

            //on openshift
            String mongoport = System.getenv("MONGODB_SERVICE_PORT");
            String user = System.getenv("MONGODB_USER");
            String password = System.getenv("MONGODB_PASSWORD");
            String db = System.getenv("MONGODB_DATABASE");
            int port = Integer.decode(mongoport);

            //Make the server connection
            ServerAddress serverAddress = null;
            try {
                serverAddress = new ServerAddress(mongoHost, port);
            } catch (UnknownHostException e) {
                System.out.println("Couldn't connect to Mongo: " + e.getMessage() + " :: " + e.getClass());
            }

            //Make the credentials
            MongoCredential credential = MongoCredential.createCredential(user, db, password.toCharArray());

            //Use the server connection and the credentials to make a client
            MongoClient mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));

            //use the client to get a db
            mongoDB = mongoClient.getDB(db);

        }

    }

    public DB getDB(){
        return mongoDB;
    }

    public String getMessage(){
        return test;
    }


}
