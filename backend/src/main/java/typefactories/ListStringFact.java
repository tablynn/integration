package typefactories;

import CSVParser.CreatorFromRow;
import CSVParser.FactoryFailureException;
import java.util.List;

/**
 * Factory to return a List of Strings object from an input List of Strings that implements the
 * CreatorFromRow interface.
 *
 * @author ahudda1
 */
public class ListStringFact implements CreatorFromRow<List<String>> {

  /**
   * Takes in a List of Strings and returns that same List of Strings.
   *
   * @param row a List of Strings to be "converted" into a List of Strings
   * @return the List of Strings passed in
   * @throws FactoryFailureException if Factory class fails to convert data
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
    // no exception thrown to avoid issues when running User 1 Demo
  }
}
