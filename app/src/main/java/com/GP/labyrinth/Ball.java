package com.GP.labyrinth;

import com.GP.coordinates.Position2D;

public class Ball {
    /*
     * Flag if the ball has the key to open the gate
     */
    public boolean HasKey = true;
    public Position2D Position;



    /*
     * Speed of the ball along the two axis
     */
    public float SpeedX = 0;
    public float SpeedY = 0;

    /*
     * Force pulling on the ball on both axis
     */
    public float ForceX = 0;
    public float ForceY = 0;

    /*
     * Time in milliseconds when position was last updated
     */
    public long LastUpdated = System.currentTimeMillis();


    /**
     * Initializes a new ball
     * @param xPositionParam X-coordinate of the ball
     * @param yPositionParam Y-coordinate of the ball
     */
    public Ball(float xPositionParam, float yPositionParam) {
        this.Position = new Position2D(xPositionParam, yPositionParam);
    }
}
