/**
 * Class:       Pedestrian
 * Purpose:     Navigate grid squares
 * Author:      Michael LaFleur
 * Email:       mlafleur@iu.edu
 * Assignment:  Project 2
 * Course:      Information Infrastructure II (INFO-I 211)
 * Professor:   Dr. Yang Liu
 * Semester:    Spring 2023
 *
 */

package com.example.javafx;

import javafx.scene.paint.Color;
import java.util.ArrayList;

public class Pedestrian {
    /**
     * FIELDS
     */
    final int COL_NEAR_PATH = 37;
    private boolean adventurer;
    private ArrayList<String> alCellsVisited = new ArrayList<>();
    private int currentCol = 0;
    private String currentRow = "";
    private int endCol = 0;
    private String endRow = "";
    private String gridPathEnd = "";
    private String gridPathStart = "";
    private String location = "";
    private int maxTerrain = 2;
    private int numTimesBlocked = 0;
    private int startCol = 0;
    private String startRow = "";
    private int steps = 0;
    private boolean trespasser;
    private boolean xPrefOverY = true;

    /**
     * Count pedestrians (number of pedestrian objects created)
     */
    static int numPedestrians = 0;

    {
        numPedestrians += 1;
    }

    /**
     * CONSTRUCTORS
     */
    public Pedestrian() {
        // 14% to 78% of pedestrians are adventurous walkers (78 - ((78 - 14) / 2) == 46):
        // https://www.cam.ac.uk/research/news/pedestrians-choose-healthy-obstacles-over-boring-pavements-study-finds
        this.adventurer = (Math.random() < 0.46);

        // 40% of pedestrians take shortcuts (source? from research review)
        this.trespasser = (Math.random() < 0.40);

        this.maxTerrain = (this.getAdventurer()) ? 3 : 2;   // Adventurers will travel over more rugged terrain
        this.gridPathStart = this.setOrigin();
        this.gridPathEnd = this.setDestination();

        // Move pedestrian to starting location (moveTo and calculateNextMove methods call each other at conclusion)
        moveTo(Main.getGridSquareObject(this.getGridPathStart()));
    }

    /**
     * ACCESSORS
     */
    public boolean getAdventurer() { return adventurer; }
    public int getCurrentCol() { return currentCol; }
    public String getCurrentRow() { return currentRow; }
    public int getEndCol() { return endCol; }
    public String getEndRow() { return endRow; }
    public String getGridPathEnd() { return gridPathEnd; }
    public String getGridPathStart() { return gridPathStart; }
    public String getLocation() { return location; }
    public int getMaxTerrain() { return maxTerrain; }
    public int getStartCol() { return startCol; }
    public String getStartRow() { return startRow; }
    public int getSteps() { return steps; }
    public boolean getTrespasser() { return trespasser; }

    /**
     * MUTATORS
     */
    public void setAdventurer(boolean adventurer) { this.adventurer = adventurer; }
    public void setCurrentCol(int col) { this.currentCol = col; }
    public void setCurrentRow(String row) { this.currentRow = row; }
    public void setEndCol(int col) { this.endCol = col; }
    public void setEndRow(String row) { this.endRow = row; }
    public void setGridPathEnd(String destination) { this.gridPathEnd = destination; }
    public void setLocation(String loc) { this.location = loc; }
    public void setMaxTerrain(int terrain) { this.maxTerrain = terrain; }
    public void setStartCol(int col) { this.startCol = col; }
    public void setStartRow(String row) { this.startRow = row; }
    public void setSteps(int steps) { this.steps = steps; }
    public void setTrespasser(boolean trespasser) { this.trespasser = trespasser; }

    public String setOrigin() {
        // Origin should only be set one time
        if(this.getGridPathStart().equals("")) {
            // Select random row from R to W
            String[] rows = {"R", "S", "T", "U", "V", "W"};
            int index = (int) (Math.random() * rows.length);
            this.setStartRow(rows[index]);

            // Select random column from 1 to 58
            int randCol = (int) (Math.random() * 58 + 1);
            this.setStartCol(randCol);

            // Return starting row and column (string)
            return rows[index] + Integer.toString(randCol);
        }

        // Else return existing value
        else return this.getGridPathStart();
    }

