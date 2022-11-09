package server;

import com.squareup.moshi.Moshi;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RedlineHandler implements Route{
    private FeatureCollection redLiningData;

    private String minLat;
    private String maxLat;
    private String minLong;
    private String maxLong;



    public RedlineHandler(){
        this.minLat = null;
        this.maxLat = null;
        this.minLong = null;
        this.maxLong = null;
       
        try{
            Path filepath = Path.of("frontend/src/mockData/fullDownload.json");
            String RedlineJSON =  Files.readString(filepath);
    

            Moshi moshi = new Moshi.Builder().build();
            this.redLiningData = moshi.adapter(FeatureCollection.class).fromJson(RedlineJSON);

        }catch(IOException e){
            // error responses
        }
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        this.minLat = request.queryParams("minLat");
        this.maxLat = request.queryParams("maxLat");
        this.minLong = request.queryParams("minLong");
        this.maxLong = request.queryParams("maxLong");
     

        if (this.minLat == null | this.maxLat == null |  this.minLong == null |  this.maxLong == null) {
            return this.redlineResponse("error_bad_request", null); // what should filtered data be? String that says none? 
        }
        else{

            List<Feature> filteredData= this.filterFeatures(this.minLat, this.maxLat, this.minLong, this.maxLong);
            return this.redlineResponse("success", filteredData);
        }


    }

    public Object redlineResponse(String successResponse, Object filteredData){
        Map<String, Object> responseMap = new HashMap<>();
        
      //  try{
            responseMap.put("result", successResponse);
            responseMap.put("minlat", minLat);
            responseMap.put("maxLat", maxLat);
            responseMap.put("minLong", minLong);
            responseMap.put("maxLong", maxLong);
            responseMap.put("data", filteredData);
      //  }catch(Exception e){
      //      responseMap.put("result", "error_datasource");
     //  }
        
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(Map.class).toJson(responseMap);
    }

    public List<Feature> filterFeatures(String minLat, String maxLat, String minLong, String maxLong){
        Float minLatFloat = Float.parseFloat(minLat);
        Float maxLatFloat = Float.parseFloat(maxLat);
        Float minLongFloat = Float.parseFloat(minLong);
        Float maxLongFloat = Float.parseFloat(maxLong);

        List<Feature> filteredFeatures;
        // FINISH: filtering out loop 
        loop: for (Feature feature : this.redLiningData.features()){
            if (feature.geometry() == null){
                continue;
            }
        }

        return null;
        //filteredFeatures;
    }
     

    public record FeatureCollection(String type, List<Feature> features){}
    public record Feature(String type, Geometry geometry, Map<String, Object> properties){}
    public record Geometry(String type, List<Float> coordinates){}

}