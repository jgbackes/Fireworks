package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Emitter for Star sparks.
 *
 * @version 1.1
 */
public class StarSparkEmitter extends Emitter {

    private final double LIFETIME = 0.1;        // seconds
    private final int NUM_LAUNCHED = 20;        // how many to launch
    private String color;

    /**
     * The StarSparkEmitter constructor.
     *
     * @param initialPos   The initial X position of the emitter.
     * @param initialV     The initial X velocity component of the emitter.
     * @param exitVelocity The launch velocity of the sparks from the emitter.
     * @param firingAngle  The launch angle of the emitter, from the vertical in degrees.
     * @param variation    The random variation range for the launch angle in degrees.
     * @throws EmitterException If the two angles are not legal.
     */
    public StarSparkEmitter(Point2D.Double initialPos, Point2D.Double initialV,
                            double exitVelocity, double firingAngle, double variation) throws EmitterException {
        super(initialPos, initialV, exitVelocity, firingAngle, variation);
    }

    /**
     * A mutator for the color of the StarSpark to be launched.
     *
     * @param color The desired StarSpark color.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Launches (returns) StarSpark objects at the supplied time.
     *
     * @param time The absolute launch time in seconds.
     * @return An ArrayList of StarSpark objects.
     */
    public ArrayList<Spark> launch(double time) {
        double angle;
        Point2D.Double vInitial;
        Point2D.Double position = getPosition();
        Point2D.Double velocity = getVelocity();
        ArrayList<Spark> sparks = new ArrayList<>(NUM_LAUNCHED);
        double randomTime;
        double exitVelocity = getExitVelocity();
        for (int i = 0; i < NUM_LAUNCHED; i++) {
            angle = getRandomLaunchAngle();
            vInitial = new Point2D.Double(velocity.x + exitVelocity * Math.sin(angle)
                    , velocity.y + exitVelocity * Math.cos(angle));
            // Add some slight variation to time to scatter the sparks a bit more
            randomTime = time + (1.5 * (Math.random() - 0.5));
            sparks.add(new Spark(randomTime, position, vInitial, LIFETIME, color));
        }
        return sparks;
    }
}
