package game_of_life.utils;

public class Pair<F, S> {
  private F first;
  private S second;

  protected Pair(F first, S second) {
      this.first = first;
      this.second = second;
  }

  public static <F, S> Pair<F, S> of(F first, S second) {
    return new Pair<>(first, second);
  }

  public F getFirst() {
      return first;
  }

  public S getSecond() {
      return second;
  }

  @Override
  public String toString() {
      return "(" + first + ", " + second + ")";
  }
}