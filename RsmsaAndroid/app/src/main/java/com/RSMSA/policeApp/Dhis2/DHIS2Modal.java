package com.RSMSA.policeApp.Dhis2;
import android.util.Log;
import com.RSMSA.policeApp.Dhis2.Data.DataElements;
import com.RSMSA.policeApp.Dhis2.Data.Program;
import com.RSMSA.policeApp.JSONParser;
import com.RSMSA.policeApp.MainOffence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilakoze on 4/14/2015.
 */
public class DHIS2Modal {
    private static final String TAG = DHIS2Config.class.getSimpleName();
    private String modalName;
    private JSONArray relations;
    private String username,password;


    public DHIS2Modal(String modalName, JSONArray relations, String username, String password) {
        this.modalName = modalName;
        this.relations = relations;
        this.username = username;
        this.password = password;
        Log.d(TAG, "Program = "+modalName);
    }

    public String getModelName() {
        return modalName;
    }

    /**
     * Get a program from the list of dhis2 programs by its name
     * @param name
     * @return Program object
     */
    public Program getProgramByName(String name){
        name = name.replace("_"," ");
        for(int i = 0;i < MainOffence.programs.size();i++){
            if(MainOffence.programs.get(i).getName().equals(name)){
                return MainOffence.programs.get(i);
            }
        }
        return null;
    }

    /**
     * Get a data element from the list of dhis2 dataElements by its id
     * @param id
     * @return
     */
    public DataElements getDataElement(String id) {
        for (int i = 0; i < MainOffence.dataElements.size(); i++) {
            if (MainOffence.dataElements.get(i).getId().equals(id)) {
                return MainOffence.dataElements.get(i);
            }
        }
        return null;
    }

    /**
     * Get a data element from the list of dhis2 dataElements by its name
     * @param name
     * @return
     */
    public DataElements getDataElementByName(String name) {
        for (int i = 0; i < MainOffence.dataElements.size(); i++) {
            if (MainOffence.dataElements.get(i).getName().equals(name)) {
                return MainOffence.dataElements.get(i);
            }
        }
        return null;
    }

    /**
     * Gets all rows of a program
     */
    public JSONArray getAllEvents (){
        JSONArray allEvents = new JSONArray();
        Program program = getProgramByName(modalName);
        JSONParser jsonParser = new JSONParser();
        JSONObject  jsonObject = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL + "api/events?program=" + program.getId(), "GET", username, password);
        JSONArray events = null;
        try {
            events = jsonObject.getJSONArray("events");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int j = 0; j < events.length(); j++) {
            JSONObject event = null;
            try {
                event = events.getJSONObject(j);
                allEvents.put(j, renderToJSON(event));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return allEvents;
    }

    public JSONObject renderToJSON(JSONObject event) throws JSONException {
        JSONArray dataValues = null;
        JSONObject renderedJson= new JSONObject();
        String id = "";
        try {
            dataValues = event.getJSONArray("dataValues");
            id = event.getString("event");
            renderedJson.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0; i<dataValues.length();i++){
            JSONObject dataValue = null;
            try {
                dataValue = dataValues.getJSONObject(i);
                String name = getDataElement(dataValue.getString("dataElement")).getName();
                if(name.startsWith(DHIS2Config.REFERENCE_PREFIX)){
                    String program = name.substring(DHIS2Config.REFERENCE_PREFIX.length());
                    DHIS2Modal programDHIS2Modal = new DHIS2Modal(program, null, username, password);
                    JSONObject  object = programDHIS2Modal.findEventById(dataValue.getString("value"));
                    renderedJson.put(name,object);
                }else{
                    String value = dataValue.getString("value");
                    renderedJson.put(name,value);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int count = 0;
        try {
            count = relations.length();
            Log.d(TAG,"rendered reprogram = "+modalName);
            Log.d(TAG,"rendered relations = "+relations.toString());
        }catch (NullPointerException e){
            Log.d(TAG," relation "+e.getMessage());
        }
        for (int k = 0; k < count ; k++) {
            //For each relation
            JSONObject relation = null;
            try {
                relation = relations.getJSONObject(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            DHIS2Modal programModal = null;
            String relationType = null;
            try {
                relationType = relation.getString("type");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String relationName = null;
            try {
                relationName = relation.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(relationType.equals("ONE_MANY")){
                //If relationship is one to many
                programModal = new DHIS2Modal(relationName,null,username, password);
            }else if(relationType.equals("MANY_MANY")){
                //If relationship is many to many
                //Create modal with one to many relation with the pivot entity
                String pivotTableName = null;
                try {
                    pivotTableName = relation.getString("pivot");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject pivotRelation = new JSONObject();
                try {
                    pivotRelation.put("name",relationName);
                    pivotRelation.put("type","ONE_MANY");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray pivotRelations = new JSONArray();
                try {
                    pivotRelations.put(0,pivotRelation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                programModal = new DHIS2Modal(pivotTableName,pivotRelations, username, password);
            }
            JSONObject where = new JSONObject();
            try {
                where.put("value",id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"printed id = "+id);
            renderedJson.put(programModal.modalName,programModal.getEvent(where));
        }
        return renderedJson;

    }


    /**
     * Find events of a program by id
     * @param uid
     * @return
     */
    public JSONObject findEventById(String uid) {
        //Get program by name
        Program program = getProgramByName(modalName);
        //Get events of the program from the server
        JSONParser jsonParser = new JSONParser();
        JSONObject  jsonObject = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL + "api/events/" + uid + ".json", "GET", username, password);
        Log.d(TAG,"event by id = "+uid+"  :  "+jsonObject.toString());
        JSONObject renderedJson = null;
        try {
            renderedJson = renderToJSON(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return renderedJson;
    }


    /**
     * Search events of a program
     * @param where JsonObject
     * @return a JsonObject of the Event.
     */

    public JSONArray getEvent(JSONObject where){
        JSONArray events = new JSONArray();

        //Get program by name
        Program program = getProgramByName(modalName);
        //Get events of the program from the server
        JSONParser jsonParser = new JSONParser();
        JSONObject  jsonObject = jsonParser.dhis2HttpRequest(DHIS2Config.BASE_URL + "api/events?program=" + program.getId(), "GET", username, password);
        JSONArray eventsReceived = null;
        try {
            Log.d(TAG,"events of program "+modalName + " = "+jsonObject);
            eventsReceived = jsonObject.getJSONArray("events");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int length = 0;
        try {
            length = eventsReceived.length();
        }catch (Exception e){}
        for (int j = 0; j < length ; j++) {
            //For each event render to entity column json
            JSONObject event = null;
            try {
                event = eventsReceived.getJSONObject(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray dataValues = null;
            try {
                dataValues = event.getJSONArray("dataValues");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int valuesLength = dataValues.length();

            for (int k = 0; k < valuesLength; k++) {
                try {
                    JSONObject dataValue = dataValues.getJSONObject(k);
                    String whereValue = where.getString("value");
                    if(dataValue.getString("value").equals(whereValue)){ //Checks the conditions provided
                        Log.d(TAG, "rendering json : Modal = " +modalName);
                        events.put(renderToJSON(event));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return events;
    }


}
