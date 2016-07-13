package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;

/**
 * Launch sparks will be rendered differently from other sparks, so it is necessary that they
 * are represented by a different class.
 */
public class LaunchSpark extends Spark {

    private final double RADIUS = 0.0005;    // metre

    /**
     * The LaunchSpark constructor.
     *
     * @param creationTime The absolute time of creation of the spark.
     * @param initialPos   The initial position of the spark.
     * @param initialV     The initial velocity component of the spark.
     * @param lifetime     The lifetime of the spark in seconds.
     * @param starColor    The color of the spark.
     */
    public LaunchSpark(double creationTime, Point2D.Double initialPos, Point2D.Double initialV,
                       double lifetime, String starColor) {
        super(creationTime, initialPos, initialV, lifetime, starColor);
        this.setStartingRadius(RADIUS);
    }

    /**
     * A simple string representation of the particle.
     *
     * @return A string containing the type and position in metres.
     */
    public String toString() {
        return "Launch " + super.toString();
    }

}
