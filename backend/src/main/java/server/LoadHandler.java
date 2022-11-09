package server;

import CSVParser.FactoryFailureException;
import CSVParser.Parser;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import typefactories.ListStringFact;

// localhost:3231/loadCSV?filePath=src/Data/ten-star.csv
// localhost:3231/loadCSV?filePath=src/Data/one-star.csv

/**
 * The LoadHandler class implements the Route interface, including the Handle method, which
 * returns the serialized JSONs using Moshi.
 */
public class LoadHandler implements Route {

  private List<List<String>> csvData;
  private String result;

  /**
   * Constructor accepts some shared state.
   *
   * @param csvData - the shared state
   */
  public LoadHandler(List<List<String>> csvData) {
    this.csvData = csvData;
    this.result = "";
  }

  /**
   * This method parses the passed in CSV file. It checks that the filepath is valid and adds
   * the returned objects from the parser to the variable csvData.
   *
   * @param filepath - the provided filepath
   */
  private void updateCSVData(String filepath) {
    // try/catch to see if filepath is valid or not
    try {
      // Call CSV Parser
      FileReader fileReader = new FileReader(filepath);
      ListStringFact listOfStrings = new ListStringFact();
      Parser parser = new Parser(fileReader, listOfStrings);
      parser.readData();

      // Update this.csvData
      this.csvData.clear();
      this.csvData.addAll(parser.getListOfObject());
      this.result = "success";

    } catch (IOException e) {
      this.csvData.clear();
      // FileNotFoundError
      this.result = "error_datasource";
    } catch (NullPointerException e) {
      this.csvData.clear();
      // Null filepath provided
      this.result = "error_bad_request";
    } catch (FactoryFailureException e) {
      this.csvData.clear();
      // Required to be caught because of the Parser class - won't get thrown
      this.result = "factory_failure_exception";
    }
  }

  /**
   * This method calls the method to load the CSV data, and creates the map of strings and
   * objects to be serialized. It then returns the serialized responseMap.
   *
   * @param request the request to handle
   * @param response used to modify properties of the response
   * @return response content
   * @throws Exception part of the interface
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    QueryParamsMap qm = request.queryMap();
    String filepath = qm.value("filePath");

    // Checks if filepath is valid/exists
    this.updateCSVData(filepath);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("result", this.result);

    Moshi moshi = new Moshi.Builder().build();
    // Moshi takes in the type to be converted to Json (responseMap is type Map, String, Object)
    Type respType = Types.newParameterizedType(Map.class, String.class, Object.class);
    return moshi.adapter(respType).toJson(responseMap);
  }
}
