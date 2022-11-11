import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.RedlineHandler;
import server.WeatherHandler;
import spark.Spark;

public class GeoDataTest {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  @BeforeEach
  public void setup(){
    // Restart spark
    Spark.get("redlineData", new RedlineHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown(){
    // Stops spark form listening on both endpoints
    Spark.unmap("redlineData");
    Spark.awaitStop();
  }

  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    System.out.println(requestURL.openConnection());
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests that weather called without both query parameters (latitude and longitude) provided
   * prints error_bad_request.
   */
  @Test
  public void testNoQueryParams() throws IOException {
    HttpURLConnection clientConnection = tryRequest("redlineData");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   *
   */
  @Test
  public void testNoFilteredFeatures() throws IOException {
    HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=33.464099&max_lat=33.475366&min_lon=-112.093494&max_lon=-112.061602");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

}
