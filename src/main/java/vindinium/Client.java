package vindinium;

import java.util.HashMap;

import java.io.BufferedReader;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Vindinium client.
 */
public final class Client {

    /**
     * Launch client.
     * @param args args[0] Mode: arena or training
     * @param args args[1] Private key
     * @param args args[2] Number of games to play (unused for now)
     * @param args args[3] HTTP URL of Vindinium server
     */
    public static void main(final String[] args) {
        try {
            withModeKeyGamesAndServer(args[0], args[1], Integer.parseInt(args[2]), new URL(args[3]));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid server URL", e);
        } // end of catch
    } // end of main

    /**
     * Runs client with given server |url|, AI |key| and |mode|.
     */
    static void withModeKeyGamesAndServer(final String mode, final String key, final int numberOfTurns, final URL url) {
        System.out.println("Connect to Vindinium server at " + url);

        State state = null;

        final HashMap<String,String> initParams = new HashMap<String,String>(1);
        initParams.put("key", key);

        for (int t = 0; state == null && t < 3; t++) { // Initial state
            try {
                state = IO.fromPost(initParams, "UTF-8", url, "UTF-8", getState);
            } catch (IOException e) {
                System.err.println("Fails to get initial state (" + t +
                                   "). Will try again.");
                e.printStackTrace();
            } // end of catch
        } // end of for

        if (state == null) {
            throw new RuntimeException("Fails to get initial state");
        } // end of if

        // ---

        final Bot bot = new RandomBot(); // Remplace by other Bot
        final HashMap<String,String> ps = new HashMap<String,String>(1);

        while (!state.game.finished) {
            ps.put("dir", bot.nextMove(state).toString());

            System.out.println("Will POST: " + ps);

            try {
                state = IO.fromPost(ps, "UTF-8", url, "UTF-8", getState);
                Thread.sleep(100);
            } catch (Exception e) {
                System.err.println("Fails to get next state");
                e.printStackTrace();
            } // end of catch
        } // end of while
    } // end of withServerUrl

    /**
     * Function getting Vindinium state from I/O reader
     */
    static final UnaryFunction<BufferedReader,State> getState =
        new UnaryFunction<BufferedReader,State>() {
        public State apply(final BufferedReader r) {
            try {
                return Json.next(r, Json.stateReader);
            } catch (IOException e) {
                throw new RuntimeException("Fails to get next state", e);
            }
        }
    };

} // end of class Client
