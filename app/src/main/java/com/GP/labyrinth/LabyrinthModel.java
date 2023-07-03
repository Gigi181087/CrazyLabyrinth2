package com.GP.labyrinth;

import androidx.annotation.NonNull;

// import coordinates package
import com.GP.coordinates.Vector2D;
import com.GP.coordinates.Position2D;
import com.GP.coordinates.Grid;

// import labyrinth package
import com.GP.labyrinth.Ball;
import com.GP.labyrinth.LabyrinthModel;
import com.GP.labyrinth.LabyrinthView;
import com.GP.mqtt.MQTTManager;

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

    public float WallSize = 0.05f;

    public float BallSize = 0.25f;
    public Grid Start;
    public Grid End;
    public Grid Key;
    public LabyrinthCell[][] Cells;
    public Ball Ball;
    public Difficulty Level;

    public enum Difficulty {
        DEMO(9),
        EASY(0),
        MEDIUM(1),
        HARD(2);

        private int intValue;

        private Difficulty(int valueParam) {
            this.intValue = valueParam;
        }

        public int getValue() {

            return intValue;
        }

        public static Difficulty fromInt(int intValue) {
            for (Difficulty value : Difficulty.values()) {
                if (value.getValue() == intValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Invalid integer value for MyEnum: " + intValue);
        }
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
        this.Ball = new Ball((float) (this.Start.X + 0.5f), (float) (this.Start.Y + 0.5f));
    }

    public void updateBallPosition(float xForceParam, float yForceParam) {
        BallMover.updateBallPosition(this, xForceParam, yForceParam);
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
            cursor = new Grid(0, 0);
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
            cursor = new Grid(xRandom, yRandom);

            // Set cursor to ending position
            labyrinthParam.End = new Grid(cursor);
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

            labyrinthParam.Start = new Grid(cursor);
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

                labyrinth.Key = new Grid(cursor);
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
                steps.add(new Grid(cursor.X, cursor.Y));

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

        // Created by ChatGPT and unchanged



        private static LabyrinthModel labyrinth;
        private static Grid startGrid;

        private static Vector2D movementVector;
        private static void updateBallPosition(@NonNull LabyrinthModel labyrinthParam, float xForceParam, float yForceParam) {
            labyrinth = labyrinthParam;


            // Create movement vector from accelerator forces
            startGrid = new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y);

            long _timeNow = System.currentTimeMillis();
            float _timeElapsed = _timeNow - labyrinth.Ball.LastUpdated;

            // calculate new position
            float _newX = (float)(labyrinth.Ball.Position.X + (labyrinth.Ball.SpeedX * 0.8 * _timeElapsed) / 1000);
            float _newY = (float)(labyrinth.Ball.Position.Y + (labyrinth.Ball.SpeedY * 0.8 * _timeElapsed) / 1000);

            // calculate new speed
            labyrinth.Ball.SpeedX += ((labyrinth.Ball.ForceX * _timeElapsed) / 1000);
            labyrinth.Ball.SpeedY += ((labyrinth.Ball.ForceY * _timeElapsed) / 1000);

            labyrinth.Ball.ForceX = xForceParam;
            labyrinth.Ball.ForceY = yForceParam;

            labyrinth.Ball.LastUpdated = _timeNow;

            movementVector = new Vector2D(labyrinth.Ball.Position, new Position2D(_newX, _newY));

            /*
             * Check if vector collides with any wall
             */

            // Check top wall
            resolveMovement();
        }

        private static void resolveMovement() {
            startGrid = new Grid((int)movementVector.StartPosition.X, (int)movementVector.StartPosition.Y);
            LabyrinthCell _cell = labyrinth.Cells[(int)labyrinth.Ball.Position.X][(int)labyrinth.Ball.Position.Y];
            labyrinth.Ball.Position = movementVector.EndPosition;
             /*
            // Collision
            if(!this.checkWithinBounds()) {

                // Create position of the ball edges
                Position2D topEdge = this.labyrinth.Ball.Position.add(0, -this.labyrinth.BallSize);
                Position2D _rightEdge = this.labyrinth.Ball.Position.add(labyrinth.BallSize, 0);
                Position2D bottomEdge = this.labyrinth.Ball.Position.add(0, labyrinth.BallSize);
                Position2D leftEdge = this.labyrinth.Ball.Position.add(labyrinth.BallSize, 0);

                // Create position of the corners
                Position2D topRightCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(1 - this.labyrinth.WallSize, this.labyrinth.WallSize);
                Position2D _bottomRightCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(1 - this.labyrinth.WallSize, 1 - this.labyrinth.WallSize);
                Position2D bottomLeftCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(this.labyrinth.WallSize, 1 - this.labyrinth.WallSize);
                Position2D topLeftCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(this.labyrinth.WallSize, this.labyrinth.WallSize);

                // Check top right corner
                if(checkVectorBetweenPositions(new Vector2D(_rightEdge, movementVector.Length, movementVector.Angle), new Position2D(startGrid.X, startGrid.Y).add(0.5f, 0), topRightCorner)) { // Check top side of cell

                    if(!_cell.WayUp) {  // Case there is a wall
                        float _scalar = (topEdge.Y - (startGrid.Y + labyrinth.WallSize)) / movementVector.getDistanceY();
                        Vector2D _topWallVector = new Vector2D(new Position2D(startGrid.X + 0.5f, startGrid.Y + labyrinth.WallSize), topRightCorner);

                        this.movementVector = this.movementVector.resolveCollision(_topWallVector).multiplyScalar(0.8f);

                        this.resolveMovement();

                        return;

                    } else { // Case there is no wall

                        // Check if ball touches the corner
                        if(checkVectorBetweenPositions(movementVector, new Position2D(startGrid.X, startGrid.Y).add(0.5f, -labyrinth.WallSize), new Position2D(startGrid.X, startGrid.Y).add(1 - labyrinth.WallSize, -labyrinth.WallSize))) {
                            this.moveBallToAxisY(this.startGrid.Y - this.labyrinth.WallSize);
                            this.resolveMovement();

                        }



                        // Check if ball bounces of the corner
                        if(this.movementVector.Angle > this.new Vector2D(this.labyrinth.Ball.XPosition + this.labyrinth.BallSize, this.labyrinth.Ball.YPosition, this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize).Angle) {
                            this.movementVector.resolveCollision(this.new Vector2D(this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize,this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y - this.labyrinth.WallSize));
                            this.resolveMovement();

                        // Move ball to the next cell
                        } else {
                            this.moveBallToAxisY(this.startGrid.Y - this.labyrinth.WallSize);
                            this.resolveMovement();
                        }
                    }

                // Check bottom right corner
                } else if (this.movementVector.Angle < this.new Vector2D(this.labyrinth.Ball.XPosition, this.labyrinth.Ball.YPosition + this.labyrinth.BallSize, this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize).Angle) {

                    // Case that a wall is on the right side
                    if(!_cell.WayRight) {
                        this.movementVector.resolveCollision(this.new Vector2D(this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + 1 - this.labyrinth.WallSize,this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize));
                        this.resolveMovement();

                        return;

                    } else {

                        // Check if ball bounces of the corners
                        // Top corner
                        if(this.movementVector.Angle < this.new Vector2D(this.labyrinth.Ball.XPosition + this.labyrinth.BallSize, this.labyrinth.Ball.YPosition, this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize).Angle) {
                            this.movementVector.resolveCollision(this.new Vector2D(this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize, this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y - this.labyrinth.WallSize));
                            this.resolveMovement();

                        } else if
                            // Move ball to the next cell
                        } else {
                            this.moveBallToAxisY(this.startGrid.Y - this.labyrinth.WallSize);
                            this.resolveMovement();
                        }
                }

            // No collision
            } else {
                this.labyrinth.Ball.Position.X = this.movementVector.EndPosition.X;
                this.labyrinth.Ball.Position.Y = this.movementVector.EndPosition.Y;
            }
            */
            // Flags to check corner
            boolean _cornerLeft = false;
            boolean _cornerTop = false;
            boolean _cornerBottom = false;
            boolean _cornerRight = false;

            // Check left wall
            if(labyrinth.Ball.Position.X <= (startGrid.X + 0.30f)) {

                if(_cell.WayLeft == false) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedX);
                    labyrinth.Ball.SpeedX *= (-0.5);
                    labyrinth.Ball.Position.X = startGrid.X + 0.30f;

                } else {
                    _cornerLeft = true;
                }

                // Check right wall
            } else if(labyrinth.Ball.Position.X > startGrid.X + 0.70f) {

                if(_cell.WayRight == false) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedX);
                    labyrinth.Ball.SpeedX *= (-0.5);
                    labyrinth.Ball.Position.X = startGrid.X + 0.70f;
                }
            }

            // Check top wall
            if(labyrinth.Ball.Position.Y <= (startGrid.Y + 0.30f)) {

                if(_cell.WayUp == false) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedY);
                    labyrinth.Ball.SpeedY *= (-0.5);
                    labyrinth.Ball.Position.Y = startGrid.Y + 0.30f;
                }

                // Check bottom wall
            } else if(labyrinth.Ball.Position.Y >= (startGrid.Y + 0.70f)) {

                if(_cell.WayDown == false) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedY);
                    labyrinth.Ball.SpeedY *= (-0.5);
                    labyrinth.Ball.Position.Y = startGrid.Y + 0.70f;
                }
            }

            if(new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y).Equals(labyrinth.Key)) {
                labyrinth.NotifyKeyCollected();
                labyrinth.Ball.HasKey = true;
            } else if(new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y).Equals(labyrinth.End) && labyrinth.Ball.HasKey) {
                labyrinth.NotifyGameWon();
            }
        }

        private void setPosition() {


        }

        /**
         * Tests if movement vector is inside cell bounds
         * @return true, if vector is within square bounds completely, otherwise false
         */
        private static boolean checkWithinBounds() {
            Grid _grid = new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y);

            if(movementVector.EndPosition.X > _grid.X + 1 - labyrinth.WallSize - labyrinth.BallSize) {

                return false;
            }

            if(movementVector.EndPosition.X < _grid.X + labyrinth.WallSize + labyrinth.BallSize) {

                return false;
            }

            if(movementVector.EndPosition.Y > _grid.Y + 1 - labyrinth.WallSize - labyrinth.BallSize) {

                return false;
            }

            if(movementVector.EndPosition.Y < _grid.Y + labyrinth.WallSize + labyrinth.BallSize) {

                return false;
            }

            return true;
        }

        public boolean checkVectorBetweenPositions(Vector2D vectorParam, Position2D firstPositionParam, Position2D secondPositionParam) {
            Vector2D vectorToFirst = new Vector2D(vectorParam.StartPosition, firstPositionParam);
            Vector2D vectorToSecond = new Vector2D(vectorParam.StartPosition, secondPositionParam);

            if(vectorParam.Angle < vectorToFirst.Angle) {

                return false;
            }

            if(vectorParam.Angle > vectorToSecond.Angle) {

                return false;
            }

            return true;
        }

        /*
        private boolean IsInCorner() {
            // Überprüfe, ob die Kugel innerhalb des Nähebereichs zur Ecke liegt

            if (Math.abs(ballX - cornerX) <= proximityThreshold && Math.abs(ballY - cornerY) <= proximityThreshold) {
                return true; // Die Kugel ist in der Nähe der Ecke
            } else {
                return false; // Die Kugel ist nicht in der Nähe der Ecke
            }
        }

         */
    }
}

