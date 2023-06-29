package com.GP.coordinates;

import androidx.annotation.NonNull;

/**
 * Stores the values of each dimension of a coordinate
 */
public class Position2D {
    public float X;
    public float Y;
    int Dimension;

    /**
     * Initializes a new position
     * @param xParam value of x
     * @param yParam value of y
     */
    public Position2D(@NonNull float xParam, @NonNull float yParam) {
        this.X = xParam;
        this.Y = yParam;
    }

    public float getDistance(@NonNull Position2D positionParam) {

        return new Vector2D(this, positionParam).Length;
    }
}
