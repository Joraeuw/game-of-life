package game_of_life.types;

import io.vavr.collection.Vector;

public enum Tribe {
  VIKINGS,
  WARRIORS,
  MAGES,
  NONE;

  public static Vector<Tribe> getTribes() {
    return Vector.of(Tribe.values())
        .reject(tribe -> tribe == Tribe.NONE);
  }

  public static boolean isAlive(Tribe tribe) {
    return tribe != Tribe.NONE;
  }

}