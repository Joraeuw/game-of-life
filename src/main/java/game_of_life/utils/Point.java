package game_of_life.utils;

import io.vavr.collection.Vector;

public class Point<T extends Number> extends Pair<T, T> {
  private Point(T x, T y) {
    super(x, y);
  }

  public static <T extends Number> Point<T> from(T x, T y) {
    return new Point<>(x, y);
  }

  public T getX() {
    return getFirst();
  }

  public T getY() {
    return getSecond();
  }

  public double distance(Point<T> other) {
    Point<T> diff = this.subtract(other);

    double dx = Math.abs(diff.getX().doubleValue());
    double dy = Math.abs(diff.getY().doubleValue());

    return Math.sqrt(dx * dx + dy * dy);
  }

  public static Point<Integer> toIntPoint(Point<Double> p) {
    return Point.from(p.getX().intValue(), p.getY().intValue());
  }

  @Override
  public String toString() {
    return "(" + getX() + ", " + getY() + ")";
  }

  @SuppressWarnings("unchecked")
  public Point<T> subtract(Point<T> other) {
    if (this.getX() instanceof Integer && other.getX() instanceof Integer &&
        this.getY() instanceof Integer && other.getY() instanceof Integer) {
      return Point.from(
          (T) (Integer) (this.getX().intValue() - other.getX().intValue()),
          (T) (Integer) (this.getY().intValue() - other.getY().intValue()));
    } else {
      throw new IllegalArgumentException("Unsupported types for subtraction.");
    }
  }

  public static Point<Double> meanPoints(Vector<Point<Integer>> points) {
    double sumX = points.map(p -> p.getX().doubleValue()).sum().doubleValue();
    double sumY = points.map(p -> p.getY().doubleValue()).sum().doubleValue();

    return Point.from(
        sumX / points.size(),
        sumY / points.size());
  }

}
