package server;

import CSVParser.FactoryFailureException;
import CSVParser.Parser;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import typefactories.ListStringFact;

//http://localhost:3231/weather?latitude=36&longitude=-119
//http://localhost:3231/weather?latitude=38&longitude=-77

/**
 * The WeatherHandler class implements the Route interface, including the Handle method, which
 * returns the serialized JSONs using Moshi. WeatherHandler sends a request to NWS API, and
 * returns the temperature at the given lat and lon (if lat and lon are valid).
 */
public class WeatherHandler implements Route {

  /**
   * This method creates the map of String(s) and Object(s), mapping "result" to "success" if the
   * CSV is loaded correctly, and "result" to "error message" if it is not. If the query parameters
   * are valid then it also maps "temperature" to the temperature at the provided lat and lon.
   *
   * @param request the request to handle
   * @param response used to modify properties of the response
   * @return response content
   * @throws Exception part of the interface
   */
  @Override
  public Object handle(Request request, Response response) {
    QueryParamsMap qm = request.queryMap();
    Map<String, Object> responseMap = new HashMap<>();

    // If the correct query parameters are provided ...
    if (qm.hasKey("latitude") && qm.hasKey("longitude")) {
      String latitude = qm.value("latitude");
      String longitude = qm.value("longitude");
      try {
        responseMap.put("result", "success");
        String lat = this.truncateNum(latitude);
        String lon = this.truncateNum(longitude);
        responseMap.put("temperature", this.getTemperature(lat, lon));
      } catch (Exception e) {
        // If invalid lat and lon numerical values provided
        responseMap.put("result", "error_datasource");
      }
    } else {
      // If incorrect query parameters are provided
      responseMap.put("result", "error_bad_request");
    }

    // Return response
    Moshi moshi = new Moshi.Builder().build();
    Type respType = Types.newParameterizedType(Map.class, String.class, Object.class);
    return moshi.adapter(respType).toJson(responseMap);
  }

  /**
   * This method takes in a number and turns in into a double with 5 decimals.
   *
   * @param num - number to be turned into a double
   * @return - argument as a String(double with 5 decimals)
   */
  public String truncateNum(String num) {
    double numAsDouble = Double.parseDouble(num);
    DecimalFormat df = new DecimalFormat("#.#####");
    return df.format(numAsDouble);
  }

  /**
   * This method takes in the truncated latitude and longitude values, makes a request to the
   * NWS API server, gets the temperature at the provided latitude/longitude, and returns that
   * temperature.
   *
   * @param lat - truncated latitude value
   * @param lon - truncated longitude value
   * @return - the temperature as a String
   */
  public String getTemperature(String lat, String lon) throws Exception {
    // API link with lat/lon values
    String webLink = "https://api.weather.gov/points/" + lat + "," + lon; // how to integrate a new data source
    // Create a client object and pass in an HTTP request object
    HttpRequest weatherRequest = HttpRequest.newBuilder()
        .uri(new URI(webLink))
        .GET()
        .build();
    // Building and sending HTTP request
    HttpResponse<String> weatherResponse = HttpClient.newBuilder()
        .build()
        .send(weatherRequest, BodyHandlers.ofString());

    return parseAPIResponse(weatherResponse.body());
  }

  /**
   * This helper method parses the API response, taking in weatherResponse.body() as a parameter
   * and returning the temperature of the given location body.
   * @param weatherResponse - the body of the HTTP request
   * @return temperature of given coordinates
   * @throws IOException
   * @throws URISyntaxException
   * @throws InterruptedException
   */
  public String parseAPIResponse(String weatherResponse)
      throws IOException, URISyntaxException, InterruptedException {
    // Because we made weather response a parameter, we have to throw IOException, URISyntaxException,
    // and InterruptedException.

    // Weather Moshi
    Moshi moshi = new Moshi.Builder().build();
    String forecastURL =
        moshi.adapter(Weather.class).fromJson(weatherResponse).properties.forecast();

    HttpRequest forecastRequest = HttpRequest.newBuilder()
        .uri(new URI(forecastURL))
        .GET()
        .build();

    HttpResponse<String> forecastResponse = HttpClient.newBuilder()
        .build()
        .send(forecastRequest, BodyHandlers.ofString());

    // Forecast Moshi
    Moshi moshiTwo = new Moshi.Builder().build();
    return moshiTwo.adapter(Forecast.class)
        .fromJson(forecastResponse.body()).properties.getTemperature();
  }

  // Navigating Weather section of NWS API

  /**
   * Weather record -> want to get to "properties."
   */
  public record Weather(WeatherProperties properties) {
    /**
     * WeatherProperties record -> want to get to "forecast."
     */
    public record WeatherProperties(String forecast){}
  }

  // Navigating Forecast section of NWS API (we get to this through the Weather section of NWS API)

  /**
   * Forecast record -> want to get to "properties."
   */
  public record Forecast(ForecastProperties properties) {

    /**
     * ForecastProperties record -> want to get to "periods."
     */
    public record ForecastProperties(List<TimePeriod> periods) {
      /**
       * This method calls the periods getTemperature method in order to get the temperature and
       * the temperature unit.
       *
       * @return the temperature and temperature unit
       */
      public String getTemperature() {
        return this.periods.get(0).getTemperature();
      }
    }

    /**
     * TimePeriod record -> want to get to "temperature" and "temperatureUnit."
     */
    public record TimePeriod(String temperature, String temperatureUnit) {
      /**
       * This method calls returns the temperature and temperature unit.
       *
       * @return the temperature and temperature unit
       */
      public String getTemperature() {
        return this.temperature + this.temperatureUnit;
      }
    }
  }

  public List<List<String>> fuzzTestingHelper(String randomString) throws FactoryFailureException {
    StringReader stringReader = new StringReader(randomString);
    ListStringFact listOfStrings = new ListStringFact();
    Parser parser = new Parser(stringReader, listOfStrings);
    parser.readData();
    return parser.getListOfObject();
  }
}
