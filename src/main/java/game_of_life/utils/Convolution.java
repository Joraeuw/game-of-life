package game_of_life.utils;

import java.util.Random;

import game_of_life.types.Tribe;
import io.vavr.collection.Vector;

public class Convolution {
  public static Tribe[][] apply(Tribe[][] board) {
    Random random = new Random();
    int rows_l = board.length;
    int cols_l = board[0].length;
    Vector<Integer> rows = Vector.range(0, rows_l);
    Vector<Integer> cols = Vector.range(0, cols_l);
    Tribe[][] nextBoard = new Tribe[board.length][board[0].length];

    rows.forEach(row -> cols.forEach(col -> {
      Tribe currentTribe = board[row][col];
      Vector<Pair<Tribe, Integer>> tribe_aliveCounts = countTribeNeighbors(board, row, col);
      int countLivingNeighbors = tribe_aliveCounts.foldLeft(0, (acc, pair) -> acc + pair.getSecond());

      if (Tribe.isAlive(currentTribe) && (countLivingNeighbors < 2 ||
          countLivingNeighbors > 3)) {
        nextBoard[row][col] = Tribe.NONE;
      } else if (Tribe.isAlive(currentTribe)) {
        Pair<Tribe, Integer> tmp = tribe_aliveCounts.find(pair -> pair.getFirst() == currentTribe)
            .getOrElse(Pair.of(currentTribe, -1));
        if (tmp.getSecond() == 2 || tmp.getSecond() == 3) {
          nextBoard[row][col] = currentTribe;
        } else if (random.nextDouble() <= .5d) {
          nextBoard[row][col] = currentTribe;
        } else {
          nextBoard[row][col] = Tribe.NONE;
        }
      } else if (!Tribe.isAlive(currentTribe)) {
        Pair<Tribe, Integer> tmp = tribe_aliveCounts
            .find(pair -> pair.getSecond() == 3).getOrElse(Pair.of(Tribe.NONE, -1));
        nextBoard[row][col] = tmp.getFirst();
      }
    }));

    return nextBoard;
  }

  public static Vector<Pair<Tribe, Integer>> countTribeNeighbors(Tribe[][] board, int row,
      int col) {
    Vector<Tribe> neighbors = Vector.rangeClosed(-1, 1)
        .flatMap(offsetRow -> Vector.rangeClosed(-1, 1).map(offsetCol -> Point.from(row + offsetRow, col + offsetCol)))
        .reject(point -> point.getX() - row == 0 && point.getY() - col == 0)
        .filter(point -> point.getX() >= 0 &&
            point.getX() < board.length &&
            point.getY() >= 0 &&
            point.getY() < board[0].length)
        .map(point -> board[point.getX()][point.getY()])
        .filter(tribe -> tribe != Tribe.NONE);

    return Vector.of(Tribe.values())
        .map(tribe -> Pair.of(tribe, neighbors.filter(curr_tribe -> curr_tribe == tribe).size()));

  }
}