/**
 * Class:       GridSquare
 * Purpose:     Rectangle subclass
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
import javafx.scene.shape.Rectangle;

public class GridSquare extends Rectangle {
    /**
     * FIELDS
     */
    private double elevation = 0.0;
    private boolean isOrigin = false;
    private String location = "";
    private Color originalFill = new Color(0.0, 0.0, 0.0, 0.0);
    private double originalOpacity = 1.0;
    private Color originalStroke = new Color(0.0, 0.0, 0.0, 0.0);
    private int terrain = 0;
    private int visits = 0;

    /**
     * CONSTRUCTORS
     */
    public GridSquare() {
        super();
    }

    /**
     * ACCESSORS
     */
    public double getElevation() { return elevation; }
    public String getLocation() { return location; }
    public Color getOriginalFill() { return originalFill; }
    public double getOriginalOpacity() { return originalOpacity; }
    public Color getOriginalStroke() { return originalStroke; }
    public int getTerrain() { return terrain; }
    public int getVisits() { return visits; }
    public boolean isOrigin() { return isOrigin; }

    public String getTerrainDesc(int terrain) {
        return switch(terrain) {
            case 0 -> " (Paved Access)";
            case 1 -> " (Grass, Gravel)";
            case 2 -> " (Dirt, Sand)";
            case 3 -> " (Brush, Mud)";
            case 4 -> " (Rock, Forest)";
            case 5 -> " (Water, Swamp)";
            default -> " (Unknown)";
        };
    }

    /**
     * MUTATORS
     */
    public void setElevation(double elevation) { this.elevation = elevation; }
    public void setLocation(String location) { this.location = location; }
    public void setOrigin(boolean b) { this.isOrigin = b; }
    public void setOriginalFill(Color fill) { this.originalFill = fill; }
    public void setOriginalOpacity(double opacity) { this.originalOpacity = opacity; }
    public void setOriginalStroke(Color stroke) { this.originalStroke = stroke; }

    public void setTerrain(int terrain) {
        if(terrain < 0) terrain = 0;
        if(terrain > 5) terrain = 5;
        this.terrain = terrain;
    }

    public void setVisits(int visits) {
        if(visits < 0) visits = 0;
        this.visits = visits;
    }

    public String toString() { return this.location; }

    /**
     * Method:  erodeTerrain
     * Purpose: Simulate erosion (every 5 visits reduces terrain score by 1)
     *
     */
    public void erodeTerrain() {
        // Frequent visits will lower terrain score (0, 5, 10, 15)
        // Trail becomes visible in 6-15 visits per research review
        int terrain = this.getTerrain();
        int visits = this.getVisits();

        if(visits % 5 == 0) {
            terrain--;
            if(terrain < 0) terrain = 0;
            this.setTerrain(terrain);
            // Optional: update visual (fill, opacity, stroke) for GridSquare here to simulate erosion
        }
    }
}
