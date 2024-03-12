package game_of_life.types;

import game_of_life.utils.Color;
import io.vavr.collection.Vector;

public enum Tribe {
  VIKINGS(Color.blue(), "A"),
  WARRIORS(Color.green(), "B"),
  MAGES(Color.red(), "C"),
  NONE;

  private final String color;
  private final String sigil;

  Tribe() {
    this.color = Color.c_default();
    this.sigil = " ";
  }

  Tribe(String color, String sigil) {
    this.color = color;
    this.sigil = sigil;
  }

  public static Vector<Tribe> getTribes() {
    return Vector.of(Tribe.values())
        .reject(tribe -> tribe == Tribe.NONE);
  }

  public static boolean isAlive(Tribe tribe) {
    return tribe != Tribe.NONE;
  }

  @Override
  public String toString() {
    return this.color + this.sigil;
  }
}