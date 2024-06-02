package game_of_life;

import java.io.Serializable;

import game_of_life.types.State;
import game_of_life.utils.Color;
import game_of_life.utils.Console;

public class Game implements Serializable {
  private static final long serialVersionUID = 8776813025115521740L;

  private volatile boolean exit = false;
  private volatile boolean pause = false;
  private volatile State signal = State.RUNNING;
  private Board board;

  public Game(int rows, int cols, int numberOfClusters, double percentageFill, int delay) {
    board = Board.create(rows, cols, numberOfClusters, percentageFill, delay);
  }

  public void setExit(boolean exit) {
    this.exit = exit;
  }

  public void setPause(boolean pause) {
    this.pause = pause;
  }

  public void start() {
    System.out.println(board);
    loop();
  }

  public void loop() {
    new Thread(() -> {
      while (!exit) {
        if (Console.hasNextLine()) {
          String input = Console.readInput().trim();
          if (input.equalsIgnoreCase("e")) {
            exit = true;
          } else if (input.equalsIgnoreCase("p")) {
            pause = !pause;
          }
        }
      }
    }).start();

    while (!exit) {
      if (pause)
        continue;

      switch (signal) {
        case RUNNING:
          signal = board.nextState(() -> {
            System.out.println(board);
            return 1;
          });
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
