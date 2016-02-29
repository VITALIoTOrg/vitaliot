/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.vital.vitalcep.restApp.cepRESTApi;

import eu.vital.vitalcep.conf.PropertyLoader;
import eu.vital.vitalcep.entities.dolceHandler.DolceSpecification;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.DateFormat.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import org.bson.Document;
import org.json.JSONException;

// TODO: Auto-generated Javadoc
/**
 * The Class FilterFacadeREST.
 */
@Path("")
public class CEPIco {

    /** The Constant logger. */
    final static Logger logger = Logger.getLogger(CEPIco.class);
    
    private Properties config;
    
    private final PropertyLoader props;
    
    private  String host;
    
    private final String mongoIp;
    
    private final int mongoPort;
    
    private final String mongoDB;
    

    
   // @Context private javax.servlet.http.HttpServletRequest hsr;
    
    public CEPIco() throws IOException {

        props = new PropertyLoader();
        
        props.getProperty("cep.ip");
        
        String file = System
                .getProperty("jboss.server.config.dir")+"/rest_interface.conf";
        
        Properties config2 = new Properties() ;
        
        try {
            config2.load(new FileInputStream(file.toString()));
            config = config2;
            host = config.getProperty("cep-ip-address").concat(":8180");
        } catch (IOException e) {
              host = "localhost:8180";
        }
        
        
        mongoPort = Integer.parseInt(props.getProperty("mongo.port"));
        mongoIp = props.getProperty("mongo.ip");
        mongoDB = props.getProperty("mongo.db");
    }
    
    
    /**
     * Gets the filters.
     *
     * @return the filters
     */
    @GET
    @Path("getcepicos")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCEPICOs() {
        
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);

        MongoDatabase db = mongo.getDatabase(mongoDB);
        
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
        
        FindIterable<Document> coll = db.getCollection("ceps")
                .find(query).projection(fields);
       
        
        final JSONArray AllJson = new JSONArray(); 
        
