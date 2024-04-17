package game_of_life.types;

public enum State {
  RUNNING,
  CYCLING,
  TRIBE_VICTORIOUS,
  EVERYONE_LOST;

  private Tribe victoriousTribe;

  public Tribe getVictoriousTribe() {
    return victoriousTribe;
  }

  public State setVictoriousTribe(Tribe tribe) {
    this.victoriousTribe = tribe;
    return this;
  }
}
