import static org.junit.jupiter.api.Assertions.assertEquals;

import CSVParser.FactoryFailureException;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.GetHandler;
import server.LoadHandler;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.WeatherHandler;
import spark.Spark;


/**
 * Testing class for GetHandler, LoadHandler and the "loadCSV" and "getCSV" spark requests.
 */
public class CSVHandlersTest {

  /**
   * Starts the server before all tests
   */
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  // Shared state that gets passed into LoadHandler and getHandler
  List<List<String>> csvData = new ArrayList<>();

  /**
   * Before each test is run, clears shared state this.csvData and sets up Spark.
   */
  @BeforeEach
  public void setup(){
    this.csvData.clear();
    // Restart spark
    Spark.get("loadCSV", new LoadHandler(this.csvData));
    Spark.get("getCSV", new GetHandler(this.csvData));
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test is run, resets Spark.
   */
  @AfterEach
  public void teardown(){
    // Stops spark form listening on both endpoints
    Spark.unmap("loadCSV");
    Spark.unmap("getCSV");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   * @param apiCall the call string, including endpoint
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Tests that an invalid API call gives an API error.
   */
  @Test
  public void testInvalidAPICall() throws IOException{
    HttpURLConnection clientConnection = tryRequest("hi");
    // Don't get an OK response - API provides an error message
    assertEquals(404, clientConnection.getResponseCode());
  }

  /**
   * Tests that loadCSV called without a filePath provided prints error_bad_request.
   */
  @Test
  public void testLoadNoFilePath() throws IOException{
    HttpURLConnection clientConnection = tryRequest("loadCSV");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_request"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that loadCSV called with an invalid filePath provided prints error_datasource.
   */
  @Test
  public void testLoadInvalidFilePath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadCSV?filePath=invalid");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_datasource"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that loadCSV called with a valid filePath provided prints success.
   */
  @Test
  public void testLoadValidFilePath() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadCSV?filePath=src/Data/ten-star.csv");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "success"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that if getCSV is called without loadCSV being called, error_bad_json is printed.
   */
  @Test
  public void testGetBeforeLoad() throws IOException {
    HttpURLConnection clientConnection = tryRequest("getCSV");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals(Map.of("result", "error_bad_json"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that if loadCSV is called with an invalid filePath before getCSV is called,
   * error_bad_json is printed.
   */
  @Test
  public void testInvalidLoadBeforeGet() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadCSV?filePath=invalid");
    HttpURLConnection clientConnection1 = tryRequest("getCSV");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(200, clientConnection1.getResponseCode());


    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
    assertEquals(Map.of("result", "error_bad_json"), responses);

    clientConnection.disconnect();
  }

  /**
   * Tests that if loadCSV is called with a valid filePath before getCSV is called, success and
   * the contents of the csv file are printed.
   */
  @Test
  public void testValidLoadBeforeGet() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadCSV?filePath=src/Data/one-star.csv");
    HttpURLConnection clientConnection1 = tryRequest("getCSV");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(200, clientConnection1.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));

    // Using "result" and "contents" as a key to get values from the returned responses map.
    Assertions.assertEquals("success", responses.get("result"));
    Assertions.assertEquals("[[0, Sol, 0, 0, 0]]", responses.get("contents").toString());
  }

  /**
   * Tests that if loadCSV is called with a valid filePath before getCSV is called, success and
   * the contents of the csv file are printed. The csv file used here is slightly more complex
   * than the one used in the previous test.
   */
  @Test
  public void testValidLoadComplexBeforeGet() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadCSV?filePath=src/Data/ten-star.csv");
    HttpURLConnection clientConnection1 = tryRequest("getCSV");
    // Connection worked -> get OK response
    assertEquals(200, clientConnection.getResponseCode());
    assertEquals(200, clientConnection1.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map<String, Object> responses = moshi.adapter(Map.class)
        .fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));

    // Using "result" and "contents" as a key to get values from the returned responses map.
    Assertions.assertEquals("success", responses.get("result"));
    Assertions.assertEquals("[[StarID, ProperName, X, Y, Z], [0, Sol, 0, 0, 0], [1, , 282.43485, 0.00449, 5.36884], [2, , 43.04329, 0.00285, -15.24144], [3, , 277.11358, 0.02422, 223.27753], [3759, 96 G. Psc, 7.26388, 1.55643, 0.68697], [70667, Proxima Centauri, -0.47175, -0.36132, -1.15037], [71454, Rigel Kentaurus B, -0.50359, -0.42128, -1.1767], [71457, Rigel Kentaurus A, -0.50362, -0.42139, -1.17665], [87666, Barnard's Star, -0.01729, -1.81533, 0.14824], [118721, , -2.28262, 0.64697, 0.29354]]", responses.get("contents").toString());
  }


  @Test
  public void testFuzzHelper() throws FactoryFailureException {
    WeatherHandler weatherHandler = new WeatherHandler();

    for (int i = 0; i<10; i++){
      final ThreadLocalRandom r = ThreadLocalRandom.current();
      byte[] bytes = new byte[10];
      r.nextBytes(bytes);
      String name = new String(bytes, Charset.forName("UTF-8"));
      System.out.println(name);

      final ThreadLocalRandom r1 = ThreadLocalRandom.current();
      byte[] bytes1 = new byte[10];
      r1.nextBytes(bytes1);
      String name1 = new String(bytes, Charset.forName("UTF-8"));
      System.out.println(name1);

      weatherHandler.fuzzTestingHelper(name + "," + name1);
    }

  }
}
