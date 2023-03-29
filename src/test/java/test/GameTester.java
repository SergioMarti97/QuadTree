package test;

import base.GameApplication;
import quadTree.FindBugsGame;
import quadTree.QuadTreeGame;

public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new FindBugsGame());
    }
}
