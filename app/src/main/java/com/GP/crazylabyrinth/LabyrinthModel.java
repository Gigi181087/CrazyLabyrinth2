package com.GP.crazylabyrinth;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LabyrinthModel {
    public interface LabyrinthEventListener {
        void OnGameWon();
        void OnWallTouched(float param);
        void OnKeyCollected();
    }

    private List<LabyrinthEventListener> eventListeners;
    public int Height;
    public int Width;
    public Grid Start;
    public Grid End;
    public Grid Key;
    public LabyrinthCell[][] Cells;
    public Ball Ball;
    public Difficulty Level;

    public enum Difficulty {
        DEMO,
        EASY,
        MEDIUM,
        HARD
    }

    public enum EventTypes {
        Reset,
        GameWon,
        WallTouched
    }

    public LabyrinthModel(int WidthParam, int HeightParam, Difficulty difficultyParam) {
        eventListeners = new ArrayList<>();
        this.Width = WidthParam;
        this.Height = HeightParam;
        this.Level = difficultyParam;
        LabyrinthCreator.Create(this);
        this.Ball = new Ball((float) (this.Start.X + 0.9), (float) (this.Start.Y + 0.9));
    }

    public void registerEventListener(LabyrinthEventListener listenerParam) {
        eventListeners.add(listenerParam);
    }

    public void removeEventListener(LabyrinthEventListener listenerParam) {
        eventListeners.remove(listenerParam);
    }
    private void NotifyGameWon() {

        for (LabyrinthEventListener listener : eventListeners) {
            listener.OnGameWon();
        }
    }

    /**
     * Event when ball touches wall.
     * @param speedParam
     */
    private void NotifyWallTouched(@NonNull float speedParam) {

        for (LabyrinthEventListener listener : eventListeners) {
            listener.OnWallTouched(speedParam);
        }
    }

    private void NotifyKeyCollected() {

        for (LabyrinthEventListener listener : eventListeners) {
            listener.OnKeyCollected();
        }
    }

    public class LabyrinthCell {
        public boolean WayUp;
        public boolean WayRight;
        public boolean WayLeft;
        public boolean WayDown;

    }

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

        private void Clone(@NonNull Grid gridParam) {
            this.X = gridParam.X;
            this.Y = gridParam.Y;
        }
    }

    public class Ball {
        /*
         * Flag if the ball has the key to open the gate
         */
        public boolean HasKey = true;

        /*
         * Position of the ball
         */
        public float XPosition = 0;
        public float YPosition = 0;


        /*
         * Speed of the ball along the two axis
         */
        private float xSpeed = 0;
        private float ySpeed = 0;

        /*
         * Force pulling on the ball on both axis
         */
        private float _xForce = 0;
        private float _yForce = 0;

        /*
         * Time in milliseconds when position was last updated
         */
        private long _lastUpdated = System.currentTimeMillis();


        /**
         * Initializes a new ball
         * @param xPositionParam X-coordinate of the ball
         * @param yPositionParam Y-coordinate of the ball
         */
        public Ball(float xPositionParam, float yPositionParam) {
            this.XPosition = xPositionParam;
            this.YPosition = yPositionParam;
        }

        public void UpdatePosition() {
            long _timeNow = System.currentTimeMillis();
            float _timeElapsed = _timeNow - _lastUpdated;

            // calculate new position
            this.XPosition += (xSpeed * _timeElapsed) / 1000;
            this.YPosition += (ySpeed * _timeElapsed) / 1000;

            // calculate new speed
            this.xSpeed += ((this._xForce * _timeElapsed) / 1000) * 0.8;
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
            LabyrinthCell _cell = Cells[(int)this.XPosition][(int)this.YPosition];
            if(new Grid((int)this.XPosition, (int)this.YPosition).Equals(Key)) {
                NotifyKeyCollected();
                this.HasKey = true;
            } else if(new Grid((int)this.XPosition, (int)this.YPosition).Equals(End) && this.HasKey) {
                NotifyGameWon();
            }


            // Check left wall
            if((this.XPosition - (int)this.XPosition) <= 0.29) {

                if(_cell.WayLeft == false) {
                    NotifyWallTouched(this.xSpeed);
                    this.xSpeed *= (-0.5);
                    this.XPosition = (int)this.XPosition + 0.29f;
                }

            // Check right wall
            } else if((this.XPosition - (int)this.XPosition) >= 0.71) {

                if(_cell.WayRight == false) {
                    NotifyWallTouched(this.xSpeed);
                    this.xSpeed *= (-0.5);
                    this.XPosition = (int)this.XPosition + 0.71f;
                }
            }

            // Check top wall
            if((this.YPosition - (int)this.YPosition) <= 0.29) {

                if(_cell.WayUp == false) {
                    NotifyWallTouched(this.ySpeed);
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

    private static class LabyrinthCreator {
        private static boolean[][] visited;
        private static Grid cursor;
        private static List<Grid> steps;
        private static LabyrinthModel labyrinth;

        /**
         * Creates a labyrinth based on the provided LabyrinthModel.
         *
         * @param labyrinthParam The LabyrinthModel used to create the labyrinth.
         */
        private static void Create(@NonNull LabyrinthModel labyrinthParam) {
            labyrinth = labyrinthParam;
            visited = new boolean[labyrinthParam.Width][];
            cursor = labyrinthParam.new Grid(0, 0);
            steps = new ArrayList<>();
            labyrinth.Cells = new LabyrinthCell[labyrinth.Width][];

            for (int i = 0; i < labyrinthParam.Width; i++) {
                labyrinth.Cells[i] = new LabyrinthCell[labyrinth.Height];
                visited[i] = new boolean[labyrinth.Height];
            }

            for (int i = 0; i < labyrinthParam.Height; i++) {

                for (int j = 0; j < labyrinthParam.Width; j++) {
                    labyrinth.Cells[j][i] = labyrinthParam.new LabyrinthCell();
                    visited[j][i] = false;
                }
            }

            // Create random ending position
            int xRandom = new Random().nextInt(labyrinthParam.Width);
            int yRandom = new Random().nextInt(labyrinthParam.Height);
            cursor = labyrinthParam.new Grid(xRandom, yRandom);

            // Set cursor to ending position
            labyrinthParam.End = labyrinthParam.new Grid(cursor);
            visited[cursor.X][cursor.Y] = true;

            // Draw way from ending point to starting point
            do {
                if(NextStep() == false) {
                    StepBack();
                };
                if(cursor.Equals(labyrinth.End)) {
                    //throw new Exception();
                }
            } while(steps.size() < labyrinthParam.Width * labyrinthParam.Height * 0.25);

            labyrinthParam.Start = labyrinthParam.new Grid(cursor);
            StepBack();

            /*
             * Draw way from start point to key, way must extent a fixed length from the original path, so each player has the same level of difficulty
             */
            if(labyrinth.Level == Difficulty.MEDIUM || labyrinth.Level == Difficulty.HARD) {
                int _keyWayLength = 0;

                do {

                    if(NextStep() == false) {

                        if(_keyWayLength > 0) {
                            _keyWayLength--;
                        }
                        StepBack();

                    } else {
                        _keyWayLength++;
                    }

                    if(cursor.Equals(labyrinth.End)) {
                        //throw new LabyrinthCreatorException();
                    }
                } while (_keyWayLength < labyrinthParam.Width * labyrinthParam.Height * 0.1);

                labyrinth.Key = labyrinth.new Grid(cursor);
                StepBack();
            }

            /*
             * Fill the rest of the empty cells
             */
            while(cursor.Equals(labyrinthParam.End) == false) {

                if (NextStep() == false) {
                    StepBack();
                }
            }

            // Clean up
            visited = null;
            cursor = null;
            steps = null;

            System.gc();
        }

        private static boolean NextStep() {
            List<Integer[]> directions = new ArrayList<>();

            if(cursor.Y > 0) {

                if (visited[cursor.X][cursor.Y - 1] == false) {
                    directions.add(new Integer[] {0, -1, 0});
                }

            }
            if(cursor.X < (labyrinth.Width - 1)) {

                if (visited[cursor.X + 1][cursor.Y] == false) {
                    directions.add(new Integer[] {1, 0, 1});
                }

            }
            if(cursor.Y < (labyrinth.Height - 1)) {

                if(visited[cursor.X][cursor.Y + 1] == false) {
                    directions.add(new Integer[] {0, 1, 2});
                }

            }
            if(cursor.X > 0) {

                if(visited[cursor.X - 1][cursor.Y] == false) {
                    directions.add(new Integer[]{-1, 0, 3});
                }
            }

            if(directions.size() > 0) {
                int _index = new Random().nextInt(directions.size());
                steps.add(labyrinth.new Grid(cursor.X, cursor.Y));

                switch((int)directions.get(_index)[2]) {
                    case 0:
                        labyrinth.Cells[cursor.X][cursor.Y].WayUp = true;
                        break;
                    case 1:
                        labyrinth.Cells[cursor.X][cursor.Y].WayRight = true;
                        break;
                    case 2:
                        labyrinth.Cells[cursor.X][cursor.Y].WayDown = true;
                        break;
                    case 3:
                        labyrinth.Cells[cursor.X][cursor.Y].WayLeft = true;
                        break;
                }
                cursor.X += directions.get(_index)[0];
                cursor.Y += directions.get(_index)[1];

                switch((int)((directions.get(_index)[2] + 2) % 4)) {
                    case 0:
                        labyrinth.Cells[cursor.X][cursor.Y].WayUp = true;
                        break;
                    case 1:
                        labyrinth.Cells[cursor.X][cursor.Y].WayRight = true;
                        break;
                    case 2:
                        labyrinth.Cells[cursor.X][cursor.Y].WayDown = true;
                        break;
                    case 3:
                        labyrinth.Cells[cursor.X][cursor.Y].WayLeft = true;
                        break;
                }
                visited[cursor.X][cursor.Y] = true;

                return true;

            } else {
                return false;
            }
        }

        private static void StepBack() {
            cursor.Clone(steps.get(steps.size() - 1));
            steps.remove(steps.size() - 1);
        }
    }

    private static class BallMover {
        private static LabyrinthModel labyrinth;
        private static Grid oldPosition;
        public void UpdateBallPosition(LabyrinthModel labyrinthParam, float xForceParam, float yForceParam) {
            this.labyrinth = labyrinthParam;

            oldPosition = labyrinth.new Grid((int)labyrinth.Ball.XPosition, (int)labyrinth.Ball.YPosition);

            long _timeNow = System.currentTimeMillis();
            float _timeElapsed = _timeNow - labyrinth.Ball._lastUpdated;

            // calculate new position
            labyrinth.Ball.XPosition += (labyrinth.Ball.xSpeed * _timeElapsed) / 1000;
            labyrinth.Ball.YPosition += (labyrinth.Ball.ySpeed * _timeElapsed) / 1000;

            // calculate new speed
            labyrinth.Ball.xSpeed += ((labyrinth.Ball._xForce * _timeElapsed) / 1000) * 0.8;
            labyrinth.Ball.ySpeed += ((labyrinth.Ball._yForce * _timeElapsed) / 1000) * 0.8;

            labyrinth.Ball._lastUpdated = _timeNow;

            this.ResolveCollisions();
        }

        private void ResolveCollisions() {
            LabyrinthCell _cell = labyrinth.Cells[(int)labyrinth.Ball.XPosition][(int)labyrinth.Ball.YPosition];
            // Flags to check corner
            boolean _cornerLeft = false;
            boolean _cornerTop = false;
            boolean _cornerBottom = false;
            boolean _cornerRight = false;

            // Check left wall
            if((labyrinth.Ball.XPosition - (int)labyrinth.Ball.XPosition) <= 0.30) {

                if(_cell.WayLeft == false) {
                    NotifyWallTouched(this.xSpeed);
                    this.xSpeed *= (-0.5);
                    this.XPosition = (int)labyrinth.Ball.XPosition + 0.30f;

                } else {
                    _cornerLeft = true;
                }

                // Check right wall
            } else if((this.XPosition - (int)this.XPosition) >= 0.71) {

                if(_cell.WayRight == false) {
                    NotifyWallTouched(this.xSpeed);
                    this.xSpeed *= (-0.5);
                    this.XPosition = (int)this.XPosition + 0.71f;
                }
            }

            // Check top wall
            if((this.YPosition - (int)this.YPosition) <= 0.29) {

                if(_cell.WayUp == false) {
                    NotifyWallTouched(this.ySpeed);
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

            if(labyrinth.new Grid((int)labyrinth.Ball.XPosition, (int)labyrinth.Ball.YPosition).Equals(labyrinth.Key)) {
                labyrinth.NotifyKeyCollected();
                labyrinth.Ball.HasKey = true;
            } else if(labyrinth.new Grid((int)labyrinth.Ball.XPosition, (int)labyrinth.Ball.YPosition).Equals(labyrinth.End) && labyrinth.Ball.HasKey) {
                labyrinth.NotifyGameWon();
            }
        }
        private boolean IsInCorner() {
            // Überprüfe, ob die Kugel innerhalb des Nähebereichs zur Ecke liegt

            if (Math.abs(ballX - cornerX) <= proximityThreshold && Math.abs(ballY - cornerY) <= proximityThreshold) {
                return true; // Die Kugel ist in der Nähe der Ecke
            } else {
                return false; // Die Kugel ist nicht in der Nähe der Ecke
            }
        }
    }
}

