package test;

import base.GameApplication;
import quadTree.FindBugsGame;
import quadTree.QuadTreeCircleGame;
import quadTree.QuadTreeRectGame;

public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new QuadTreeCircleGame());
    }
}
