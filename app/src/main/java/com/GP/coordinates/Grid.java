package com.GP.coordinates;

import androidx.annotation.NonNull;

public class Grid {
    public int X;
    public int Y;

    public Grid(Grid gridParam) {
        this.X = gridParam.X;
        this.Y = gridParam.Y;
    }
    public Grid (int xParam, int yParam) {
        this.X = xParam;
        this.Y = yParam;
    }

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
