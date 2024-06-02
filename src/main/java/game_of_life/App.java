package game_of_life;

import java.util.HashMap;
import java.util.Stack;
import java.util.function.Function;

import game_of_life.types.CLICommand;
import game_of_life.utils.Console;
import game_of_life.utils.Pair;
import game_of_life.utils.Validator;

public class App {
    public static class AppState {
        boolean shouldExit = false;
        Stack<String> prevSessionIDs = new Stack<>();
        String currentSessionID = "";
    }

    public static void main(String[] args) {
        AppState appState = new AppState();
        Console.run();

        HashMap<String, Session> sessions = new HashMap<>();
        HashMap<CLICommand, Function<String[], Integer>> funMapping = initializeFunctionMapping(appState, sessions);

        while (!appState.shouldExit) {
            Pair<CLICommand, String[]> command_params = CLICommand.read();
            CLICommand command = command_params.getFirst();
            String[] params = command_params.getSecond();

            Function<String[], Integer> fun = funMapping.get(command);

            if (fun != null)
                fun.apply(params);
            else
                System.out.println("Invalid command!");
        }

        Console.close();
    }

    private static HashMap<CLICommand, Function<String[], Integer>> initializeFunctionMapping(AppState appState,
            HashMap<String, Session> sessions) {
        HashMap<CLICommand, Function<String[], Integer>> funMapping = new HashMap<>();

        funMapping.put(CLICommand.EXIT, params -> {
            appState.shouldExit = true;
            System.out.println("Press any character and enter...");

            return 0;
        });

        funMapping.put(CLICommand.NEW, params -> {
            int rows = 40;
            int cols = 90;
            int numberOfClusters = 3;
            double percentageFill = 0.3;
            int delay = 200;

            for (int i = 0; i < params.length; i++) {
                String flag = Console.getParam(params, i);

                if (flag == null)
                    break;

                String error;
                String value;

                switch (flag.toLowerCase()) {

                    case "-rows":
                        error = "-rows is not valid!";
                        value = Console.getParam(params, i + 1, error);
                        if (i + 1 < params.length && Validator.isInteger(value, error)) {
                            rows = Integer.parseInt(value);
                        }
                        break;
                    case "-cols":
                        error = "-cols is not valid!";
                        value = Console.getParam(params, i + 1, error);
                        if (i + 1 < params.length && Validator.isInteger(value, error)) {
                            cols = Integer.parseInt(value);
                        }
                        break;
                    case "-clusters":
                        error = "-clusters is not valid!";
                        value = Console.getParam(params, i + 1, error);
                        if (i + 1 < params.length && Validator.isInteger(value, error)) {
                            numberOfClusters = Integer.parseInt(value);
                        }
                        break;
                    case "-fill":
                        error = "-fill is not valid!";
                        value = Console.getParam(params, i + 1, error);
                        if (i + 1 < params.length && Validator.isDouble(value, error)) {
                            percentageFill = Integer.parseInt(value);
                        }
                        break;
                    case "-delay":
                        error = "-clusters is not valid!";
                        value = Console.getParam(params, i + 1, error);
                        if (i + 1 < params.length && Validator.isInteger(value, error)) {
                            delay = Integer.parseInt(value);
                        }
                        break;
                    default:
                        break;
                }
            }

            Session session = new Session(rows, cols, numberOfClusters, percentageFill, delay);
            sessions.put(session.uuid, session);

            appState.prevSessionIDs.push(appState.currentSessionID);
            appState.currentSessionID = session.uuid;
            return 0;
        });

        funMapping.put(CLICommand.OPEN, params -> {
            String sessionID = Console.getParam(params, 0, "Please provide a session id!");

            if (sessionID == null)
                return 0;

            try {
                appState.prevSessionIDs.push(appState.currentSessionID);
                appState.currentSessionID = sessionID;
                sessions.put(sessionID, new Session(sessionID));
            } catch (Error e) {
                if (e.getMessage() == "no_session") {
                    appState.currentSessionID = appState.prevSessionIDs.pop();
                    System.out.println("Session not found! Provided session id: " + sessionID);
                }
            }
            return 0;
        });

        funMapping.put(CLICommand.SAVE, (params) -> {
            if (params.length == 1 &&
                    (appState.prevSessionIDs.contains(params[0]) || appState.currentSessionID == params[0])) {
                sessions.get(params[0]).saveSessionState();
                System.out.println("Session: " + params[0] + "has been saved.");

            } else if (params.length == 1)
                System.out.println("Invalid session ID!");
            else {
                sessions.get(appState.currentSessionID).saveSessionState();
                System.out.println("Session: " + appState.currentSessionID + "has been saved.");
            }
            return 0;
        });

        funMapping.put(CLICommand.SAVE_AS, (params) -> {
            String filepath = Console.getParam(params, 0, "Please provide a file path!");

            sessions.get(appState.currentSessionID).saveSessionState(filepath);

            if (filepath != null)
                System.out.println("Session: " + appState.currentSessionID + "has been saved on: " + filepath);
            return 0;
        });

        funMapping.put(CLICommand.CLOSE, (params) -> {
            String sessionID = Console.getParam(params, 0, "Please provide a session id!");
            sessions.remove(sessionID);

            if (appState.currentSessionID == sessionID)
                appState.currentSessionID = appState.prevSessionIDs.pop();
            else
                appState.prevSessionIDs.remove(sessionID);

            System.out.println("Session: " + sessionID + " has been closed.");
            return 0;
        });

        funMapping.put(CLICommand.RESUME, (params) -> {
            Session toBeResumed = sessions.get(appState.currentSessionID);
            if (toBeResumed != null)
                toBeResumed.resume();
            else
                System.out.println("There's no active session!");
            return 0;
        });

        funMapping.put(CLICommand.SWITCH, (params) -> {
            String sessionID = Console.getParam(params, 0, "Please provide a session id!");

            if (sessionID == null) {
                return 0;
            } else if (sessionID == appState.currentSessionID)
                return 0;
            else if (appState.prevSessionIDs.contains(sessionID)) {
                appState.prevSessionIDs.remove(sessionID);
                appState.prevSessionIDs.push(appState.currentSessionID);
                appState.currentSessionID = sessionID;

                System.out.println("Switched to session: " + sessionID);
            } else
                System.out.println("Session: " + sessionID + "does not exist!");
            return 0;
        });

        funMapping.put(CLICommand.INFO, (params) -> {
            System.out.println("Current sessionID: " + appState.currentSessionID);
            System.out.println("Previous sessionIDs: ");
            appState.prevSessionIDs.forEach(sessionID -> {
                if (!sessionID.equals(""))
                    System.out.println("\t" + sessionID);
            });

            return 0;
        });

        funMapping.put(CLICommand.HELP, (params) -> {
            System.out.println(
                    "new -rows <rows> -cols <cols> -clusters <number_of_clusters> -fill <percentage_fill> -delay <delay> - creates a new session with given parameters. Any number and combination of parameters can be given."
                            + "\n\t<rows> - number of rows. Defaults to: 40"
                            + "\n\t<cols> - number of cols. Defaults to: 90"
                            + "\n\t<number_of_clusters> - number of clusters. Defaults to: 3. Max: 3"
                            + "\n\t<percentage_fill> - number between 0 and 1, describing the percent of map filled. Defaults to: 0.3"
                            + "\n\t<delay> - The time each generation will stack visualized. Defaults to: 200 (ms)\n");
            System.out.println("open <sessionID> - opens a session by ID if it exists.");
            System.out.println("switch <sessionID> - switch a session by ID if it exists.");
            System.out.println("resume <sessionID> - resumes a session(game) by ID if it exists.");
            System.out.println(
                    "save <sessionID> - saves the session by session id. Defaults to the current sessionID.");
            System.out.println("save_as <filepath> - saves a .dat file in the specified directory.");
            System.out.println("close <sessionID> - closes a session by ID if it exists.]");
            System.out.println("info - displays some useful information about all sessions.");
            System.out.println("help - displays this very menu.");
            System.out.println("exit - exits the program. Sessions will not be saved automatically.");
            appState.prevSessionIDs.forEach(sessionID -> {
                if (!sessionID.equals(""))
                    System.out.println("\t" + sessionID);
            });

            return 0;
        });

        return funMapping;
    }
}