        coll.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                AllJson.put(document);
            }
        });
                    
        return AllJson.toString();

    }
    
    /**
     * instantiate cep.
     *
     * @return the filters
     */
    @POST
    @Path("ceps")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startCEP(String dolce) throws FileNotFoundException, IOException {
        
        JSONObject body = new JSONObject(dolce);
        
        Properties props = new Properties();
        InputStream is = new FileInputStream(getClass().
                getProtectionDomain().getCodeSource().getLocation()
                .getPath() +"/conf/cep.properties");
        try {
            props.load(is);
        }
        finally {
            try {
                is.close();
            }
            catch (Exception e) {
                // ignore this exception
            }
        }
        PropertyConfigurator.configure(props);      
        
        if( !body.has("dolceSpecification")){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        String fileName = RandomStringUtils.randomAlphanumeric(8);
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(props.getProperty("cep.path")
                                +"/"+fileName+"_dolce"), "utf-8"))) {
            writer.write(body.getString("dolceSpecification"));
            writer.close();
        } catch (IOException ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } 
        
        int pid;

        
        try {
            Process pr = Runtime.getRuntime().exec("/lastCep/bcep -d /lastCep/pp -mi mqin -mo mqou nohop &");
            pid = getPid(pr);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();

        }
        
        return Response.status(Response.Status.OK).entity("{\"PID\":\""+
                pid+"\",\"file\":\""+fileName+"\"}").build();
    }

    public static int getPid(Process process) {
        try {
            Class<?> cProcessImpl = process.getClass();
            java.lang.reflect.Field fPid = cProcessImpl.getDeclaredField("pid");
            if (!fPid.isAccessible()) {
                fPid.setAccessible(true);
            }
            return fPid.getInt(process);
        } catch (Exception e) {
            return -1;
        }
    }
    /**
     * Creates a filter.
     *
     * @return the filter id 
     */
    @PUT
    @Path("createcepico")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCEPICO(String cepico) throws IOException {
        JSONObject jo = new JSONObject(cepico);
        if(!jo.has("source") ){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        MongoClient mongo = new MongoClient(mongoIp, mongoPort);
        MongoDatabase db = mongo.getDatabase(mongoDB);

        try {
           db.getCollection("ceps");
        } catch (Exception e) {
          //System.out.println("Mongo is down");
          mongo.close();
          return Response.status(Response
                            .Status.INTERNAL_SERVER_ERROR).build();

        }
        
        // create an empty query
        BasicDBObject query = new BasicDBObject(); 
        BasicDBObject fields = new BasicDBObject().append("_id",false);
        fields.append("dolceSpecification", false);
               
        if ( jo.has("dolceSpecification")) {
            
            //Filter oFilter = new Filter(filter);
            JSONObject dsjo = jo.getJSONObject("dolceSpecification");
            String str = dsjo.toString();//"{\"dolceSpecification\": "+ dsjo.toString()+"}";
            
            try{
                
                DolceSpecification ds = new DolceSpecification(str);
                
                if(ds instanceof DolceSpecification) {
                UUID uuid = UUID.randomUUID();
                    String randomUUIDString = uuid.toString();
                            
                    DBObject dbObject = 
                            createCEPSensor(cepico, randomUUIDString, dsjo);
                    
                   // String cepPath = "/home/a601149/workspace/BCEP/bcepCode/bcep/source";

                    //Runtime r = Runtime.getRuntime();

                    //Process p = r.exec(cepPath+"bcep -a");
                    
                    Document doc = new Document(dbObject.toMap());

                    try{
                        db.getCollection("ceps").insertOne(doc);
                        
                         JSONObject opState = createOperationalStateObservation(host
                            ,randomUUIDString);

                        DBObject oPut =  (DBObject)JSON.parse(opState.toString());
                        Document doc1 = new Document(oPut.toMap());

                        try{
                            db.getCollection("cepsobservations").insertOne(doc1);
                            String id = doc1.get("_id").toString();

                        }catch(MongoException ex
                                ){
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                    .build();
                        }

                   
                        JSONObject aOutput = new JSONObject();
                        aOutput.put("id", "http://"+ host.toString()+"/cep/sensor/"
                            +randomUUIDString);
                        return Response.status(Response.Status.OK)
                            .entity(aOutput.toString()).build();
                       
                    }catch(MongoException ex
                            ){
                        return Response.status(Response.Status.BAD_REQUEST)
                                .build();
                    }
      
                }else{
                    
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }catch(Exception e){
                 return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }   
        
        return Response.status(Response.Status.BAD_REQUEST).build();
          
    }

    private String getXSDDateTime(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        return  dateFormat.format(date);
    }

    private JSONObject createOperationalStateObservation(String host1, String randomUUIDString) throws JSONException {
        JSONObject opState = new JSONObject();
        opState.put("@context",
                "http://vital-iot.eu/contexts/measurement.jsonld");
        opState.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString + "/observation/1");
        opState.put("type","ssn:Observation");
        opState.put("ssn:featureOfInterest", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString);
        JSONObject property = new JSONObject();
        property.put("type","vital:OperationalState");
        opState.put("ssn:observationProperty",property);
        JSONObject resultTime = new JSONObject();
        Date date = new Date();
        resultTime.put("time:inXSDDateTime",getXSDDateTime(date));//check format
        opState.put("ssn:observationResultTime",resultTime);
        //"time:inXSDDateTime": "2015-10-14T11:59:11+02:00"
        JSONObject hasValue = new JSONObject();
        hasValue.put( "type","ssn:ObservationValue");
        hasValue.put( "value","vital:Running");
        JSONObject observationResult = new JSONObject();
        observationResult.put("ssn:hasValue",hasValue);
        observationResult.put("type","ssn:SensorOutput");
        opState.put("ssn:observationResult",observationResult);
        return opState;
    }
    

    private DBObject createCEPSensor(String cepico, String randomUUIDString, JSONObject dsjo) throws JSONException {
        DBObject dbObject = (DBObject) JSON.parse(cepico);
        dbObject.removeField("id");
        dbObject.put("id", "http://"+ host.toString()+"/cep/sensor/"
                +randomUUIDString);
        dbObject.put("type", "CEPSensor");
        dbObject.put("status", "vitalRunning");
        JSONArray observes =  new JSONArray();
        JSONArray compl = dsjo.getJSONArray("complex");
        for (int i = 0; i < compl.length(); i++) {
            JSONObject oComplex = new JSONObject(
                    compl.get(i).toString());
            JSONObject oObserves = new JSONObject();
            
            oObserves.put("type", "vital:ComplexEvent");
            //oObserves.put("uri", "http://"+ host.toString()
            //        +"/cep/sensor/"+randomUUIDString
            //        +"/"+oComplex.getString("id").toString());
            oObserves.put("id", "http://"+ host.toString()
                    +"/cep/sensor/"+randomUUIDString
                    +"/"+oComplex.getString("id").toString());
            
            observes.put(oObserves);
            
        }
        DBObject dbObject2 = (DBObject)JSON.parse(observes
                            .toString());
        dbObject.put("ssn:observes",dbObject2);
        return dbObject;
    }
    
    private void createCEPICOObject(DBObject dbObject, String host1, String randomUUIDString, JSONObject dsjo) throws JSONException {
        dbObject.put("@context",
                "http://vital-iot.eu/contexts/sensor.jsonld");
        dbObject.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString);
        dbObject.put("uri", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString);
        dbObject.removeField("type");
        dbObject.put("type","vital:CEPSensor");
        dbObject.put("status", "vital:Running");
        JSONArray observes =  new JSONArray();
        JSONArray compl = dsjo.getJSONArray("complex");
        for (int i = 0; i < compl.length(); i++) {
            JSONObject oComplex = new JSONObject(
                    compl.get(i).toString());
            JSONObject oObserves = new JSONObject();
            oObserves.put("type", "vital:ComplexEvent");
            oObserves.put("uri", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString + "/" + oComplex.getString("id").toString());
            oObserves.put("id", "http://" + host1.toString() + "/cep/sensor/" + randomUUIDString + "/" + oComplex.getString("id").toString());
            observes.put(oObserves);
        }
        DBObject dbObject2 = (DBObject)JSON.parse(observes.toString());
        dbObject.put("ssn:observes",dbObject2);
    }
    
    /**
     * Gets a filter.
     *
     * @return the filter 
     */
    @POST
    @Path("getcepico")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCEPICO(String id) {
        
        JSONObject jo = new JSONObject(id);
        String idjo = jo.getString("id");
          
       MongoClient mongo = new MongoClient(mongoIp, mongoPort);
            MongoDatabase db = mongo.getDatabase(mongoDB);
            
            try {
               db.getCollection("ceps");
            } catch (Exception e) {
              //System.out.println("Mongo is down");
              mongo.close();
              return Response.status(Response
                                .Status.INTERNAL_SERVER_ERROR).build();
                    
            }
        
        BasicDBObject searchById = new BasicDBObject("id",idjo);
        String found = null;
        BasicDBObject fields = new BasicDBObject().append("_id",false);

       FindIterable<Document> coll = db.getCollection("ceps")
                .find(searchById).projection(fields);
        
        try {
            found = coll.first().toJson();
        }catch(Exception e){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        if (found == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            return Response.status(Response.Status.OK)
                    .entity(found.toString()).build();
        }
        
    }
    
    /**
     * Gets a filter.
     *
     * @return the filter 
     */
//    @POST
//    @Path("deletecepico")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response deleteCEPICO(String filterId) {
//        
//        JSONObject jo = new JSONObject(filterId);
//        String idjo = jo.getString("id");
//             
//        MongoClient mongo = null;
//        
//        try {
//            mongo = new MongoClient(mongoIp,mongoPort);
//        } catch (UnknownHostException ex) {
//            java.util.logging.Logger.getLogger(CEPIco.class.getName())
//                    .log(Level.SEVERE, null, ex);
//        }
//        
//        DB db = mongo.getDB("vital");
//        DBCollection coll = db.getCollection("ceps");
//        DBObject searchById = new BasicDBObject("id",idjo);
//        DBObject found = null;
//        found = coll.findOne(searchById);
//        
//        
//        
//        if (found == null){
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }else{
//             WriteResult result = coll.remove(searchById);
//            if (result.getN()==(int)1){
//                return Response.status(Response.Status.OK)
//                    .build();
//            }else{
//                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .build();
//            }
//        }
//    }
	
}
