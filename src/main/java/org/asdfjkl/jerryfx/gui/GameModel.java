package org.asdfjkl.jerryfx.gui;

import org.asdfjkl.jerryfx.lib.*;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class GameModel {

    public static final int MODE_ENTER_MOVES = 0;
    public static final int MODE_ANALYSIS = 1;
    public static final int MODE_PLAY_WHITE = 2;
    public static final int MODE_PLAY_BLACK = 3;
    public static final int MODE_GAME_ANALYSIS = 4;
    public static final int BOTH_PLAYERS = 5;
    public static final int MODE_PLAYOUT_POSITION = 7;
    Game game;
    private ArrayList<StateChangeListener> stateChangeListeners = new ArrayList<>();
    private int currentMode;
    private int multiPv = 1;
    private boolean flipBoard = false;
    private boolean humanPlayerColor = CONSTANTS.WHITE;

    public boolean wasSaved = false;
    private int engineStrength = 20;
    private int engineThinkTimeSecs = 3;

    ArrayList<Engine> engines = new ArrayList<>();
    Engine activeEngine = null;

    private int gameAnalysisForPlayer = BOTH_PLAYERS;
    private int gameAnalysisThreshold = 500; // centipawns
    private int gameAnalysisThinkTime = 3000;

    private boolean gameAnalysisJustStarted = false;

    public String currentBestPv = "";
    public int currentBestEval = 0;
    public int currentMateInMoves = -1;
    public boolean currentIsMate = false;

    public String childBestPv = "";
    public int childBestEval = 0;
    public int childMateInMoves = -1;
    public boolean childIsMate = false;

    public String lastSeenBestmove = "";

    public Polyglot book;

    private Preferences prefs;

    private static final int modelVersion = 4;

    private PgnDatabase pgnDatabase;
    public int currentPgnDatabaseIdx = 0;
    public File lastOpenedDirPath = null;

    private SearchPattern searchPattern;

    public GameModel() {
        this.game = new Game();
        Board b = new Board(true);

        pgnDatabase = new PgnDatabase();
        searchPattern = new SearchPattern();

        this.game.getRootNode().setBoard(b);
        this.currentMode = MODE_ENTER_MOVES;

        String stockfishPath = "";
        String bookPath = "";
        String jarPath = "";
        String path = App.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        System.out.println("path: "+path);
        try {
            jarPath = URLDecoder.decode(path, "UTF-8");
            System.out.println("jarpath: "+jarPath);
            File tmp = (new File(jarPath));
            if(tmp.getParentFile().exists()) {
                File subEngine = new File(tmp.getParentFile(), "engine");
                stockfishPath = new File(subEngine, "stockfish12.exe").getPath();
                System.out.println("stockfishpath: "+stockfishPath);
                File subBook = new File(tmp.getParentFile(), "book");
                bookPath = new File(subBook, "varied.bin").getPath();
                System.out.println("bookpath: "+bookPath);
            } else {
                System.out.println("parent not exists");
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("exception in getting jar path");
            e.printStackTrace();
        }

        Engine stockfish = new Engine();
        stockfish.setName("Stockfish (Internal)");
        //stockfish.setPath("C:\\Program Files (x86)\\Jerry_Chess\\engine\\stockfish.exe");
        System.out.println(stockfishPath);
        stockfish.setPath(stockfishPath);
        stockfish.setInternal(true);
        engines.add(stockfish);
        activeEngine = stockfish;

        book = new Polyglot();
        File file = null;
        System.out.println(bookPath);
        file = new File(bookPath);
        book.loadBook(file);

    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getGameAnalysisForPlayer() { return gameAnalysisForPlayer; }

    public void setGameAnalysisForPlayer(int player) { gameAnalysisForPlayer = player; }

    public int getGameAnalysisThreshold() { return gameAnalysisThreshold; }

    public void setGameAnalysisThreshold(int threshold) { gameAnalysisThreshold = threshold; }

    public void setGameAnalysisThinkTime(int thinktime) { gameAnalysisThinkTime = thinktime; }

    public int getGameAnalysisThinkTime() { return gameAnalysisThinkTime; }

    public void setMode(int mode) {
        this.currentMode = mode;
    }

    public void setComputerThinkTimeSecs(int secs) {
        engineThinkTimeSecs = secs;
    }

    public int getComputerThinkTimeSecs() {
        return engineThinkTimeSecs;
    }

    public int getEngineStrength() { return engineStrength; }

    public void setEngineStrength(int strength) { engineStrength = strength; }

    public int getMode() {
        return currentMode;
    }

    public int getMultiPv() {
        return multiPv;
    }

    public void setFlipBoard(boolean flipBoard) {
        this.flipBoard = flipBoard;
    }

    public boolean getFlipBoard() {
        return flipBoard;
    }

    public void setHumanPlayerColor(boolean humanPlayerColor) {
        this.humanPlayerColor = humanPlayerColor;
    }

    public boolean getHumanPlayerColor() {
        return humanPlayerColor;
    }

    public boolean getGameAnalysisJustStarted() { return gameAnalysisJustStarted; }

    public void setGameAnalysisJustStarted(boolean val) { gameAnalysisJustStarted = val; }

    public PgnDatabase getPgnDatabase() {
        return pgnDatabase;
    }

    public SearchPattern getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(SearchPattern searchPattern) {
        this.searchPattern = searchPattern;
    }

    public void setMultiPv(int multiPv) {
        if(multiPv < 1) {
            throw new IllegalArgumentException("setMultiPV: "+multiPv+ " but must be >= 1!");
        }
        if(multiPv > 4) {
            throw new IllegalArgumentException("setMultiPV: "+multiPv+ " but must be <= 4!");
        }
        this.multiPv = multiPv;
    }


    public void addListener(StateChangeListener toAdd) {
        stateChangeListeners.add(toAdd);
    }

    public void triggerStateChange() {
        //System.out.println("state change");

        for (StateChangeListener sl : stateChangeListeners)
            sl.stateChange();
    }

    public void saveModel() {

        prefs = Preferences.userRoot().node(this.getClass().getName());

        prefs.putInt("modelVersion",modelVersion);

        PgnPrinter printer = new PgnPrinter();
        String pgn = printer.printGame(getGame());
        prefs.put("curentGame", pgn);

    }

    public void saveEngines() {

        prefs = Preferences.userRoot().node(this.getClass().getName());
        for(int i=1;i<engines.size();i++) {
            Engine engine = engines.get(i);
            String engineString = engine.writeToString();
            prefs.put("ENGINE"+i, engineString);
            System.out.println("SAVED: "+engineString);
        }
        prefs.putInt("ACTIVE_ENGINE_IDX", engines.indexOf(activeEngine));
    }

    public void restoreEngines() {

        prefs = Preferences.userRoot().node(this.getClass().getName());
        int mVersion = prefs.getInt("modelVersion", 0);

        if(mVersion == modelVersion) {

            for(int i=1;i<10;i++) {
                String engineString = prefs.get("ENGINE"+i, "");
                System.out.println("RESTORED: "+engineString);
                if(!engineString.isEmpty()) {
                    Engine engine = new Engine();
                    engine.restoreFromString(engineString);
                    engines.add(engine);
                }
            }
            int activeIdx = prefs.getInt("ACTIVE_ENGINE_IDX", 0);
            if(activeIdx < engines.size()) {
                activeEngine = engines.get(activeIdx);
            } else {
                activeEngine = engines.get(0);
            }
        }
    }

    public void saveScreenGeometry(ScreenGeometry g) {

        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

        prefs.putDouble("WINDOW_POSITION_X", g.xOffset);
        prefs.putDouble("WINDOW_POSITION_Y", g.yOffset);
        prefs.putDouble("WINDOW_WIDTH", g.width);
        prefs.putDouble("WINDOW_HEIGHT", g.height);
        prefs.putDouble("MOVE_DIVIDER_RATIO", g.moveDividerRatio);
        prefs.putDouble("MAIN_DIVIDER_RATIO", g.mainDividerRatio);

        System.out.println("saved main div ratio: "+g.mainDividerRatio);
        /*
        System.out.println("saved: "+g.xOffset);
        System.out.println("saved: "+g.yOffset);
        System.out.println("saved: "+g.width);
        System.out.println("saved: "+g.height);
        System.out.println("saved: "+g.moveDividerRatio);
        System.out.println("saved: "+g.mainDividerRatio);

         */

    }

    public ScreenGeometry restoreScreenGeometry() {

        prefs = Preferences.userRoot().node(this.getClass().getName());
        int mVersion = prefs.getInt("modelVersion", 0);

        ScreenGeometry g = new ScreenGeometry(-1.0, -1.0, -1.0,-1.0,
                ScreenGeometry.DEFAULT_MOVE_DIVIDER_RATIO,
                ScreenGeometry.DEFAULT_MAIN_DIVIDER_RATIO);

        if(mVersion == modelVersion) {

            g.xOffset = prefs.getDouble("WINDOW_POSITION_X", -1.0);
            g.yOffset = prefs.getDouble("WINDOW_POSITION_Y", -1.0);
            g.width = prefs.getDouble("WINDOW_WIDTH", -1.0);
            g.height = prefs.getDouble("WINDOW_HEIGHT", -1.0);
            g.moveDividerRatio = prefs.getDouble("MOVE_DIVIDER_RATIO",
                    ScreenGeometry.DEFAULT_MOVE_DIVIDER_RATIO);
            g.mainDividerRatio = prefs.getDouble("MAIN_DIVIDER_RATIO",
                    ScreenGeometry.DEFAULT_MAIN_DIVIDER_RATIO);

        }
        /*
        System.out.println("restored: "+g.xOffset);
        System.out.println("restored: "+g.yOffset);
        System.out.println("restored: "+g.width);
        System.out.println("restored: "+g.height);
        System.out.println("restored: "+g.moveDividerRatio);
        System.out.println("restored: "+g.mainDividerRatio);
        System.out.println("restored: "+g.isValid());

         */
        return g;

    }

    public void restoreModel() {

        prefs = Preferences.userRoot().node(this.getClass().getName());
        int mVersion = prefs.getInt("modelVersion", 0);

        //intln(mVersion);
        if(mVersion == modelVersion) {
            //System.out.println("model version ok");

            PgnReader reader = new PgnReader();

            String pgn = prefs.get("curentGame", "");

            //System.out.println("string" + pgn);
            if(!pgn.isEmpty()) {
                Game g = reader.readGame(pgn);
                PgnPrinter p = new PgnPrinter();
                //System.out.println("read: " + p.printGame(g));
                if (g.getRootNode().getBoard().isConsistent()) {
                    //System.out.println("setting game");
                    setGame(g);
                    g.setTreeWasChanged(true);
                    //triggerStateChange();
                }
            }
        }

    }


}



