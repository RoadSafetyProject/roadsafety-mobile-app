package com.RSMSA.policeApp.Dhis2;

import com.RSMSA.policeApp.Dhis2.Data.DataElements;
import com.RSMSA.policeApp.Dhis2.Data.Program;
import com.RSMSA.policeApp.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilakoze on 4/14/2015.
 */
public class Model {
    private String modalName;
    private JSONArray relations;
    private List<Program> programs = new ArrayList<>();
    private List<DataElements> dataElements = new ArrayList<>();


    public Model(String modalName, JSONArray relations) {
        this.modalName = modalName;
        this.relations = relations;
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
        for(int i = 0;i < programs.size();i++){
            if(programs.get(i).getName().equals(name)){
                return programs.get(i);
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
        for (int i = 0; i < dataElements.size(); i++) {
            if (dataElements.get(i).getId().equals(id)) {
                return dataElements.get(i);
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
        JSONObject  jsonObject = jsonParser.makeHttpRequestReturnJsonObject(DHIS2Config.BASE_URL + "api/events?program=" + program.getId(), "GET");
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

    public JSONObject renderToJSON(JSONObject event){
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
                    Model programModel = new Model(program, null);
                    JSONObject  object = programModel.findEventById(dataValue.getString("value"));
                    renderedJson.put(name,object);
                }else{
                    String value = dataValue.getString("value");
                    renderedJson.put(name,value);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int k = 0; k < relations.length(); k++) {
            //For each relation
            JSONObject relation = null;
            try {
                relation = relations.getJSONObject(k);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Model programModal = null;
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
                programModal = new Model(relationName,null);
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

                programModal = new Model(pivotTableName,pivotRelations);
                //TODO not complete

            }
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
        JSONObject  jsonObject = jsonParser.makeHttpRequestReturnJsonObject(DHIS2Config.BASE_URL + "api/events/" + uid + ".json", "GET");
        return renderToJSON(jsonObject);
    }


    /**
     * Search events of a program
     *
     * @param object where (Search criteria)
     *
     * @param function onResult (Callback after the result is returned)
     *
     */
//TODO not complete.
//    this.get = function(where,onResult){
//        //Get program by name
//        var program = self.getProgramByName(self.modalName);
//        // Stores the rows of an entity
//        this.events = [];
//        var selfGet = this;
//        //Checks that all requests are made
//        this.count = [];
//        this.resultsFetched = function(){
//            if (selfGet.count.length == 0) {
//                onResult(selfGet.events);
//            }
//        }
//        //Get events of the program from the server
//        get(dhis2.config.baseUrl + "api/events?program="+program.id,function(result2){
//            for (j = 0; j < result2.events.length; j++) {//For each event render to entity column json
//                var event = result2.events[j];
//                for (k = 0; k < event.dataValues.length; k++) {
//                    if(event.dataValues[k].value == where.value){//Checks the conditions provided
//                        selfGet.count.push(1);
//                        //Render events to appropriate Modal
//                        self.renderToJSON(event, function(object) {
//                            //Push object to events
//                            selfGet.events.push(object);
//                            //Pop count to signify
//                            selfGet.count.pop();
//                            //Check if all results from the server are fetched
//                            selfGet.resultsFetched();
//                        });
//                    }
//                }
//            }
//            //Check if all results from the server are fetched
//            selfGet.resultsFetched();
//        });
//        return;
//    }


}
