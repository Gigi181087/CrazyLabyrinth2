package com.GP.labyrinth;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.GP.coordinates.Vector2D;
import com.GP.coordinates.Position2D;
import com.GP.coordinates.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LabyrinthModel {

    private final LabyrinthListener listener;

    /**
     * Event interface for labyrinth model
     */
    public interface LabyrinthListener {

        /**
         * Event triggered when the game is won
         */
        void onGameWon();

        /**
         * Event triggered when ball touches a wall
         * @param param speed of the ball on impact
         */
        void onWallTouched(float param);

        /**
         * Event triggered when the ball collects the key
         */
        void onKeyCollected();
    }

    final Context context;
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

    /**
     * Holds the different difficulty levels
     */
    public enum Difficulty {
        DEMO(9),
        EASY(0),
        MEDIUM(1),
        HARD(2);

        private final int intValue;

        /**
         * Constructor
         * @param valueParam integer representation of enum to be created
         */
        Difficulty(int valueParam) {
            this.intValue = valueParam;
        }


        /**
         * Gives integer representation of an enum value
         * @return integer representation
         */
        public int getValue() {

            return intValue;
        }

        /**
         * Gives string representation of an enum value
         * @return string representation
         */
        public String getString() {
            switch(intValue) {

                case 0:
                    return "EASY";
                case 1:
                    return "MEDIUM";
                case 2:
                    return "HARD";

                default:
                    return "DEMO";
            }
        }

        /**
         * Creates Enum from integer
         * @param intValue integer representation of enum
         * @return enum value according to given integer
         */
        public static Difficulty fromInt(int intValue) {
            for (Difficulty value : Difficulty.values()) {
                if (value.getValue() == intValue) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Invalid integer value for MyEnum: " + intValue);
        }
    }

    /**
     * Constructor
     * @param contextParam context
     */
    public LabyrinthModel(Context contextParam) {
        this.context = contextParam;
        this.listener = (LabyrinthListener) contextParam;
    }

    /**
     * Creates a labyrinth with given parameters
      * @param widthParam number of horizontal passages
     * @param heightParam number of vertical passages
     * @param difficultyParam use Difficulty.EASY, Difficulty.MEDIUM or Difficulty.HARD
     */
    public void create(int widthParam, int heightParam, Difficulty difficultyParam) {
        this.Width = widthParam;
        this.Height = heightParam;
        this.Level = difficultyParam;
        boolean _success = false;

        while(!_success) {
            _success =  LabyrinthCreator.Create(this);
        }
        this.Ball = new Ball((float) (this.Start.X + 0.5f), (float) (this.Start.Y + 0.5f));

        if(difficultyParam.getValue() > Difficulty.EASY.getValue()) {
            this.Ball.HasKey = false;
        }
    }

    /**
     * Updates the ball position in the labyrinth
     * @param xForceParam new gravity forces on x-axis
     * @param yForceParam new gravity forces on y-axis
     */
    public void updateBallPosition(float xForceParam, float yForceParam) {
        BallMover.updateBallPosition(this, xForceParam, yForceParam);
    }


    /*
     * LabyrinthModel Events
     */

    /**
     * Event is published, when the game is won
     */
    private void NotifyGameWon() {
        Log.d("LabyrinthModel", "Event GameWon triggered!");
        listener.onGameWon();
    }

    /**
     * Event is published when the ball touches any wall
     * @param speedParam speed of the ball when hitting the wall
     */
    private void NotifyWallTouched(float speedParam) {
        listener.onWallTouched(speedParam);
    }

    /**
     * Event is published when the ball collects the key on difficulty medium or hard
     */
    private void NotifyKeyCollected() {
        Log.d("LabyrinthModel", "Event KeyCollected triggered!");
        listener.onKeyCollected();
    }

    /**
     * Simple class which holds the configuration of a labyrinthcell
     */
    public class LabyrinthCell {
        public boolean WayUp;
        public boolean WayRight;
        public boolean WayLeft;
        public boolean WayDown;

    }


    /**
     * Static class responsible to create a randomized labyrinth with determined sizes
     */
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
        private static boolean Create(@NonNull LabyrinthModel labyrinthParam) {
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

                if(!NextStep()) {
                    StepBack();
                }

                if(cursor.Equals(labyrinth.End)) {
                    return false;
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

                    if(!NextStep()) {

                        if(_keyWayLength > 0) {
                            _keyWayLength--;
                        }
                        StepBack();

                    } else {
                        _keyWayLength++;
                    }

                    if(cursor.Equals(labyrinth.End)) {
                        return false;
                    }
                } while (_keyWayLength < labyrinthParam.Width * labyrinthParam.Height * 0.1);

                labyrinth.Key = new Grid(cursor);
                StepBack();
            }

            /*
             * Fill the rest of the empty cells
             */
            while(!cursor.Equals(labyrinthParam.End)) {

                if (!NextStep()) {
                    StepBack();
                }
            }

            // Clean up
            visited = null;
            cursor = null;
            steps = null;

            System.gc();

            return true;
        }

        /**
         * Moves the cursor to the next step if possible
         * @return true on success, otherwise false
         */
        private static boolean NextStep() {
            List<Integer[]> directions = new ArrayList<>();

            if(cursor.Y > 0) {

                if (!visited[cursor.X][cursor.Y - 1]) {
                    directions.add(new Integer[] {0, -1, 0});
                }

            }
            if(cursor.X < (labyrinth.Width - 1)) {

                if (!visited[cursor.X + 1][cursor.Y]) {
                    directions.add(new Integer[] {1, 0, 1});
                }

            }
            if(cursor.Y < (labyrinth.Height - 1)) {

                if(!visited[cursor.X][cursor.Y + 1]) {
                    directions.add(new Integer[] {0, 1, 2});
                }

            }
            if(cursor.X > 0) {

                if(!visited[cursor.X - 1][cursor.Y]) {
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

        /**
         * Moves the cursor to the previous cell
         */
        private static void StepBack() {
            cursor.Clone(steps.get(steps.size() - 1));
            steps.remove(steps.size() - 1);
        }
    }


    /**
     * Static class which is used to simulate ball movement in a labyrinth
     */
    private static class BallMover {
        private static LabyrinthModel labyrinth;
        private static Grid startGrid;
        private static Vector2D movementVector;

        /**
         * Simulates ball movement through labyrinth
         * @param labyrinthParam labyrinth on whcih the movement is performed
         * @param xForceParam new gravity forces on x-axis
         * @param yForceParam new gravity forces on y-axis
         */
        private static void updateBallPosition(@NonNull LabyrinthModel labyrinthParam, float xForceParam, float yForceParam) {
            labyrinth = labyrinthParam;

            long _timeNow = System.currentTimeMillis();
            float _timeElapsed = _timeNow - labyrinth.Ball.LastUpdated;

            // Create movement vector from accelerator forces
            startGrid = new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y);



            // calculate new position
            float _newX = (float)(labyrinth.Ball.Position.X + (labyrinth.Ball.SpeedX * 0.95f * _timeElapsed) / 1000);
            float _newY = (float)(labyrinth.Ball.Position.Y + (labyrinth.Ball.SpeedY * 0.95f * _timeElapsed) / 1000);
            movementVector = new Vector2D(labyrinth.Ball.Position, new Position2D(_newX, _newY));

            // calculate new speed
            labyrinth.Ball.SpeedX += ((labyrinth.Ball.ForceX * _timeElapsed) / 1000);
            labyrinth.Ball.SpeedY += ((labyrinth.Ball.ForceY * _timeElapsed) / 1000);
            labyrinth.Ball.ForceX = xForceParam;
            labyrinth.Ball.ForceY = yForceParam;
            labyrinth.Ball.LastUpdated = _timeNow;

            resolveMovement();
        }

        /**
         * Resolves collisions with wall during simulation
         */
        private static void resolveMovement() {
            startGrid = new Grid((int)movementVector.StartPosition.X, (int)movementVector.StartPosition.Y);
            LabyrinthCell _cell = labyrinth.Cells[(int)labyrinth.Ball.Position.X][(int)labyrinth.Ball.Position.Y];
            labyrinth.Ball.Position = movementVector.EndPosition;


            // Check left wall
            if(labyrinth.Ball.Position.X <= (startGrid.X + 0.30f)) {

                if(!_cell.WayLeft) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedX);
                    labyrinth.Ball.SpeedX *= (-0.5f);
                    labyrinth.Ball.Position.X = startGrid.X + 0.30f;

                }

                // Check right wall
            } else if(labyrinth.Ball.Position.X > startGrid.X + 0.70f) {

                if(!_cell.WayRight) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedX);
                    labyrinth.Ball.SpeedX *= -0.5f;
                    labyrinth.Ball.Position.X = startGrid.X + 0.70f;
                }
            }

            // Check top wall
            if(labyrinth.Ball.Position.Y <= (startGrid.Y + labyrinth.WallSize + labyrinth.BallSize)) {

                if(!_cell.WayUp) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedY);
                    labyrinth.Ball.SpeedY *= (-0.5f);
                    labyrinth.Ball.Position.Y = startGrid.Y + 0.30f;
                }

                // Check bottom wall
            } else if(labyrinth.Ball.Position.Y >= (startGrid.Y + 0.70f)) {

                if(!_cell.WayDown) {
                    labyrinth.NotifyWallTouched(labyrinth.Ball.SpeedY);
                    labyrinth.Ball.SpeedY *= (-0.5f);
                    labyrinth.Ball.Position.Y = startGrid.Y + 0.70f;
                }
            }

            if(new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y).Equals(labyrinth.Key) && !labyrinth.Ball.HasKey) {
                labyrinth.NotifyKeyCollected();
                labyrinth.Ball.HasKey = true;
            } else if(new Grid((int)labyrinth.Ball.Position.X, (int)labyrinth.Ball.Position.Y).Equals(labyrinth.End) && labyrinth.Ball.HasKey) {
                labyrinth.NotifyGameWon();
            }
        }
    }
}

