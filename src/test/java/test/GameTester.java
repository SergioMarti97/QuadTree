package test;

import base.GameApplication;
import generic.QuadTreeBallGame;
import test.quadTree.bugs.FindBugsGame;

public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new FindBugsGame());
    }
}
