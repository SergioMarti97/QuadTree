package test;

import base.GameApplication;
import grid.GridCircleGame;
import quadTree.QuadTreeCircleGame;
import sweepAndPrune.SweepAndPruneGame;

public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new QuadTreeCircleGame());
    }
}
