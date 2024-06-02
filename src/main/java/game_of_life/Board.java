package game_of_life;

import game_of_life.types.State;
import game_of_life.types.Tribe;
import game_of_life.utils.Color;
import game_of_life.utils.Console;
import game_of_life.utils.KMeansClustering;
import game_of_life.utils.Pair;
import game_of_life.utils.Point;
import io.vavr.Function0;
import io.vavr.Function2;
import io.vavr.collection.Stream;
import io.vavr.collection.Vector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Board {
  private static final int ROWS = 40;
  private static final int COLS = 90;
  private static final int NUM_CLUSTERS = 3;
  private static final double PERCENTAGE_FILL = .3d;
  private final long DELAY;

  private LinkedHashSet<Integer> visitedStatesMemo = new LinkedHashSet<>();

  private Tribe[][] frontBuffer;
  private Tribe[][] backBuffer;

  private Board(long delay) {
    frontBuffer = emptyBuffer();
    backBuffer = emptyBuffer();
    this.DELAY = delay;

    Pair<Integer, Integer> xRange = Pair.of(0, ROWS);
    Pair<Integer, Integer> yRange = Pair.of(0, COLS);

    initializeBoard(xRange, yRange);
    prepNextState();
  }

  public static Board create(long delay) {
    return new Board(delay);
  }

  public static Tribe[][] emptyBuffer() {
    Tribe[][] buffer = new Tribe[ROWS][COLS];
    for (Tribe[] row : buffer)
      Arrays.fill(row, Tribe.NONE);
    return buffer;
  }

  public State nextState(Function0<Integer> printAction) {
    Console.clear();
    swapBuffers();
    printAction.apply();
    CompletableFuture.runAsync(this::prepNextState);
    sleep();

    return this.computeBoardNumericalState();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    Function2<Integer, Integer, String> printCell = (i, j) -> frontBuffer[i][j].toString();

    Stream.range(0, ROWS).forEach(i -> {
      String idxStr = Integer.toString(i + 1);
      sb.append(Color.c_default() + String.format("%" + (3 - idxStr.length()) + "s", idxStr) + " ");
      Stream.range(0, COLS).forEach(j -> {
        sb.append(printCell.apply(i, j));
      });
      sb.append("\n");
    });

    sb.append("e) exit | p) pause/start");
    return sb.toString();
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

  private void prepNextState() {
    Random random = new Random();
    Vector<Integer> rows = Vector.range(0, ROWS);
    Vector<Integer> cols = Vector.range(0, COLS);

    rows.forEach(row -> cols.forEach(col -> {
      Tribe currentTribe = frontBuffer[row][col];
      Vector<Pair<Tribe, Integer>> tribe_aliveCounts = this.countTribeNeighbors(row, col);
      int countLivingNeighbors = tribe_aliveCounts.foldLeft(0, (acc, pair) -> acc + pair.getSecond());

      if (Tribe.isAlive(currentTribe) && (countLivingNeighbors < 2 ||
          countLivingNeighbors > 3)) {
        this.backBuffer[row][col] = Tribe.NONE;
      } else if (Tribe.isAlive(currentTribe)) {
        Pair<Tribe, Integer> tmp = tribe_aliveCounts.find(pair -> pair.getFirst() == currentTribe)
            .getOrElse(Pair.of(currentTribe, -1));
        if (tmp.getSecond() == 2 || tmp.getSecond() == 3) {
          this.backBuffer[row][col] = currentTribe;
        } else if (random.nextDouble() <= .5d) {
          this.backBuffer[row][col] = currentTribe;
        } else {
          this.backBuffer[row][col] = Tribe.NONE;
        }
      } else if (!Tribe.isAlive(currentTribe)) {
        Pair<Tribe, Integer> tmp = tribe_aliveCounts
            .find(pair -> pair.getSecond() == 3).getOrElse(Pair.of(Tribe.NONE, -1));
        this.backBuffer[row][col] = tmp.getFirst();
      }
    }));
  }

  private Vector<Pair<Tribe, Integer>> countTribeNeighbors(int row, int col) {
    Vector<Tribe> neighbors = Vector.rangeClosed(-1, 1)
        .flatMap(offsetRow -> Vector.rangeClosed(-1, 1).map(offsetCol -> Point.from(row + offsetRow, col + offsetCol)))
        .reject(point -> point.getX() - row == 0 && point.getY() - col == 0)
        .filter(point -> point.getX() >= 0 &&
            point.getX() < frontBuffer.length &&
            point.getY() >= 0 &&
            point.getY() < frontBuffer[0].length)
        .map(point -> frontBuffer[point.getX()][point.getY()])
        .filter(tribe -> tribe != Tribe.NONE);

    return Vector.of(Tribe.values())
        .map(tribe -> Pair.of(tribe, neighbors.filter(curr_tribe -> curr_tribe == tribe).size()));
  }

  public Pair<Set<Tribe>, Integer> tribeHashBoard() {
    int hash = 0;
    final int prime = 31;
    Set<Tribe> presentTribes = new HashSet<>();

    for (int i = 0; i < ROWS; i++) {
      for (int j = 0; j < COLS; j++) {
        hash = prime * hash + frontBuffer[i][j].hashCode();
        presentTribes.add(frontBuffer[i][j]);
      }
    }
    presentTribes.remove(Tribe.NONE);
    return Pair.of(presentTribes, hash);
  }

  public int cycleFrequency(int hash) {
    int position = 0;
    for (int currentHash : visitedStatesMemo) {
      if (currentHash == hash) {
        return visitedStatesMemo.size() - position - 1;
      }
      position++;
    }

    throw new Error("cycle must exist");
  }

  private State computeBoardNumericalState() {
    Pair<Set<Tribe>, Integer> tribes_hash = tribeHashBoard();
    Set<Tribe> presentTribes = tribes_hash.getFirst();
    Tribe lastTribe = Tribe.NONE;
    for (Tribe tribe : presentTribes)
      lastTribe = tribe;

    Integer currentHash = tribes_hash.getSecond();

    if (visitedStatesMemo.contains(currentHash))
      return State.CYCLING;
    else if (presentTribes.size() == 1 && lastTribe == Tribe.NONE)
      return State.EVERYONE_LOST;
    else if (presentTribes.size() == 1)
      return State.TRIBE_VICTORIOUS.setVictoriousTribe(lastTribe);

    visitedStatesMemo.add(currentHash);

    return State.RUNNING;
  }
}
