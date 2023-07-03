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

    public Vector2D(Position2D startPositionParam, float angleParam, float lengthParam) {
        this.StartPosition = startPositionParam;
        this.Length = lengthParam;
        this.Angle = angleParam;

        this.EndPosition = new Position2D(startPositionParam.X + lengthParam * (float) Math.cos(Math.toRadians(angleParam)), startPositionParam.Y + lengthParam * (float) Math.sin(Math.toRadians(angleParam)));
    }

    public Vector2D resolveCollision(Vector2D vectorParam) {

        return new Vector2D(StartPosition, EndPosition);
    }

    public Vector2D multiplyScalar(float scalarParam) {

        return new Vector2D(StartPosition, new Position2D(EndPosition.X * scalarParam, EndPosition.Y * scalarParam));
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
