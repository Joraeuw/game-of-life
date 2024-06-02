package game_of_life.utils;

public class Console {
  public static void clear() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
}
