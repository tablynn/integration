package CSVParser;

import java.util.List;

/**
 * Strategy based interface that takes in a List of Strings and creates an object of type T from
 * that List of Strings.
 *
 * @param <T> object to be created
 */
public interface CreatorFromRow<T> {

  /**
   * Converts the List of Strings parameter into an object of generic type T.
   *
   * @param row a List of Strings that will be converted to object of type T
   * @return object of generic type T.
   * @throws FactoryFailureException if Factory class fails to convert data
   */
  T create(List<String> row) throws FactoryFailureException;
}
