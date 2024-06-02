package game_of_life.types;

import java.util.Arrays;

import game_of_life.utils.Console;
import game_of_life.utils.Pair;

public enum CLICommand {
  NEW,
  RESUME,
  SWITCH,
  OPEN,
  CLOSE,
  SAVE,
  SAVE_AS,
  HELP,
  EXIT,
  INFO;

  public static Pair<CLICommand, String[]> read() {
    String[] input = Console.readInput().trim().split(" ");

    String strCommand = input[0];
    String[] params = Arrays.copyOfRange(input, 1, input.length);

    CLICommand command;
    try {
      command = CLICommand.valueOf(strCommand.toUpperCase());
    } catch (IllegalArgumentException e) {
      return Pair.of(null, params);
    }

    return Pair.of(command, params);
  }
}
