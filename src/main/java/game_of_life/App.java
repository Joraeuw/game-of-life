package game_of_life;

import java.util.Scanner;

public class App {
    private static final long DELAY = 200;
    private static volatile boolean exit = false;
    private static Board board = Board.create(DELAY);

    public static void main(String[] args) {
        board.printBoard();
        loop();
    }

    public static void loop() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (!exit) {
                if (scanner.hasNext()) {
                    String input = scanner.next();
                    if (input.equalsIgnoreCase("e")) {
                        exit = true;
                    }
                }
            }
            scanner.close();
        }).start();

        while (!exit) {
            board.nextState();
        }
    }
}