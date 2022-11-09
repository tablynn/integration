package server;

import static spark.Spark.after;

import java.util.ArrayList;
import java.util.List;
import spark.Spark;

/**
 * Top-level class for this demo. Contains the main() method which starts Spark and runs the
 * various handlers.
 */
public class Server {

  /**
   * Main method.
   *
   * @param args - arguments provided to main method
   */
  public static void main(String[] args) {
    // Shared state that is passed into LoadHandler and GetHandler
    List<List<String>> csvData = new ArrayList<>();
    Spark.port(3231);

    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

    // Setting up the handler for GET and the endpoints
    Spark.get("loadCSV", new LoadHandler(csvData));
    Spark.get("getCSV", new GetHandler(csvData));
    Spark.get("weather", new WeatherHandler());
    Spark.get("redlineData", new RedlineHandler()); //backend/src/main/java/server/RedlineHandler.java
    Spark.init();
    Spark.awaitInitialization();
    System.out.println("Server started.");
  }
}