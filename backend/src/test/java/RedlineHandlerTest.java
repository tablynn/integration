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
import server.RedlineHandler;
import spark.Spark;

public class RedlineHandlerTest {

  public RedlineHandlerTest() throws IOException {
  }

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
    Spark.get("redlineData", new RedlineHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * After each test is run, resets Spark.
   */
  @AfterEach
  public void teardown(){
    // Stops spark form listening on both endpoints
    Spark.unmap("/redlineData");
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
  //http://localhost:3231/redlineData?min_lat=0&max_lat=1&min_lon=0&max_lon=-1
  @Test
  public void testNoFilterSuccess() throws Exception{
    HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=0&max_lat=1&min_lon=0&max_lon=-1");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
      clientConnection.getInputStream()));

      assertEquals("success", response.get("features").toString());
      assertEquals("[]", response.get("features").toString());

  clientConnection.disconnect();    

  }

  @Test
  public void testNoParamSuccess() throws Exception{
    HttpURLConnection clientConnection = tryRequest("redlineData");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
        clientConnection.getInputStream()));

    assertEquals("success", response.get("features").toString());

    clientConnection.disconnect();
  }

    @Test
  public void testingSuccessMap() throws IOException {
      HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=33.454037&max_lat=33.466779&min_lon=-112.102899&max_lon=-112.092607");
      assertEquals(200, clientConnection.getResponseCode());

      Moshi moshi = new Moshi.Builder().build();
      Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
          clientConnection.getInputStream()));

      assertEquals("success", response.get("features").toString());
     // assertEquals(COPY FROM SERVER)


      clientConnection.disconnect();
    }

    @Test
  public void testMissingParamsAndInvalidEntry() throws IOException {
      HttpURLConnection clientConnection = tryRequest("redlineData?redlineData?min_lat=&min_lon=-112.102899&max_lon=-112.092607");
      assertEquals(200, clientConnection.getResponseCode());

      Moshi moshi = new Moshi.Builder().build();
      Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
          clientConnection.getInputStream()));

      assertEquals("error_bad_request", response.get("Result"));
      clientConnection.disconnect();
    }

  /**
   * min is greater than max
   * @throws IOException
   */

  @Test
  public void testInvalidMinAndMax() throws IOException {
      HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=10&max_lat=5&min_lon=-17&max_lon=-4");
      assertEquals(200, clientConnection.getResponseCode());

      Moshi moshi = new Moshi.Builder().build();
      Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
          clientConnection.getInputStream()));

      assertEquals("error_bad_request", response.get("Result"));
      clientConnection.disconnect();
    }

    @Test
  public void testNotNumberCords() throws IOException {
      HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=alks&max_lat=lsdthj&min_lon=-dsg&max_lon=-sdgr");
      assertEquals(200, clientConnection.getResponseCode());

      Moshi moshi = new Moshi.Builder().build();
      Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
          clientConnection.getInputStream()));

      assertEquals("error_bad_request", response.get("Result"));
      clientConnection.disconnect();
    }

    @Test
  public void testOutOfRangeCoords() throws IOException {
      HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=23456789876543&max_lat=2345678909876543456789&min_lon=-5678765434567&max_lon=-3456787654323456");
      assertEquals(200, clientConnection.getResponseCode());

      Moshi moshi = new Moshi.Builder().build();
      Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
          clientConnection.getInputStream()));

      assertEquals("error_bad_request", response.get("Result"));
      clientConnection.disconnect();
    }
    }






