package physics.overlap;

import physics.ball.Ball;
import physics.grid.Cell;

public class OverlapUtils {

    public static boolean isBallInCell(Ball b, Cell c) {
        return (b.getPos().getX() + b.getSize().getX() > c.getOri().getX() &&
                b.getPos().getY() + b.getSize().getY() > c.getOri().getY() &&
                b.getPos().getX() + b.getSize().getX() <= c.getEnd().getX() &&
                b.getPos().getY() + b.getSize().getY() <= c.getEnd().getY());
    }

}
