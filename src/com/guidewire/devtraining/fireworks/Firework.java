package com.guidewire.devtraining.fireworks;

import java.awt.geom.Point2D;

/**
 * A base class for all Emitter and Particle objects.  This class holds position and
 * velocity data.
 */
public class Firework {

    private Point2D.Double position;
    private Point2D.Double velocity;

    /**
     * The Firework constructor.
     *
     * @param Pos The position in metres.
     * @param Vel The velocity component in m/sec.
     */
    public Firework(Point2D.Double Pos, Point2D.Double Vel) {
        position = Pos;
        velocity = Vel;
    }

    /**
     * An accessor for position data.
     *
     * @return An array for the (x, y) position in metres.
     */
    public Point2D.Double getPosition() {
        return (Point2D.Double) position.clone();
    }

    /**
     * A mutator for the position data.
     *
     * @param position An array containing the (x, y) position data in metres.
     */
    public void setPosition(Point2D.Double position) {
        this.position = (Point2D.Double) position.clone();
    }

    /**
     * An accessor for the velocity data.
     *
     * @return An array for the (vx, vy) velocity data in m/sec.
     */
    public Point2D.Double getVelocity() {
        return (Point2D.Double) velocity.clone();
    }

    /**
     * A mutator for the velocity data.
     *
     * @param velocity An array containing the (vx, vy) velocity data in m/sec.
     */
    public void setVelocity(Point2D.Double velocity) {
        this.velocity = (Point2D.Double) velocity.clone();
    }

    /**
     * Returns a string representation of the position.
     *
     * @return A string containing the position in metres.
     */
    public String toString() {
        return String.format("(%5.2f, %5.2f)", position.x, position.y);
    }

}
