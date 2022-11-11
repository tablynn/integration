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
    Spark.unmap("redlineData");
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

  @Test
  public void noFilterSuccess() throws Exception{
    HttpURLConnection clientConnection = tryRequest("redlineData?min_lat=0&max_lat=1&min_lon=0&max_lon=1");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();
    Map response = moshi.adapter(Map.class).fromJson(new Buffer().readFrom(
      clientConnection.getInputStream()));

      assertEquals("succes", response.get("features").toString());
      assertEquals("[]", response.get("features").toString());

  clientConnection.disconnect();    

  }

  
}
