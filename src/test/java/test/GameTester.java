package test;

import base.GameApplication;
import generic.AllVsAllBallGame;
import generic.KdTreeBallGame;
import generic.QuadTreeBallGame;
import shape.ShapeGame;
import verlet.AllVsAllVerletGame;
import verlet.QuadTreeVerletGame;

public class GameTester extends GameApplication {

    @Override
    public void init() throws Exception {
        super.init();
        setAppName("Test collisions");
        setGame(new QuadTreeVerletGame());
    }
}
