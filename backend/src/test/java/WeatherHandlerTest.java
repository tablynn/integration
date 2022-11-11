import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.WeatherHandler;
import spark.Spark;

/**
 * Testing class for WeatherHandler and the "weather" spark request.
 */
public class WeatherHandlerTest {

  /**
   * Starts the server before all tests
   */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Before each test is run, clears shared state this.csvData and sets up Spark.
   */
  @BeforeEach
  public void setup(){
    // Restart spark
    Spark.get("weather", new WeatherHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test is run, resets Spark.
   */
  @AfterEach
  public void teardown(){
    // Stops spark form listening on both endpoints
    Spark.unmap("weather");
    Spark.awaitStop();
  }

  /**
   * Tests that weather called without both query parameters (latitude and longitude) provided
   * prints error_bad_request.
   */
  @Test
  public void testNoQueryParams() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that weather called without one of two query parameters (longitude) provided prints
   * error_bad_request.
   */
  @Test
  public void testLatNoLong() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?latitude=34");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that weather called with both query parameters provided except both query parameters
   * are not given values prints error_datasource.
   */
  @Test
  public void testLatNoValue() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?latitude=&longitude=");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_datasource"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that weather called with both query parameters, where both query parameters are valid
   * numbers prints success.
   */
  @Test
  public void testValidLatLong() throws IOException {
    HttpURLConnection clientConnection = tryRequest("weather?latitude=38&longitude=-77");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", responses.get("result"));

    clientConnection.disconnect();
  }

  /**
   * Tests that the helper method to parse API response works correctly when taking in
   * a mock JSON file.
   * @throws IOException
   * @throws URISyntaxException
   * @throws InterruptedException
   */
  @Test
  public void testMockRevision() throws IOException, URISyntaxException, InterruptedException {
    WeatherHandler weatherHandler = new WeatherHandler();
    String file = "src/data/mockAPI.json";
    String mockJSON = new String(Files.readAllBytes(Paths.get(file)));
    assertEquals("34F",weatherHandler.parseAPIResponse(mockJSON));
  }

}
