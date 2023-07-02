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

        // Created by ChatGPT and unchanged



        private static LabyrinthModel labyrinth;
        private static Grid startGrid;

        private static Vector2D movementVector;
        public void UpdateBallPosition(LabyrinthModel labyrinthParam, float xForceParam, float yForceParam) {
            this.labyrinth = labyrinthParam;


            // Create movement vector from accelerator forces
            startGrid = new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y);

            long _timeNow = System.currentTimeMillis();
            float _timeElapsed = _timeNow - labyrinth.Ball.LastUpdated;

            // calculate new position
            float _newX = (float)(this.labyrinth.Ball.Position.X + (labyrinth.Ball.SpeedX * 0.8 * _timeElapsed) / 1000);
            float _newY = (float)(this.labyrinth.Ball.Position.Y + (labyrinth.Ball.SpeedY * 0.8 * _timeElapsed) / 1000);

            // calculate new speed
            labyrinth.Ball.SpeedX += ((labyrinth.Ball.ForceX * _timeElapsed) / 1000);
            labyrinth.Ball.SpeedY += ((labyrinth.Ball.ForceY * _timeElapsed) / 1000);

            labyrinth.Ball.LastUpdated = _timeNow;

            movementVector = new Vector2D(this.labyrinth.Ball.Position, new Position2D(_newX, _newY));

            /*
             * Check if vector collides with any wall
             */

            // Check top wall
            this.resolveMovement();
        }

        private void resolveMovement() {
            this.startGrid = new Grid((int)this.movementVector.StartPosition.X, (int)this.movementVector.StartPosition.Y);

            // Collision
            if(!this.checkWithinBounds()) {
                LabyrinthCell _cell = labyrinth.Cells[(int)labyrinth.Ball.Position.X][(int)labyrinth.Ball.Position.X];

                // Create position of the ball edges
                Position2D topEdge = this.labyrinth.Ball.Position.add(0, -this.labyrinth.BallSize);
                Position2D rightEdge = this.labyrinth.Ball.Position.add(labyrinth.BallSize, 0);
                Position2D bottomEdge = this.labyrinth.Ball.Position.add(0, labyrinth.BallSize);
                Position2D leftEdge = this.labyrinth.Ball.Position.add(labyrinth.BallSize, 0);

                // Create position of the corners
                Position2D topRightCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(1 - this.labyrinth.WallSize, this.labyrinth.WallSize);
                Position2D _bottomRightCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(1 - this.labyrinth.WallSize, 1 - this.labyrinth.WallSize);
                Position2D bottomLeftCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(this.labyrinth.WallSize, 1 - this.labyrinth.WallSize);
                Position2D topLeftCorner = new Position2D((float)startGrid.X, (float)startGrid.Y).add(this.labyrinth.WallSize, this.labyrinth.WallSize);

                // Check top right corner
                if(checkVectorBetweenPositions(movementVector, new Position2D(startGrid.X, startGrid.Y).add(0.5f, 0), topRightCorner)) {



                    if(!_cell.WayUp) {  // Case there is a wall
                        this.movementVector.resolveCollision(this.new Vector2D(this.labyrinth.Ball.XPosition, this.startGrid.Y + this.labyrinth.WallSize,this.startGrid.X + 1 - this.labyrinth.WallSize, this.startGrid.Y + this.labyrinth.WallSize));
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
                this.labyrinth.Ball.XPosition = this.movementVector.endX;
                this.labyrinth.Ball.YPosition = this.movementVector.endY;
            }

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

        private void setPosition() {


        }

        /**
         * Tests if movement vector is inside cell bounds
         * @return true, if vector is within square bounds completely, otherwise false
         */
        private boolean checkWithinBounds() {
            Grid _grid = this.labyrinth.new Grid((int)this.labyrinth.Ball.XPosition, (int)this.labyrinth.Ball.YPosition);

            if(this.movementVector.endX > _grid.X + 1 - this.labyrinth.WallSize - this.labyrinth.BallSize) {

                return false;
            }

            if(this.movementVector.endX < _grid.X + this.labyrinth.WallSize + this.labyrinth.BallSize) {

                return false;
            }

            if(this.movementVector.endY > _grid.Y + 1 - this.labyrinth.WallSize - this.labyrinth.BallSize) {

                return false;
            }

            if(this.movementVector.endY < _grid.Y + this.labyrinth.WallSize + this.labyrinth.BallSize) {

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

        private boolean checkWithinCorridor(Vector2D vectorParam, Position LeftParam, Position fightParam, Position backLeftParam, Position backRightParam) {

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

