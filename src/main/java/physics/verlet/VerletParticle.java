package physics.verlet;

import base.vectors.points2d.Vec2df;

/**
 * This interfaces implements the method to do a verlet integration step
 */
public interface VerletParticle {

    /**
     * The verlet integration:
     *
     * velocity = position(n) - position(n - 1)
     * position(n + 1) = position(n) + velocity + acceleration * dt^2
     *
     * @param dt elapsed time between two steps
     */
    void doVerletStep(float dt);

    /**
     * Call this method to accelerate the particle
     * @param acc the acceleration
     */
    void accelerate(Vec2df acc);

}
