package org.mholford.diderot;

import org.junit.Test;
import org.mholford.chatlantis.Chatlantis;
import org.mholford.chatlantis.ChatlantisAnswer;
import org.mholford.chatlantis.Utils;

import java.io.IOException;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class AutomatedConvoTest implements Utils {
  
  @Test
  public void test1() throws IOException, ReflectiveOperationException {
    String goal = "Nov 28, 1955";
    Map<String, String> map = stringMapOf(
        "Seattle, Washington, U.S.", "when was bill gates born");
    String input = "where was bill gates born";
    ChatlantisAnswer answer = loopUtilGoal(input, goal, map, "matt", "diderot", 100);
    assertEquals(goal, answer.getAnswer());
  }
  
  private ChatlantisAnswer loopUtilGoal(String input, String goal, Map<String, String> map,
                                        String user, String botName, int threshold)
      throws IOException, ReflectiveOperationException {
    String convId = null;
    Chatlantis server = Chatlantis.get();
    ChatlantisAnswer answer = server.speak(input, user, convId, botName);
    int tries = 0;
    while (!answer.getAnswer().equals(goal) && tries++ < threshold) {
      String a = answer.getAnswer();
      input = map.get(a);
      if (input == null) {
        throw new IOException("No response to match input: " + a);
      }
      convId = answer.getConversation().getId();
      answer = server.speak(input, user, convId, botName);
    }
    if (tries >= threshold) {
      throw new RuntimeException("Unbroken loop; goal condition not reaced");
    }
    return answer;
  }
}
