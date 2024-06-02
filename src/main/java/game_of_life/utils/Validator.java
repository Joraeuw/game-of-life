package game_of_life.utils;

public class Validator {
  public static boolean isInteger(String str, String errorMessage) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      System.out.println(errorMessage);
      return false;
    }
  }

  public static boolean isDouble(String str, String errorMessage) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      System.out.println(errorMessage);
      return false;
    }
  }
}
