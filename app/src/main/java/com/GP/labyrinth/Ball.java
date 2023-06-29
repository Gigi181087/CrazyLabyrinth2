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

    public void UpdatePosition() {
        long _timeNow = System.currentTimeMillis();
        float _timeElapsed = _timeNow - _lastUpdated;

        // calculate new position
        this.XPosition += (SpeedX * _timeElapsed) / 1000;
        this.YPosition += (ySpeed * _timeElapsed) / 1000;

        // calculate new speed
        this.SpeedX += ((this._xForce * _timeElapsed) / 1000) * 0.8;
        this.ySpeed += ((this._yForce * _timeElapsed) / 1000) * 0.8;

        _lastUpdated = _timeNow;

        HandleCollision();
    }

    public void UpdatePosition(float xForceParam, float yForceParam) {
        UpdatePosition();

        // update force values
        this._xForce = xForceParam;
        this._yForce = yForceParam;
    }

    private void HandleCollision() {
        LabyrinthModel.LabyrinthCell _cell = Cells[(int)this.XPosition][(int)this.YPosition];
        if(new LabyrinthModel.Grid((int)this.XPosition, (int)this.YPosition).Equals(Key)) {
            NotifyKeyCollected();
            this.HasKey = true;
        } else if(new LabyrinthModel.Grid((int)this.XPosition, (int)this.YPosition).Equals(End) && this.HasKey) {
            NotifyGameWon();
        }


        // Check left wall
        if((this.XPosition - (int)this.XPosition) <= 0.29) {

            if(_cell.WayLeft == false) {
                NotifyWallTouched(this.SpeedX);
                this.SpeedX *= (-0.5);
                this.XPosition = (int)this.XPosition + 0.29f;
            }

            // Check right wall
        } else if((this.XPosition - (int)this.XPosition) >= 0.71) {

            if(_cell.WayRight == false) {
                NotifyWallTouched(this.SpeedX);
                this.SpeedX *= (-0.5);
                this.XPosition = (int)this.XPosition + 0.71f;
            }
        }

        // Check top wall
        if((this.YPosition - (int)this.YPosition) <= 0.29) {

            if(_cell.WayUp == false) {
                labyrinth.NotifyWallTouched(this.ySpeed);
                this.ySpeed *= (-0.5);
                this.YPosition = (int)this.YPosition + 0.29f;
            }

            // Check bottom wall
        } else if((this.YPosition - (int)this.YPosition) >= 0.71) {

            if(_cell.WayDown == false) {
                NotifyWallTouched(this.ySpeed);
                this.ySpeed *= (-0.5);
                this.YPosition = (int)this.YPosition + 0.71f;
            }
        }
    }
}