    public String setDestination() {
        // Pedestrians who reach milestone R12 will update destination to C22
        if(this.getGridPathEnd().equals("R12")) {
            xPrefOverY = false;
            this.setGridPathEnd("C22");
            this.setEndRow("C");
            this.setEndCol(22);
            return "C22";
        }

        // Pedestrians who reach milestone F38 will update destination to A33
        if(this.getGridPathEnd().equals("F38")) {
            xPrefOverY = false;
            this.setGridPathEnd("A33");
            this.setEndRow("A");
            this.setEndCol(33);
            return "A33";
        }

        // Pedestrians who have not set destination yet will be assigned milestones (R12 or F38)
        if(this.getGridPathEnd().equals("")) {
            // If pedestrian is near official path, milestone is R12
            if (this.getStartCol() <= COL_NEAR_PATH) {
                this.setGridPathEnd("R12");
                this.setEndRow("R");
                this.setEndCol(12);
                return "R12";
            }

            // Else if pedestrian is not near official path...
            else {
                // If pedestrian has adventurer and trespasser personality, milestone is F38
                if(this.getAdventurer() && this.getTrespasser()) {
                    // This pedestrian will move (forward) on y-axis first
                    xPrefOverY = false;
                    this.setGridPathEnd("F38");
                    this.setEndRow("F");
                    this.setEndCol(38);
                    return "F38";
                }

                // Else compliant pedestrian will follow official path even when further away (milestone R12)
                else {
                    this.setGridPathEnd("R12");
                    this.setEndRow("R");
                    this.setEndCol(12);
                    return "R12";
                }
            }
        }

        // Pedestrians who already have a set destination will return that value
        else return this.getGridPathEnd();
    }

    public String toString() {
        String myString = "";
        myString += "PEDESTRIAN " + numPedestrians + "\n\n";
        myString += "Adventurer: " + this.getAdventurer() + "\n";
        myString += "Trespasser: " + this.getTrespasser() + "\n";
        myString += "Max Terrain: " + this.getMaxTerrain() + "\n\n";
        myString += "Origin: " + this.getGridPathStart() + "\n";
        myString += "Destination: " + this.getGridPathEnd() + "\n\n";
        myString += "Location: " + this.getLocation() + "\n";
        myString += "Steps: " + this.getSteps() + "\n\n";

        return myString;
    }

    /**
     * Method:  moveTo
     * Purpose: Move pedestrian to GridSquare (moveTo calls calculateNextMove at conclusion)
     * @param   gs the GridSquare object
     *
     */
    public void moveTo(GridSquare gs) {
        // Set pedestrian's current location
        String loc = gs.getLocation();
        this.setLocation(loc);
        this.setCurrentRow(loc.substring(0,1));
        this.setCurrentCol(Integer.parseInt(loc.substring(1)));

        // If non-trespassing pedestrian reached milestone R12, update destination to C22
        if(loc.equals("R12")) {
            String newDest = this.setDestination();
        }

        // If trespassing pedestrian reached milestone F38, update destination to A33
        if(loc.equals("F38")) {
            String newDest = this.setDestination();
        }

        // If pedestrian has never visited this GridSquare...
        if(!alCellsVisited.contains(loc)) {
            // Add GridSquare coords to list of cells visited
            alCellsVisited.add(loc);
        }

        // Increment GridSquare's number of visits
        gs.setVisits(gs.getVisits() + 1);

        // Erode GridSquare's terrain (ruggedness decreases by 1 every 5 visits)
        gs.erodeTerrain();

        // Display GridSquare's information
        Main.displayCellInfo(gs, true);

        // Set GridSquare's fill color and opacity
        if(this.getSteps() == 0) {
            gs.setOrigin(true);             // Origin square status
            gs.setFill(Color.GOLD);         // Origin square color
            gs.setOpacity(1.0);
        }

        if(this.getLocation().equals("C22") || this.getLocation().equals("A33")) {
            gs.setFill(Color.LIMEGREEN);    // Destination square color
            gs.setOpacity(1.0);
        }

        else {
            if(!gs.isOrigin()) {            // Don't fill over origin squares
                gs.setFill(Color.WHITE);    // Path squares
                gs.setOpacity(0.7);
            }
        }

        // Calculate pedestrian's next move
        calculateNextMove();
    }

