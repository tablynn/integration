package server;

import CSVParser.FactoryFailureException;
import CSVParser.Parser;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.FileReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import typefactories.ListStringFact;

//localhost:3231/getCSV

/**
 * The GetHandler class implements the Route interface, including the Handle method, which
 * returns the serialized JSONs using Moshi. GetHandler gets the contents of the CSV.
 */
public class GetHandler implements Route {

  private List<List<String>> csvData;

  /**
   * Constructor accepts some shared state.
   *
   * @param csvData the shared state
   */
  public GetHandler(List<List<String>> csvData) {
    this.csvData = csvData;
  }

  /**
   * This method creates the map of String(s) and Object(s), mapping "result" to "success" if the
   * CSV is loaded correctly, and "result" to "error_bad_json" if it is not. If the file is loaded
   * successfully, the map is also populated with "contents," from the shared state
   * csvData (List of List of String(s)
   *
   * @param request the request to handle
   * @param response used to modify properties of the response
   * @return response content
   * @throws Exception part of the interface
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();

    // If "loadCSV" hasn't been called or hasn't been executed successfully then csvData is empty
    if (!this.csvData.isEmpty()) {
      responseMap.put("result", "success");
      responseMap.put("contents", this.csvData);
    } else {
      // "loadCSV" not called successfully before
      responseMap.put("result", "error_bad_json");
    }

    Moshi moshi = new Moshi.Builder().build();
    Type respType = Types.newParameterizedType(Map.class, String.class, Object.class);

    return moshi.adapter(respType).toJson(responseMap);
  }


}
