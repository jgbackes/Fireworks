package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Emitter for launch sparks.
 *
 * @version 1.1
 */
public class LaunchSparkEmitter extends Emitter {

    private final double LIFETIME = 0.15;        // seconds
    private final int NUM_LAUNCHED = 20;        // how many to launch

    /**
     * The LaunchSparkEmitter constructor.
     *
     * @param initialPos   The initial X position of the emitter.
     * @param initialV     The initial X velocity component of the emitter.
     * @param exitVelocity The launch velocity of the sparks from the emitter.
     * @param firingAngle  The launch angle of the emitter, from the vertical in degrees.
     * @param variation    The random variation range for the launch angle in degrees.
     * @throws EmitterException If the two angles are not legal.
     */
    public LaunchSparkEmitter(Point2D.Double initialPos, Point2D.Double initialV,
                              double exitVelocity, double firingAngle, double variation) throws EmitterException {
        super(initialPos, initialV, exitVelocity, firingAngle, variation);
    }

    /**
     * Launches (returns) LaunchSpark objects at the supplied time.
     *
     * @param time The absolute launch time in seconds.
     * @return An ArrayList containing LaunchSpark objects.
     */
    public ArrayList<LaunchSpark> launch(double time) {
        Point2D.Double vInitial;
        double angle;
        Point2D.Double position = getPosition();
        Point2D.Double velocity = getVelocity();
        ArrayList<LaunchSpark> sparks = new ArrayList<>(NUM_LAUNCHED);
        for (int i = 0; i < NUM_LAUNCHED; i++) {
            angle = getRandomLaunchAngle();
            vInitial = new Point2D.Double(velocity.x + getExitVelocity() * Math.sin(angle), velocity.y + getExitVelocity() * Math.cos(angle));
            sparks.add(new LaunchSpark(time, position, vInitial, LIFETIME, "orange"));
        }
        return sparks;
    }
}
