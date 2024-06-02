package game_of_life;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class Session {
  public final String uuid;
  private Path directory;
  private String filename;
  private Game game;

  public Session(int rows, int cols, int numberOfClusters, double percentageFill, int delay) {
    this.uuid = UUID.randomUUID().toString();
    this.directory = Paths.get("./data");
    this.filename = uuid + ".dat";

    game = new Game(rows, cols, numberOfClusters, percentageFill, delay);
    game.start();
  }

  public Session(String sessionID) throws Error {
    this.uuid = sessionID;
    this.directory = Paths.get("./data");
    this.filename = sessionID + ".dat";

    loadSessionState();
    game.start();
  }

  public void resume() {
    this.game.setExit(false);
    this.game.setPause(false);
    this.game.loop();
  }

  public void saveSessionState() {
    if (directory == null) {
      System.err.println("Directory path is not set. Cannot save session state.");
      return;
    }

    try {
      Files.createDirectories(directory);
      FileOutputStream fileOutputStream = new FileOutputStream(directory.resolve(filename).toFile());
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(game);
      objectOutputStream.close();
      System.out.println("Session state saved successfully.");
    } catch (IOException e) {
      System.err.println("Error occurred while saving session state.");
    }
  }

  public void saveSessionState(String filePath) {
    Path path = Paths.get(filePath);
    this.directory = path.getParent();
    this.filename = path.getFileName().toString();

    saveSessionState();
  }

  public void loadSessionState() throws Error {
    if (directory == null) {
      System.err.println("Directory path is not set. Cannot load session state.");
      return;
    }

    try {
      FileInputStream fileInputStream = new FileInputStream(directory.resolve(filename).toFile());
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      this.game = (Game) objectInputStream.readObject();
      this.game.setExit(false);
      objectInputStream.close();
      System.out.println("Session state loaded successfully.");
    } catch (IOException | ClassNotFoundException e) {
      throw new Error("no_session");
    }
  }
}
