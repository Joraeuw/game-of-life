package game_of_life.utils;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Console {
  private static final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
  private static final Scanner scanner = new Scanner(System.in);
  private static boolean shouldRun = true;

  public static void clear() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static void run() {
    Thread inputThread = new Thread(() -> {
      while (true && shouldRun) {
        String input = scanner.nextLine();
        inputQueue.offer(input);
      }
    });

    inputThread.start();
  }

  public static boolean hasNextLine() {
    return !inputQueue.isEmpty();
  }

  public static void close() {
    scanner.close();
    shouldRun = false;
  }

  public static String readInput() {
    inputQueue.forEach((element) -> {
      System.out.print(element + " ");
    });
    System.out.println();
    try {
      return inputQueue.take();
    } catch (InterruptedException e) {
      return null;
    }
  }

  public static String getParam(String[] params, int idx) {
    if (params.length - 1 < idx) {
      return null;
    }

    return params[idx];
  }

  public static String getParam(String[] params, int idx, String errorMessage) {
    if (params.length - 1 < idx) {
      System.out.println(errorMessage);
      return null;
    }

    return params[idx];
  }
}
