package com.GP.coordinates;

public class Vector2D {
    public Position2D StartPosition;
    public Position2D EndPosition;
    public float Length;
    public float Angle;


    public Vector2D(Position2D startPositionsParam, Position2D endPositionParam) {

        this.StartPosition = startPositionsParam;
        this.EndPosition = endPositionParam;

        float deltaX = startPositionsParam.X - endPositionParam.X;
        float deltaY = startPositionsParam.Y - endPositionParam.Y;

        // Setze die Werte der Vektorklasse
        this.Length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        this.Angle = (float) Math.atan2(deltaY, deltaX);

    }

    /**
     * gives the distance from starting point to ending point of the vector on x-axis
     * @return distance on x-axis
     */
    public float getDistanceX() {

        return this.StartPosition.X - this.EndPosition.X;
    }

    /**
     * gives the distance from starting point to ending point of the vector on y-axis
     * @return distance on y-axis
     */
    public float getDistanceY() {

        return this.StartPosition.Y - this.EndPosition.Y;
    }
}
