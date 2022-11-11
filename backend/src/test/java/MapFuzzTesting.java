import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class MapFuzzTesting {

  /**
   * Random ints to give ranges
   * @param min
   * @param max
   * @return
   */
  private int RandomValue(int min, int max){
    Random randomInt = new Random();
    return randomInt.nextInt(max - min) + min;
  }

  @Test
  public void testRandoFuzzTesting(){

  }

}
