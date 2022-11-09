package CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that parses given information/data and performs word count utility on that data.
 *
 * @author ahudda1
 */
public class Parser<T> {
  private Reader readerType;
  private CreatorFromRow<T> genericType;
  private List listOfObject;
  private int rowCount;
  private int characterCount;
  private int columnCount;
  private int wordCount;

  /**
   * Initializes all parameters and instance variables.
   *
   * @param readerType type of Reader object needed to read in information/data
   * @param genericType instance of a class that implements CreatorFromRow type T interface - allows
   *     for parsed data to be converted into that object type.
   */
  public Parser(Reader readerType, CreatorFromRow<T> genericType) {
    // initialize all instance variables
    this.readerType = readerType;
    this.genericType = genericType;
    this.listOfObject = new ArrayList();
    this.rowCount = 0;
    this.characterCount = 0;
    this.columnCount = 0;
    this.wordCount = 0;
  }

  /**
   * Method that uses BufferedReader to read in lines and calls helper methods to parse them, update
   * count instance variables and create objects from rows.
   *
   * @throws FactoryFailureException if Factory class fails to convert data
   */
  public void readData() throws FactoryFailureException {
    try {
      BufferedReader reader = new BufferedReader(this.readerType);
      String line = reader.readLine();
      // repeat this loop until the end of the data
      while (line != null) {
        this.parseAndStoreData(line);
        line = reader.readLine();
      }
      this.printInfo();
      reader.close();
    } catch (IOException e) {
      System.out.println("Encountered an error: " + e.getMessage() + " Please try again!");
    }
  }

  /**
   * Helper method: parses the given String line and updates all the appropriate counter instance
   * variables according to the parsed data.
   *
   * @param line the String line that BufferedReader just read in
   * @throws FactoryFailureException if Factory class fails to convert data
   */
  private void parseAndStoreData(String line) throws FactoryFailureException {
    this.rowCount = this.rowCount + 1;
    this.characterCount = this.characterCount + (line.length());
    String[] columns = line.split(",");
    this.columnCount = columns.length;
    String[] words = line.split(",| ");
    // only wordCount ++ if the String is not empty ("")
    for (int i = 0; i < words.length; i++) {
      if (words[i].equals("") == false) {
        this.wordCount = this.wordCount + 1;
      }
    }
    // method call to convert List of Strings to object type
    this.rowToObject(columns);
  }

  /**
   * Helper method: converts List of Strings into a generic type object and adds it to a List of
   * those objects.
   *
   * @param columns List of Strings where each string is one column
   * @throws FactoryFailureException if Factory class fails to convert data
   */
  private void rowToObject(String[] columns) throws FactoryFailureException {
    // try catch to catch thrown Exceptions from CreatorFromRow<T> create() method
    try {
      T rowAsObject = this.genericType.create(Arrays.asList(columns));
      this.listOfObject.add(rowAsObject);
    } catch (FactoryFailureException f) {
      System.out.print("Factory Failure Exception - provided factory could not process CSV row");
      throw f;
    }
  }

  /** Helper method: prints relevant data (count variables) with correct format. */
  private void printInfo() {
    /*System.out.println("Words: " + this.wordCount);
    System.out.println("Characters: " + this.characterCount);
    System.out.println("Rows: " + this.rowCount);
    System.out.println("Columns: " + this.columnCount); */
  }


  /**
   * Getter to return the list of objects created that encapsulates all the objects returned from
   * the create() method.
   *
   * @return integer value - number of columns
   */
  public List getListOfObject() {
    return this.listOfObject;
  }
}