    /**
     * Method:  calculateNextMove
     * Purpose: Decide which GridSquare pedestrian will move to next (calculateNextMove calls moveTo at conclusion)
     *
     */
    public void calculateNextMove() {
        // Determine direction preferences (0,0 is top left corner in JavaFX)
        // Pedestrian only moves in 4 directions: left, right, forward, back
        boolean allowMove = true;
        int currCol = this.getCurrentCol();
        int destCol = this.getEndCol();

        String strCurrRow = this.getCurrentRow();
        String strDestRow = this.getEndRow();

        int intCurrRow = (int)(strCurrRow.charAt(0));
        int intDestRow = (int)(strDestRow.charAt(0));

        GridSquare gsNext = new GridSquare();

        int xDirPref = 0;   // -1: left,    0: none, 1: right
        int yDirPref = 0;   // -1: forward, 0: none, 1: back

        // Is pedestrian right, left, or aligned with destination? (set xDirPref)
        if(currCol > destCol) xDirPref = -1;
        else if(currCol < destCol) xDirPref = 1;
        else xDirPref = 0;

        // Is pedestrian back, forward, or aligned with destination? (set yDirPref)
        if(intCurrRow > intDestRow) yDirPref = -1;
        else if(intCurrRow < intDestRow) yDirPref = 1;
        else yDirPref = 0;

        // Toggle preferred direction if target row or column was reached
        if(xPrefOverY && (xDirPref == 0)) xPrefOverY = false;
        if(!xPrefOverY && (yDirPref == 0)) xPrefOverY = true;

        // If not at destination (xDirPref and yDirPref are both zero at destination)...
        if(!(xDirPref == 0 && yDirPref == 0)) {
            // If recalculating from a blocked path, toggle preferred direction
            if(numTimesBlocked == 1) {
                if(xPrefOverY) xPrefOverY = false;
                else xPrefOverY = true;
            }

            // If both x and y directions are blocked...
            if(numTimesBlocked == 2) {
                // Do special handling here to get unstuck (currently unnecessary due to milestones and map layout)
            }

            // Else not blocked... check preferred direction (x) for boundaries and obstacles
            else {
                if (xPrefOverY) {
                    // If preferred x direction is left...
                    if (xDirPref == -1) {
                        // If at left boundary, forbid move
                        if (currCol == 1) {
                            allowMove = false;
                        }

                        // Else not at left boundary (check left GridSquare)
                        else {
                            int intGsNext = currCol - 1;
                            String strGsNext = strCurrRow + Integer.toString(intGsNext);
                            gsNext = Main.getGridSquareObject(strGsNext);

                            // If left GridSquare is too steep, forbid move
                            if (gsNext.getElevation() > 3) allowMove = false;

                            // If left GridSquare is too rugged, forbid move
                            if (gsNext.getTerrain() > this.getMaxTerrain()) allowMove = false;
                        }
                    }

                    // Else preferred x direction is right...
                    else {
                        // If at right boundary, forbid move
                        if (currCol == 58) {
                            allowMove = false;
                        }

                        // Else not at right boundary (check right GridSquare)
                        else {
                            int intGsNext = currCol + 1;
                            String strGsNext = strCurrRow + Integer.toString(intGsNext);
                            gsNext = Main.getGridSquareObject(strGsNext);

                            // If right GridSquare is too steep, forbid move
                            if (gsNext.getElevation() > 3) allowMove = false;

                            // If right GridSquare is too rugged, forbid move
                            if (gsNext.getTerrain() > this.getMaxTerrain()) allowMove = false;
                        }
                    }
                }

                // Else check preferred direction (y) for boundaries and obstacles
                else {
                    // If preferred y direction is forward...
                    if (yDirPref == -1) {
                        // If at forward boundary, forbid move
                        if (strCurrRow.equals("A")) {
                            allowMove = false;
                        }

                        // Else not at forward boundary (check forward GridSquare)
                        else {
                            int intGsNext = currCol;
                            char charRowHere = strCurrRow.charAt(0);
                            int intRowNext = (int)charRowHere - 1;
                            char charRowNext = (char)intRowNext;
                            String strRowNext = Character.toString(charRowNext);
                            String strGsNext = strRowNext + Integer.toString(intGsNext);
                            gsNext = Main.getGridSquareObject(strGsNext);

                            // If forward GridSquare is too steep, forbid move
                            if (gsNext.getElevation() > 3) allowMove = false;

                            // If forward GridSquare is too rugged, forbid move
                            if (gsNext.getTerrain() > this.getMaxTerrain()) allowMove = false;
                        }
                    }

                    else {
                        // If at backward boundary, forbid move
                        if (strCurrRow.equals("Z")) {
                            allowMove = false;
                        }

                        // Else not at backward boundary (check backward GridSquare)
                        else {
                            int intGsNext = currCol;
                            char charRowHere = strCurrRow.charAt(0);
                            int intRowNext = (int)charRowHere + 1;
                            char charRowNext = (char)intRowNext;
                            String strRowNext = Character.toString(charRowNext);
                            String strGsNext = strRowNext + Integer.toString(intGsNext);
                            gsNext = Main.getGridSquareObject(strGsNext);

                            // If backward GridSquare is too steep, forbid move
                            if (gsNext.getElevation() > 3) allowMove = false;

                            // If backward GridSquare is too rugged, forbid move
                            if (gsNext.getTerrain() > this.getMaxTerrain()) allowMove = false;
                        }
                    }
                }

                // If movement in preferred direction is forbidden...
                if (!allowMove) {
                    // Increment number of times blocked
                    numTimesBlocked++;

                    // RECURSIVE CALL! (but in last block of method and conditions are in place to end recursion)
                    calculateNextMove();
                }

                // Else movement in preferred direction is allowed...
                else {
                    // Reset number of times blocked
                    numTimesBlocked = 0;

                    // Increment number of steps taken by pedestrian
                    this.setSteps(this.getSteps() + 1);

                    // Move to next GridSquare
                    moveTo(gsNext);
                }
            }
        }
    } // End calculateNextMove()
}
