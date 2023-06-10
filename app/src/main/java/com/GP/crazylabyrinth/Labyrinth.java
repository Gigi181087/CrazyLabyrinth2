package com.GP.crazylabyrinth;

import android.icu.text.Transliterator;

import java.util.Random;

public class Labyrinth {
    public int _height;
    public int _width;
    private LabyrinthCell _start;
    private LabyrinthCell _end;
    private LabyrinthCell _key;
    private LabyrinthCell _cursor;
    public LabyrinthCell[][] _cells;

    public enum Difficulty {
        Demo,
        Easy,
        Medium,
        Hard
    }


    public Labyrinth(Difficulty difficulty) {

        if(difficulty == Difficulty.Easy) {
            this._height = 10;
            this._width = 10;
        } else if(difficulty == Difficulty.Medium) {
            this._height = 25;
            this._width = 25;
        }
        this._cells = new LabyrinthCell[this._width][];

        for(int i = 0; i < this._width; i++) {
            this._cells[i] = new LabyrinthCell[this._height];
        }

        for(int i = 0; i < this._width; i++) {

            for(int j = 0; j < this._height; j++) {

                if(i > 0) {
                    this._cells[i][j]._left = this._cells[i - 1][j];
                }

                if(i < this._width - 1) {
                    this._cells[i][j]._right = this._cells[i + 1][j];
                }

                if(j > 0) {
                    this._cells[i][j]._up = this._cells[i][j - 1];
                }

                if(j < this._height - 1) {
                    this._cells[i][j]._down = this._cells[i][j + 1];
                }
            }
        }
        this.Create();

        return;
    }

    /**
     * Creates a random labyrinth with a random start and end position and a random key position, if difficulty is higher than easy
     */
    private void Create() {

        do {
            // Create random starting position
            int x = new Random().nextInt(this._width);
            int y = new Random().nextInt(this._height);
            this._start = this._cells[x][y];
            // Create random ending position
            x = new Random().nextInt(this._width);
            y = new Random().nextInt(this._height);
            this._end = this._cells[x][y];
        } while(this._start == this._end);

        // Set cursor to starting position
        this._cursor = _start;
        _cursor.setVisited(true);

        // Create path from starting point to ending point
         do {

            if(this.StepAvailable()) {
                this.DoStep();

            } else {
                this.StepBack();
            }

        } while(this._cursor != this._start);


        return;
    }

    private boolean StepAvailable() {
        boolean retVal = false;

        // There should only be one way from starting and ending point
        if(this._cursor == this._end || this._cursor == this._end) {

            if(this._cursor._up != null) {

                if(this._cursor._up._visited) {

                    return false;
                }
            }

            if(this._cursor._down != null) {

                if(this._cursor._down._visited) {

                    return false;
                }
            }

            if(this._cursor._left != null) {

                if(this._cursor._left._visited) {

                    return false;
                }
            }

            if(this._cursor._right != null) {

                if(this._cursor._right._visited) {

                    return false;
                }
            }
        }

        if(this._cursor._up != null) {

            if(this._cursor._up._visited == false) {

                return true;
            }

        }

        if(this._cursor._down != null) {

            if(this._cursor._down._visited == false) {

                return true;
            }

        }

        if(this._cursor._left != null) {

            if(this._cursor._left._visited == false) {

                return true;
            }

        }

        return false;
    }

    private void DoStep() {

        while(true) {
            int _direction = new Random().nextInt(4);

            switch (_direction) {

                case 0:

                    if (this._cursor._up != null) {

                        if (this._cursor._up._visited != false) {
                            this._cursor = this._cursor._up;
                            this._cursor._visited = true;
                            this._cursor._lastVisit = this._cursor._down;

                            return;
                        }
                    }

                case 1:

                    if (this._cursor._right != null) {

                        if (this._cursor._right._visited != false) {
                            this._cursor = this._cursor._right;
                            this._cursor._visited = true;
                            this._cursor._lastVisit = this._cursor._left;

                            return;
                        }
                    }

                case 2:

                    if (this._cursor._down != null) {

                        if (this._cursor._down._visited != false) {
                            this._cursor = this._cursor._down;
                            this._cursor._visited = true;
                            this._cursor._lastVisit = this._cursor._up;

                            return;
                        }
                    }

                case 3:

                    if (this._cursor._left != null) {

                        if (this._cursor._left._visited != false) {
                            this._cursor = this._cursor._left;
                            this._cursor._visited = true;
                            this._cursor._lastVisit = this._cursor._right;

                            return;
                        }
                    }
            }
        }
    }

    private void StepBack() {
        LabyrinthCell buf = this._cursor._lastVisit;
        this._cursor._lastVisit = null;
        this._cursor = buf;

        return;
    }

    public class LabyrinthCell {
        public LabyrinthCell _up;
        public LabyrinthCell _left;
        public LabyrinthCell _right;
        public LabyrinthCell _down;
        public LabyrinthCell _lastVisit;
        public boolean _visited;


        public void setVisited(boolean value) {
            this._visited = value;
        }

    }

    private class Position {
        public int x;
        public int y;
    }
}
