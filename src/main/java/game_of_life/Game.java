package game_of_life;

import java.util.Scanner;

import game_of_life.types.State;
import game_of_life.utils.Color;

public class Game {
  private static final long DELAY = 200;
  private static volatile boolean exit = false;
  private static volatile boolean pause = false;
  private static volatile State signal = State.RUNNING;
  private static Board board = Board.create(DELAY);

  public static void start() {
    board.printBoard();
    loop();
  }

  private static void loop() {
    new Thread(() -> {
      Scanner scanner = new Scanner(System.in);
      while (!exit) {
        if (scanner.hasNext()) {
          String input = scanner.next();
          if (input.equalsIgnoreCase("e")) {
            exit = true;
          } else if (input.equalsIgnoreCase("p")) {
            pause = !pause;
          }
        }
      }
      scanner.close();
    }).start();

    while (!exit) {
      if (pause)
        continue;

      switch (signal) {
        case RUNNING:
          signal = board.nextState();
          break;
        case CYCLING:
          System.out.println(Color.blue() +
              "The state is cyclic! The cycle repeats every "
              + Color.red()
              + Integer.toString(board.cycleFrequency(board.tribeHashBoard().getSecond()))
              + Color.blue()
              + " generations!"
              + Color.c_default());
          exit = true;
          break;
        case EVERYONE_LOST:
          System.out.println(Color.blue() + "All tribes died out!" + Color.c_default());
          exit = true;
          break;
        case TRIBE_VICTORIOUS:
          System.out.println(Color.blue() +
              "The " +
              Color.red() +
              signal.getVictoriousTribe().name() +
              Color.blue() +
              " tribe was victorious!" +
              Color.c_default());
          exit = true;
          break;
        default:
          break;
      }
    }
  }
}
