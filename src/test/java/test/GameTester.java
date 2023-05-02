package test;

import base.GameApplication;
import generic.KdTreeBallGame;

public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new KdTreeBallGame());
    }
}
