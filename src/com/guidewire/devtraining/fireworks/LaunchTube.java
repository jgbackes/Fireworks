package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Emitter for Roman candle stars.
 *
 * @version 1.1
 */
public class LaunchTube extends Emitter {

    // Keeps track of the number of stars launched so the colors can change.
    private int numLaunched = 0;
    private String[] colors = {"blue", "green", "orange", "red", "yellow", "white", "cyan", "magenta"};

    /**
     * The LaunchTube constructor.
     *
     * @param initialPos   The initial position of the emitter.
     * @param initialV     The initial velocity component of the emitter.
     * @param exitVelocity The launch velocity of the sparks from the emitter.
     * @param firingAngle  The launch angle of the emitter, from the vertical in degrees.
     * @param variation    The random variation range for the launch angle in degrees.
     * @throws EmitterException If the two angles are not legal.  The firing angle must be between -15 and 15
     *                          degrees and the variation must lie between 0 and 10 degrees.
     */
    public LaunchTube(Point2D.Double initialPos, Point2D.Double initialV,
                      double exitVelocity, double firingAngle, double variation) throws EmitterException {
        super(initialPos, initialV, exitVelocity, firingAngle, variation);
        if (firingAngle < -15 || firingAngle > 15)
            throw new EmitterException("Launch angle out of range.");
        if (variation < 0 || variation > 10)
            throw new EmitterException("Variation angle out of range");
    }

    /**
     * Creates and "launches" a star at the given time in seconds.
     *
     * @param time The absolute time in seconds.
     * @return An instance of a Star object in an ArrayList, which will contain its initial position,
     * initial velocity components and the desired color.
     */
    public ArrayList<Star> launch(double time) {
        double angle = getRandomLaunchAngle();
        Point2D.Double position = getPosition();
        Point2D.Double velocity = getVelocity();
        Point2D.Double vInitial = new Point2D.Double(velocity.x + getExitVelocity() * Math.sin(angle)
                , velocity.y + getExitVelocity() * Math.cos(angle));
        ArrayList<Star> star = new ArrayList<>();
        if (numLaunched != 8)
            star.add(new Star(time, position, vInitial, colors[numLaunched]));
        numLaunched++;
        return star;
    }

}
