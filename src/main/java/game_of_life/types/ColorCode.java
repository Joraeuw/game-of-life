package game_of_life.types;

public enum ColorCode {
  FG_RED(31),
  FG_GREEN(32),
  FG_BLUE(34),
  FG_DEFAULT(39);

  private int value;

  private ColorCode(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "\033[" + value + "m";
  }
}
