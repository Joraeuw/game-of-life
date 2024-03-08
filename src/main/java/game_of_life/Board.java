package game_of_life;

import game_of_life.types.Tribe;
import game_of_life.utils.KMeansClustering;
import game_of_life.utils.Pair;
import game_of_life.utils.Point;
import io.vavr.Function2;
import io.vavr.collection.Stream;
import io.vavr.collection.Vector;
import java.util.Random;

public class Board {
  private static final int ROWS = 40;
  private static final int COLS = 40;
  private static final int NUM_CLUSTERS = 3;
  private static final int MAX_ITERATIONS = 300;

  private Tribe[][] board;

  public Board() {
    board = new Tribe[ROWS][COLS];

    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        board[i][j] = Tribe.NONE;
      }
    }
    Pair<Integer, Integer> xRange = Pair.of(0, ROWS - 1);
    Pair<Integer, Integer> yRange = Pair.of(0, COLS - 1);

    initializeBoard(xRange, yRange);
  }

  private void initializeBoard(Pair<Integer, Integer> xRange, Pair<Integer, Integer> yRange) {
    Vector<Point<Integer>> points = Board.generatePositions(xRange, yRange, .3d);
    Vector<Vector<Point<Integer>>> clusters = KMeansClustering.kMeansCluster(points, NUM_CLUSTERS, MAX_ITERATIONS);
    Tribe[] tribes = Tribe.values();

    clusters.zipWithIndex()
        .map(cluster_idx -> Pair.of(tribes[cluster_idx._2()], cluster_idx._1()))
        .forEach(tribe_cluster -> {
          Tribe tribe = tribe_cluster.getFirst();
          tribe_cluster.getSecond().forEach(point -> board[point.getX()][point.getY()] = tribe);
        });
  }

  private static Vector<Point<Integer>> generatePositions(
      Pair<Integer, Integer> xRange,
      Pair<Integer, Integer> yRange,
      double percentage_fill) {
    Random random = new Random();

    Vector<Integer> xRangeVector = Vector.range(xRange.getFirst(), xRange.getSecond());
    Vector<Integer> yRangeVector = Vector.range(yRange.getFirst(), yRange.getSecond());

    return yRangeVector.flatMap(y -> xRangeVector.filter(_x -> random.nextDouble() > (1 - percentage_fill))
        .map(x -> Point.from(x, y)));
  }

  public void printBoard() {
    Function2<Integer, Integer, String> printCell = (i, j) -> {
      String sigil = getSigil(board[i][j]);
      return String.format("%-3s", sigil);
    };

    Stream.range(0, ROWS).forEach(i -> {
      Stream.range(0, COLS).forEach(j -> {
        System.out.print(printCell.apply(i, j));
      });
      System.out.println();
    });
  }

  private String getSigil(Tribe tribe) {
    switch (tribe) {
      case VIKINGS:
        return "\u001B[31m" + "A";
      case WARRIORS:
        return "\u001B[32m" + "B";
      case MAGES:
        return "\u001B[33m" + "C";
      default:
        return " ";
    }
  }
}
