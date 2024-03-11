package game_of_life;

import game_of_life.types.Tribe;
import game_of_life.utils.Color;
import game_of_life.utils.Convolution;
import game_of_life.utils.KMeansClustering;
import game_of_life.utils.Kernel;
import game_of_life.utils.Pair;
import game_of_life.utils.Point;

import io.vavr.Function2;
import io.vavr.collection.Stream;
import io.vavr.collection.Vector;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Board {
  private static final int ROWS = 40;
  private static final int COLS = 90;
  private static final int NUM_CLUSTERS = 3;
  private static final double PERCENTAGE_FILL = .3d;
  private final long DELAY;

  private Tribe[][] frontBuffer;
  private Tribe[][] backBuffer;

  private Board(long delay) {
    frontBuffer = new Tribe[ROWS][COLS];
    backBuffer = new Tribe[ROWS][COLS];
    this.DELAY = delay;

    for (Tribe[] row : frontBuffer)
      Arrays.fill(row, Tribe.NONE);

    for (Tribe[] row : backBuffer)
      Arrays.fill(row, Tribe.NONE);

    Pair<Integer, Integer> xRange = Pair.of(0, ROWS);
    Pair<Integer, Integer> yRange = Pair.of(0, COLS);

    initializeBoard(xRange, yRange);
    prepNextState();
  }

  public static Board create(long delay) {
    return new Board(delay);
  }

  public void nextState() {
    clearConsole();
    swapBuffers();
    printBoard();
    CompletableFuture.runAsync(this::prepNextState);
    sleep();
  }

  private void prepNextState() {
    backBuffer = Convolution.apply(frontBuffer);
  }

  public void printBoard() {
    Function2<Integer, Integer, String> printCell = (i, j) -> getSigil(frontBuffer[i][j]);

    Stream.range(0, ROWS).forEach(i -> {
      String idxStr = Integer.toString(i + 1);
      System.out.print(Color.c_default() + String.format("%" + (3 - idxStr.length()) + "s", idxStr) + " ");
      Stream.range(0, COLS).forEach(j -> {
        System.out.print(printCell.apply(i, j));
      });
      System.out.println();
    });
  }

  private void initializeBoard(Pair<Integer, Integer> xRange, Pair<Integer, Integer> yRange) {
    Vector<Point<Integer>> points = Board.generatePositions(xRange, yRange);
    Vector<Vector<Point<Integer>>> clusters = KMeansClustering.kMeansCluster(points, NUM_CLUSTERS);
    Tribe[] tribes = Tribe.values();

    clusters.zipWithIndex()
        .map(cluster_idx -> Pair.of(tribes[cluster_idx._2()], cluster_idx._1()))
        .forEach(tribe_cluster -> {
          Tribe tribe = tribe_cluster.getFirst();
          tribe_cluster.getSecond().forEach(point -> frontBuffer[point.getX()][point.getY()] = tribe);
        });
  }

  private static Vector<Point<Integer>> generatePositions(
      Pair<Integer, Integer> xRange,
      Pair<Integer, Integer> yRange) {
    Random random = new Random();

    Vector<Integer> xRangeVector = Vector.range(xRange.getFirst(), xRange.getSecond());
    Vector<Integer> yRangeVector = Vector.range(yRange.getFirst(), yRange.getSecond());

    return yRangeVector.flatMap(y -> xRangeVector.filter(_x -> random.nextDouble() < PERCENTAGE_FILL)
        .map(x -> Point.from(x, y)));
  }

  private String getSigil(Tribe tribe) {
    switch (tribe) {
      case VIKINGS:
        return Color.blue() + "A";
      case WARRIORS:
        return Color.green() + "B";
      case MAGES:
        return Color.red() + "C";
      default:
        return " ";
    }
  }

  private void swapBuffers() {
    Tribe[][] temp = frontBuffer;
    frontBuffer = backBuffer;
    backBuffer = temp;
  }

  private void sleep() {
    try {
      TimeUnit.MILLISECONDS.sleep(DELAY);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void clearConsole() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

}
