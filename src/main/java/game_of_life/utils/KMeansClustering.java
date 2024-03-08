package game_of_life.utils;

import io.vavr.collection.List;
import io.vavr.collection.Vector;
import java.util.Random;

public class KMeansClustering {
  @SuppressWarnings("unused")
  public static Vector<Vector<Point<Integer>>> kMeansCluster(Vector<Point<Integer>> points,
      int k, int maxIterations) {
    if (points.size() < k) {
      throw new IllegalArgumentException("Number of clusters cannot be more than the number of points.");
    }

    Vector<Point<Double>> centroids = initializeCentroids(points, k);
    Vector<Vector<Point<Integer>>> clusters = Vector.empty();

    for (Integer _iteration : List.rangeClosed(0, maxIterations)) {
      clusters = centroids.map(centroid -> Vector.empty());
      clusters = assignToCentroids(points, clusters, centroids);
      Vector<Point<Double>> new_centroids = updateCentroids(clusters);

      if (centroids == new_centroids)
        break;
    }

    return clusters;
  }

  private static Vector<Point<Double>> initializeCentroids(Vector<Point<Integer>> points, int k) {
    Random random = new Random();
    return List.range(0, k)
        .map(i -> points.get(random.nextInt(points.size())))
        .map(point -> Point.from(point.getX().doubleValue(), point.getY().doubleValue()))
        .toVector();
  }

  private static Vector<Vector<Point<Integer>>> assignToCentroids(Vector<Point<Integer>> points,
      Vector<Vector<Point<Integer>>> clusters, Vector<Point<Double>> centroids) {
    return points.foldLeft(clusters, (acc, point) -> {
      int closest_centroid_idx = centroids
          .zipWithIndex()
          .minBy(centroid_idx -> point.distance(Point.toIntPoint(centroid_idx._1())))
          .map(centroid_idx -> centroid_idx._2())
          .getOrElse(-1);

      return acc.update(closest_centroid_idx, cluster -> cluster.append(point));
    });
  }

  private static Vector<Point<Double>> updateCentroids(Vector<Vector<Point<Integer>>> clusters) {
    return clusters
        .filter(cluster -> cluster.size() > 0)
        .foldLeft(Vector.empty(), (centroids, cluster) -> centroids.append(Point.meanPoints(cluster)));
  }
}
