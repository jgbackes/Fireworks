package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;

/**
 * A spark.
 *
 * @version 1.0
 */
public class Spark extends Particle {

    private final double RADIUS = 0.0015;    // metre
    private final double MASS = 2E-6;        // kg

    /**
     * The Spark constructor.
     * The lifetime of the spark is set to 0.6 seconds and the render size to 2 pixels.
     *
     * @param creationTime The absolute time of creation of the spark.
     * @param initialPos   The initial position of the spark in the X direction.
     * @param initialV     The initial X velocity component of the spark.
     * @param lifetime     The lifetime of the spark in seconds.
     * @param starColor    The color of the spark.
     */
    public Spark(double creationTime, Point2D.Double initialPos, Point2D.Double initialV
            , double lifetime, String starColor) {
        super(creationTime, initialPos, initialV, starColor);
        setLifetime(lifetime);
        setStartingMass(MASS);
        setStartingRadius(RADIUS);
    }

    public int getRenderSize() {
        return 2;
    }

    /**
     * A simple string representation of the particle.
     *
     * @return A string containing the type and position in metres.
     */
    public String toString() {
        return "Spark" + super.toString();
    }

}
