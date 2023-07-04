package com.GP.coordinates;

import androidx.annotation.NonNull;

/**
 * class for creating the grid
 */
public class Grid {
    public int X;
    public int Y;

    /**
     * Constructor
     * @param gridParam copies a grid while creating
     */
    public Grid(Grid gridParam) {
        this.X = gridParam.X;
        this.Y = gridParam.Y;
    }

    /**
     * Constructor
     * @param xParam is initialized with value
     * @param yParam is initialized with value
     */
    public Grid (int xParam, int yParam) {
        this.X = xParam;
        this.Y = yParam;
    }

    /**
     * checks whether a value is present
     * @param gridParam value of x and y from the grid
     * @return result
     */
    public boolean Equals(Grid gridParam) {
        if(gridParam == null) {

            return false;
        }

        if((this.X != gridParam.X) ||(this.Y != gridParam.Y)) {

            return false;

        } else {

            return true;
        }
    }

    public void Clone(@NonNull Grid gridParam) {
        this.X = gridParam.X;
        this.Y = gridParam.Y;
    }
}
