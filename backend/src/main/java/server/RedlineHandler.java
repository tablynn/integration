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


//http://localhost:3231/redlineData?min_lat=33.464099&max_lat=33.475366&min_lon=-112.093494&max_lon=-112.061602

//http://localhost:3231/redlineData?min_lat=33.454037&max_lat=33.466779&min_lon=-112.102899&max_lon=-112.092607

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
        this.minLat = request.queryParams("min_lat");
        this.maxLat = request.queryParams("max_lat");
        this.minLong = request.queryParams("min_lon");
        this.maxLong = request.queryParams("max_lon");

        if (this.minLat == null | this.maxLat == null |  this.minLong == null |  this.maxLong == null) {
            System.out.println("something is null");
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

        List<Feature> filteredFeatures = new ArrayList<Feature>();
        // FINISH: filtering out loop 
        loop: for (Feature feature : this.redLiningData.features()){
            if (feature.geometry() == null){
                continue;
            }
                List<List<Float>> totalBounds = feature.geometry().coordinates().get(0).get(0);
                for (List<Float> coordinates : totalBounds){
                    float lon = coordinates.get(0);
                    float lat = coordinates.get(1);
                    if (lon < minLongFloat || lon > maxLongFloat || lat < minLatFloat || lat > maxLatFloat){
                        continue loop;
                    }
                    
                }
                filteredFeatures.add(feature);
            
        }

        return filteredFeatures;
    }
     

    public record FeatureCollection(String type, List<Feature> features){}
    public record Feature(String type, Geometry geometry, Map<String, Object> properties){}
    public record Geometry(String type, List<List<List<List<Float>>>> coordinates){}

}