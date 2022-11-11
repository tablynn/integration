package server;

import com.squareup.moshi.Moshi;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;



/**
 * The RedlineHandler class implements the Route interface, including the Handle method, which
 * returns the serialized JSONs using Moshi. Returns if redline request was a success or if there 
 * was an error. If there was a success, the localhost displays the correct data bounded
 * by the user's input of min/ max latitude/longitude
 */


//http://localhost:3231/redlineData?min_lat=33.464099&max_lat=33.475366&min_lon=-112.093494&max_lon=-112.061602

//http://localhost:3231/redlineData?min_lat=33.454037&max_lat=33.466779&min_lon=-112.102899&max_lon=-112.092607

public class RedlineHandler implements Route{
    private FeatureCollection redLiningData;

    private String minLat;
    private String maxLat;
    private String minLong;
    private String maxLong;


    /**
     * creating the filepath linked to the fullDownload json
     */

    public RedlineHandler(){
        this.minLat = null;
        this.maxLat = null;
        this.minLong = null;
        this.maxLong = null;

        /*try{
            Path filepath = Path.of("frontend/src/mockData/fullDownload.json");
            String RedlineJSON =  Files.readString(filepath);
            Moshi moshi = new Moshi.Builder().build();
            this.redLiningData = moshi.adapter(FeatureCollection.class).fromJson(RedlineJSON);

        }catch(IOException ignored){
            // error responses
            System.out.println("erorr");
        }  */
    }

    /**
     * Accepts the users minimum/maximum latitude and longitude. checks if these inputs are null.
     * If not, function puts the inputs into a filteredData list and returns a map
     * linking from "success" to the filteredData
     */

    @Override
    public Object handle(Request request, Response response) throws Exception {

        this.minLat = request.queryParams("min_lat");
        this.maxLat = request.queryParams("max_lat");
        this.minLong = request.queryParams("min_lon");
        this.maxLong = request.queryParams("max_lon");

      /**  Path filepath = Path.of("frontend/src/mockData/fullDownload.json");
        String RedlineJSON =  Files.readString(filepath);
        Moshi moshi = new Moshi.Builder().build();
        this.redLiningData = moshi.adapter(FeatureCollection.class).fromJson(RedlineJSON); */
        Path filePath = Path.of("frontend/src/mockData/fullDownload.json");
        String content = Files.readString(filePath);
        Moshi moshi = new Moshi.Builder().build();
        this.redLiningData = moshi.adapter(FeatureCollection.class).fromJson(content);

        if (this.minLat == null | this.maxLat == null |  this.minLong == null |  this.maxLong == null) {
            System.out.println("something is null");
            return this.redlineResponse("error_bad_request", null); // what should filtered data be? String that says none? 
        }
        else{
            List<Feature> filteredData= this.filterFeatures(this.minLat, this.maxLat, this.minLong, this.maxLong);
            return this.redlineResponse("success", filteredData);
        }


    }

    /**
     * Returning the repsonse of the users request 
     * @param successResponse -- checks if success or failure
     * @param filteredData -- data from fullDownload inside of user's inputs
     * @return -- serialized map showing the result, max/ min coordinates, and the 
     * filtered data
     */

    public Object redlineResponse(String successResponse, Object filteredData){
        Map<String, Object> responseMap = new HashMap<>();
        
       try{
            responseMap.put("result", successResponse);
            responseMap.put("minlat", minLat);
            responseMap.put("maxLat", maxLat);
            responseMap.put("minLong", minLong);
            responseMap.put("maxLong", maxLong);
            responseMap.put("data", filteredData);
        }catch(NumberFormatException e){
            responseMap.put("result", "error_bad_request");
        }catch (NullPointerException e){
            responseMap.put("result", "error_datasource");
      }
        
        Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(Map.class).toJson(responseMap);
    }

    /**
     * Filters the features on the fullDownload json to find the data inside of the 
     * users coordinates
     * @param minLat -- users min latitude
     * @param maxLat -- users max latitude
     * @param minLong -- users min longitude
     * @param maxLong -- users max longitude 
     * @return -- the filtered features data 
     */

    public List<Feature> filterFeatures(String minLat, String maxLat, String minLong, String maxLong){
        Float minLatFloat = Float.parseFloat(minLat);
        Float maxLatFloat = Float.parseFloat(maxLat);
        Float minLongFloat = Float.parseFloat(minLong);
        Float maxLongFloat = Float.parseFloat(maxLong);

        List<Feature> filteredFeatures = new ArrayList<Feature>();
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
     

    /*
     * Records that mock the fulldownload.json data, so that the filterFeatures method can access and run through the 
     * different features in the data download. 
     */
    public record FeatureCollection(String type, List<Feature> features){}
    public record Feature(String type, Geometry geometry, Map<String, Object> properties){}
    public record Geometry(String type, List<List<List<List<Float>>>> coordinates){}

}