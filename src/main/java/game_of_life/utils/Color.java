package game_of_life.utils;

import game_of_life.types.ColorCode;

public class Color {

  public static String red() {
    return ColorCode.FG_BLUE.toString();
  }

  public static String green() {
    return ColorCode.FG_GREEN.toString();
  }

  public static String blue() {
    return ColorCode.FG_RED.toString();
  }

  public static String c_default() {
    return ColorCode.FG_DEFAULT.toString();
  }
}
