package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * The base class for Emitter objects.
 */
public abstract class Emitter extends Firework {

    private double launchAngle = 0;                // radians
    private double launchAngleVariation = 0;    // radians
    private double exitVelocity;                // m/sec

    /**
     * The constructor for an Emitter object.
     *
     * @param initialPos   The initial position of the emitter.
     * @param initialV     The initial velocity component of the emitter.
     * @param exitVelocity The launch velocity of the sparks from the emitter.
     * @param firingAngle  The launch angle of the emitter, from the vertical in degrees.
     * @param variation    The random variation range for the launch angle in degrees.
     * @throws EmitterException If the two angles are not legal. The firing angle must lie
     *                          between -180 and 180 degrees, and the variation angle between 0 and 180 degrees, inclusive.
     */
    public Emitter(Point2D.Double initialPos, Point2D.Double initialV,
                   double exitVelocity, double firingAngle, double variation) throws EmitterException {
        super(initialPos, initialV);
        this.exitVelocity = exitVelocity;
        setLaunchAngle(firingAngle);
        if (variation < 0 || variation > 180)
            throw new EmitterException("Firing angle variation out of range: " + variation);
        else
            launchAngleVariation = variation * Math.PI / 180.0;
    }

    /**
     * An accessor that calculates and returns an angle randomly generated between (firing angle - variation)
     * and (firing angle + variation) in radians.
     *
     * @return The launch angle in radians.
     */
    public double getRandomLaunchAngle() {
        return launchAngle + launchAngleVariation * 2 * (Math.random() - 0.5);
    }

    /**
     * An accessor for the exit (or launch) velocity of the emitter.
     *
     * @return The exit velocity in m/sec.
     */
    public double getExitVelocity() {
        return exitVelocity;
    }

    /**
     * A mutator for the exit (or launch) velocity.
     *
     * @param exitVelocity The desired exit velocity in m/sec.
     */
    public void setExitVelocity(double exitVelocity) {
        this.exitVelocity = exitVelocity;
    }

    /**
     * An accessor for the launch angle.
     *
     * @return The launch angle in degrees.
     */
    public double getLaunchAngle() {
        return launchAngle * 180.0 / Math.PI;
    }

    public void setLaunchAngle(double angleDeg) throws EmitterException {
        if (angleDeg < -180 || angleDeg > 180)
            throw new EmitterException("Firing angle out of range: " + angleDeg);
        else
            launchAngle = angleDeg * Math.PI / 180.0;
    }

    /**
     * Launches particles at the supplied time.
     *
     * @param time Time in seconds
     * @return A collection of launched particles.  If the list is empty then the emitter has
     * expired.
     */
    public abstract ArrayList<? extends Particle> launch(double time);

}
