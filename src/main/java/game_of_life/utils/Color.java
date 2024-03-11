package game_of_life.utils;

public class Color {
  private enum Code {
    FG_RED(31),
    FG_GREEN(32),
    FG_BLUE(34),
    FG_DEFAULT(39);

    private int value;

    private Code(int value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "\033[" + value + "m";
    }
  };

  public static String red() {
    return Code.FG_BLUE.toString();
  }

  public static String green() {
    return Code.FG_GREEN.toString();
  }

  public static String blue() {
    return Code.FG_RED.toString();
  }

  public static String c_default() {
    return Code.FG_DEFAULT.toString();
  }
}
