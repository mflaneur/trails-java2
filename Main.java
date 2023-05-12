/**
 * Class:           Main
 * Purpose:         JavaFX End-of-Semester Project
 * Author:          Michael LaFleur
 * Email:           mlafleur@iu.edu
 * Last Modified:   April 29, 2023
 * Assignment:      Project 2
 * Course:          Information Infrastructure II (INFO-I 211)
 * Professor:       Dr. Yang Liu
 * Semester:        Spring 2023
 * Date Submitted:  April 29, 2023
 *
 */

package com.example.javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.HashMap;

public class Main extends Application
{
    /**
     * GRID GLOBALS:
     * - The entire width of the map is used (CELL_COLUMNS * CELL_WIDTH == MAP_WIDTH)
     * - Only the bottom portion of the map is used (MAP_HEIGHT - (CELL_ROWS * CELL_HEIGHT) == ROW_START_Y)
     * - Grid squares (cells) are JavaFX rectangles size 18 x 18
     * - MAP_WIDTH and MAP_HEIGHT were cropped in Photoshop to be evenly divisible by 18 (matches PS grid)
     *
     * ALGORITHM:
     * - Grid squares along entire perimeter are reserved for alphanumeric row-column "labeling" (text)
     * - Remaining grid squares have events (Ctrl-F search: "GRID SQUARES FOR MAP ANALYSIS START HERE")
     */
    final int ASCII_CAP_A = 65;
    final int CELL_COLUMNS = 60;
    final int CELL_HEIGHT = 18;
    final int CELL_ROWS = 28;
    final int CELL_WIDTH = 18;
    final int MAP_HEIGHT = 792;
    final int MAP_WIDTH = 1080;
    final int ROW_START_X = 0;
    final int ROW_START_Y = 288;

    private static HashMap<String, GridSquare> hmGridCells = new HashMap<String, GridSquare>();
    private static Label lblCellElevation = new Label("");
    private static Label lblCellLocation = new Label("");
    private static Label lblCellTerrain = new Label("");
    private static Label lblCellTerrainDesc = new Label("");
    private static Label lblCellVisits = new Label("");

    Text pText = new Text();

    public static void main(String[] args)
    {
        // Launch the application.
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        /**
         * FIELDS
         */
        char capLetter = 'A';       // Temp alpha placeholder
        double digitOffset = 0.0;   // Temp nudge for double digits
        String strLabel = "";       // Temp alphanumeric placeholder

        /**
         * MAP BOX STARTS HERE
         */
        // Map screenshot source (Google Earth Pro Desktop): https://www.google.com/earth/versions/#earth-pro
        Image image = new Image("file:src/main/java/com/example/JavaFX/indiana-dunes-social-trails.png");

        // Set image view
        ImageView imageView = new ImageView(image);

        // Set image position
        imageView.setX(0);
        imageView.setY(0);

        // Set image view fit height and width
        imageView.setFitHeight(MAP_HEIGHT);
        imageView.setFitWidth(MAP_WIDTH);

        // Create pane and add image view
        Pane pane = new Pane();
        pane.getChildren().addAll(imageView);

        // Create grid
        for(int i = 0; i < CELL_COLUMNS; i++) {
            for(int j = 0; j < CELL_ROWS; j++) {
                GridSquare gs = new GridSquare();

                double recX = ROW_START_X + (i * CELL_WIDTH);
                double recY = ROW_START_Y + (j * CELL_HEIGHT);

                gs.setX(recX);
                gs.setY(recY);

                gs.setWidth(CELL_WIDTH);
                gs.setHeight(CELL_HEIGHT);

                // Grid lines color
                gs.setStroke(Color.BLACK);
                gs.setStrokeWidth(0.1);

                // Squares for column grid labels
                if(i == 0 || i == (CELL_COLUMNS - 1)) {
                    gs.setFill(Color.LIGHTGRAY);

                    // The four corner squares are black
                    if(j == 0 || j == (CELL_ROWS - 1)) gs.setFill(Color.BLACK);

                    else {
                        // Label rows with capital letters
                        int capValue = (j - 1) + ASCII_CAP_A;
                        capLetter = (char) capValue;
                        strLabel = Character.toString(capLetter);
                    }
                }

                // Squares for row grid labels
                else if(j == 0 || j == (CELL_ROWS - 1)) {
                    // Label columns with numbers
                    gs.setFill(Color.LIGHTGRAY);
                    strLabel = Integer.toString(i);
                    if(i > 9) digitOffset = 3.0; // Double digits need re-centered
                }

                /**
                 * GRID SQUARES FOR MAP ANALYSIS START HERE
                 */

                else {
                    // Put rectangles in HashMap for later access by key
                    int capValue = (j - 1) + ASCII_CAP_A;
                    capLetter = (char) capValue;
                    strLabel = Character.toString(capLetter);
                    String strCellNumber = Integer.toString(i);
                    String strCellName = strLabel + strCellNumber;
                    hmGridCells.put(strCellName, gs);
                    gs.setLocation(strCellName);

                    gs.setFill(Color.TRANSPARENT);   // No base color for grid cells
                    strLabel = "";                  // No visible label for grid cells

                    // Set rectangle events
                    gs.setOnMouseEntered(event -> {
                        // Display cell info (boolean: visible)
                        displayCellInfo(gs, true);

                        // Colors: remember original
                        colorsRemember(gs, (Color) gs.getStroke(), (Color) gs.getFill(), gs.getOpacity());

                        // Colors: highlight effect
                        gs.setStroke(Color.RED);    // Cursor grid
                        gs.setFill(Color.RED);      // Cursor fill
                        gs.setOpacity(0.5);         // Cursor opacity
                    });

                    gs.setOnMouseExited(mouseEvent -> {
                        // Display cell info (boolean: hidden)
                        displayCellInfo(gs, false);

                        // Colors: restore original
                        colorsRestore(gs);
                    });
                }

                /**
                 * GRID SQUARES FOR MAP ANALYSIS END HERE
                 */

                // Create text "label"
                Text txtLabel = new Text(strLabel);
                double txtX = recX + 5.0 - digitOffset; // X offset by visual adjustment
                double txtY = recY + 13.0;              // Y offset by visual adjustment

                // Thin letters need nudged to right (so letters appear center aligned)
                char[] thinLetters = {'I', 'J'};
                if(new String(thinLetters).indexOf(capLetter) > -1)  {
                    txtX += 2.0;
                }

                // Wide letters need nudged to left (so letters appear center aligned)
                char[] wideLetters = {'M', 'Q', 'W'};
                if(new String(wideLetters).indexOf(capLetter) > -1)  {
                    txtX -= 2.0;
                }

                txtLabel.setX(txtX);
                txtLabel.setY(txtY);

                // Add rectangle and txtLabel to pane
                pane.getChildren().addAll(gs, txtLabel);
            } // End nested for loop (j)
        } // End for loop (i)

        // Create image VBox
        VBox vboxImage = new VBox(pane);

        /**
         * INFO BOX STARTS HERE
         */

        // Create labels
        Label infoElevation = new Label("Elevation: ");
        Label infoLocation = new Label("Grid Location:");
        Label infoTerrain = new Label("Terrain: ");
        Label infoVisits = new Label("Visits: ");
        Label spacer = new Label(" ");

        // Add style for info labels
        infoElevation.getStyleClass().add("info");
        infoTerrain.getStyleClass().add("info");
        infoVisits.getStyleClass().add("info");

        // Add style for txtCellName
        lblCellLocation.getStyleClass().add("cell");

        // Create HBoxes
        HBox hboxSpacer = new HBox(spacer);
        HBox hboxElevation = new HBox(infoElevation, lblCellElevation);
        HBox hboxTerrain = new HBox(infoTerrain, lblCellTerrain, lblCellTerrainDesc);
        HBox hboxVisits = new HBox(infoVisits, lblCellVisits);

        // Create bundle VBox
        VBox vboxBundle = new VBox(hboxSpacer, hboxElevation, hboxTerrain, hboxVisits);
        vboxBundle.setAlignment(Pos.CENTER_LEFT);

        // Create button for pedestrian
        Button buttonPedestrian = new Button("Add Pedestrian");
        VBox vboxButton = new VBox(pText, buttonPedestrian);
        vboxButton.setAlignment(Pos.CENTER_LEFT);
        vboxButton.setPadding(new Insets(50, 0, 50, 0));

        // Button event
        buttonPedestrian.setOnAction(e-> {
            Pedestrian p = new Pedestrian();
            pText.setText(p.toString());
        });

        // Create info VBox
        VBox vboxInfo = new VBox(infoLocation, lblCellLocation, vboxBundle, vboxButton);
        vboxInfo.setMinHeight(792);
        vboxInfo.setMinWidth(300);
        vboxInfo.setPadding(new Insets(50, 50, 50, 50));
        vboxInfo.setAlignment(Pos.CENTER_LEFT);

        /**
         * SCENE ASSEMBLY STARTS HERE
         */

        // Call initialization methods
        initializeGridAttributes();

        // Create primary hbox (horizontal so map box and info box are same height / side by side)
        HBox hboxPrimary = new HBox(vboxImage, vboxInfo);

        // Set hbox alignment to center
        hboxPrimary.setAlignment(Pos.TOP_LEFT);

        // Set hbox padding to 0 pixels
        hboxPrimary.setPadding(new Insets(0));

        // Create scene
        Scene scene = new Scene(hboxPrimary, 1380, 792);

        // Link style sheet
        scene.getStylesheets().add("file:src/main/java/com/example/JavaFX/Main.css");

        // Add scene to stage
        primaryStage.setScene(scene);

        // Set stage title
        primaryStage.setTitle("Desire Path Prediction and Generation Modeled in JavaFX");

        // Disable resizing of window
        primaryStage.setResizable(false);

        // Show window
        primaryStage.show();

        // Activate first pedestrian (button)
        buttonPedestrian.fire();
    }

    /**
     * Method:  colorsRemember
     * Purpose: Remember GridSquare's original fill, opacity, and stroke
     * @param   gs the GridSquare object
     * @param   stroke color
     * @param   fill color
     * @param   opacity double
     *
     */
    public void colorsRemember(GridSquare gs, Color stroke, Color fill, double opacity) {
        gs.setOriginalStroke(stroke);
        gs.setOriginalFill(fill);
        gs.setOriginalOpacity(opacity);
    }

    /**
     * Method:  colorsRestore
     * Purpose: Restore GridSquare's original fill, opacity, and stroke
     * @param   gs the GridSquare object
     *
     */
    public void colorsRestore(GridSquare gs) {
        gs.setStroke((Paint) gs.getOriginalStroke());
        gs.setFill((Paint) gs.getOriginalFill());
        gs.setOpacity(gs.getOriginalOpacity());
    }

    /**
     * Method:  displayCellInfo
     * Purpose: Display (or hide if show == false) information about GridSquare in side column
     * @param   gs the GridSquare object
     * @param   show visible or hidden status
     *
     */
    public static void displayCellInfo(GridSquare gs, boolean show) {
        String elevation = show ? Double.toString(gs.getElevation()) : "";
        String location = show ? gs.toString() : "";
        String terrain = show ? Integer.toString(gs.getTerrain()) : "";
        String terrainDesc = show ? gs.getTerrainDesc(gs.getTerrain()) : "";
        String visits = show ? Integer.toString(gs.getVisits()) : "";

        lblCellElevation.setText(elevation);
        lblCellLocation.setText(location);
        lblCellTerrain.setText(terrain);
        lblCellTerrainDesc.setText(terrainDesc);
        lblCellVisits.setText(visits);
    }

    /**
     * Method:  initializeGridAttributes
     * Purpose: Sets terrain, elevation, and color attributes (fill, opacity, stroke) of each GridSquare on map
     *
     */
    public void initializeGridAttributes() {
        final int ALPHA_A = 1;
        final int ALPHA_E = 5;
        final int ALPHA_L = 12;
        final int ALPHA_M = 13;
        final int ALPHA_W = 23;
        final int ALPHA_X = 24;
        final int ALPHA_Z = 26;

        // GREEN layer: A1 through L58
        for(int i = 1; i <= 58; i++) {
            for(int j = ALPHA_A; j <= ALPHA_L; j++) {
                int iLetter = ASCII_CAP_A + (j - 1);
                char cLetter = (char) iLetter;
                String sLetter = Character.toString(cLetter);

                String coord = sLetter + Integer.toString(i);
                GridSquare gs = hmGridCells.get(coord);
                gs.setStroke(Color.BLACK);
                gs.setFill(Color.GREEN);
                gs.setOpacity(0.4);
                gs.setTerrain(3);
                gs.setElevation(2.0);
            }
        }

        // SANDYBROWN layer: M1 through W58
        for(int i = 1; i <= 58; i++) {
            for(int j = ALPHA_M; j <= ALPHA_W; j++) {
                int iLetter = ASCII_CAP_A + (j - 1);
                char cLetter = (char) iLetter;
                String sLetter = Character.toString(cLetter);

                String coord = sLetter + Integer.toString(i);
                GridSquare gs = hmGridCells.get(coord);
                gs.setStroke(Color.BLACK);
                gs.setFill(Color.SANDYBROWN);
                gs.setOpacity(0.4);
                gs.setTerrain(2);
                gs.setElevation(1.0);
            }
        }

        // MIDNIGHTBLUE layer: A1-10 through E10
        for(int i = 1; i <= 10; i++) {
            for(int j = ALPHA_A; j <= ALPHA_E; j++) {
                int iLetter = ASCII_CAP_A + (j - 1);
                char cLetter = (char) iLetter;
                String sLetter = Character.toString(cLetter);

                String coord = sLetter + Integer.toString(i);
                GridSquare gs = hmGridCells.get(coord);
                gs.setStroke(Color.BLACK);
                gs.setFill(Color.MIDNIGHTBLUE);
                gs.setOpacity(0.4);
                gs.setTerrain(5);
                gs.setElevation(0.0);
            }
        }

        // MIDNIGHTBLUE layer: X1 through Z58
        for(int i = 1; i <= 58; i++) {
            for(int j = ALPHA_X; j <= ALPHA_Z; j++) {
                int iLetter = ASCII_CAP_A + (j - 1);
                char cLetter = (char) iLetter;
                String sLetter = Character.toString(cLetter);

                String coord = sLetter + Integer.toString(i);
                GridSquare gs = hmGridCells.get(coord);
                gs.setStroke(Color.BLACK);
                gs.setFill(Color.MIDNIGHTBLUE);
                gs.setOpacity(0.4);
                gs.setTerrain(5);
                gs.setElevation(0.0);
            }
        }

        // GREEN outliers: (array)
        String[] pOutliers = { "M35", "N35" };
        for(String coord : pOutliers) {
            GridSquare gs = hmGridCells.get(coord);
            gs.setStroke(Color.BLACK);
            gs.setFill(Color.GREEN);
            gs.setOpacity(0.4);
            gs.setTerrain(3);
            gs.setElevation(2.0);
        }

        // SANDYBROWN outliers: (array)
        String[] yOutliers = {  "A53", "A54", "A55", "A56", "A57", "A58",
                                "B53", "B54", "B55", "B56", "B57", "B58",
                                "C33", "C34", "C56", "C57",
                                "D33",
                                "E31", "E32", "E33",
                                "F23", "F24", "F25", "F26", "F27", "F28", "F29", "F30", "F31",
                                "G22", "G23",
                                "H21", "H22",
                                "I20", "I21", "I50", "I51", "I58",
                                "J19", "J20", "J49", "J50", "J51", "J52", "J53", "J56", "J57", "J58",
                                "K1" , "K2" , "K3" , "K18", "K19", "K20", "K50", "K51", "K52", "K53",
                                "L1" , "L17", "L18", "L52", "L53", "L54"};

        for(String coord : yOutliers) {
            GridSquare gs = hmGridCells.get(coord);
            gs.setStroke(Color.BLACK);
            gs.setFill(Color.SANDYBROWN);
            gs.setOpacity(0.4);
            gs.setTerrain(2);
            gs.setElevation(1.0);
        }

        // OLIVE layer: (array)
        String[] oOutliers = {  "A11", "A12", "A13", "A14", "A15", "A16",
                                "B11", "B12", "B13", "B14", "B15", "B16",
                                "C11", "C12", "C13", "C14", "C15", "C16",
                                "D11", "D12", "D13", "D14", "D15", "D16",
                                "E11", "E12", "E13", "E14", "E15", "E16", "E17",
                                "F1" , "F2" , "F3" , "F4" , "F5" , "F6" , "F7" , "F8" , "F9" , "F10", "F11", "F12", "F13", "F14", "F15", "F16",
                                "G1" , "G2" , "G3" , "G4" , "G5" , "G6" , "G7" , "G8" , "G9" , "G10", "G11", "G12", "G13", "G14", "G15",
                                "H1" , "H2" , "H3" , "H4" , "H5" , "H6" , "H7" , "H8" , "H9" , "H10", "H11", "H12", "H13", "H14",
                                "I1" , "I2" , "I3" , "I4" , "I5" , "I6" , "I7" , "I8" , "I9" , "I10", "I11", "I12", "I13",
                                "J1" , "J2" , "J3" , "J4" , "J5" , "J6" , "J7" , "J8" , "J9" , "J10", "J54", "J55",
                                "K4" , "K5" , "K6" , "K7" , "K8" , "K47", "K48", "K49", "K54", "K55", "K56", "K57", "K58",
                                "L36", "L37", "L46", "L47", "L48", "L49", "L50", "L51", "L55", "L56", "L57", "L58",
                                "M18", "M19", "M20", "M21", "M22", "M23", "M24", "M25", "M26", "M27", "M28", "M29", "M30", "M31", "M32", "M36",
                                "M37", "M40", "M41", "M42", "M43", "M44", "M45", "M46", "M47", "M48", "M49", "M50", "M51", "M56", "M57", "M58",
                                "N18", "N19", "N20", "N21", "N22", "N23", "N24", "N25", "N26", "N27", "N28", "N29", "N30", "N31", "N32", "N33", "N36",
                                "N37", "N40", "N41", "N42", "N43", "N44", "N45", "N46", "N47", "N48", "N49", "N50", "N51", "N55", "N56", "N57", "N58",
                                "O17", "O18", "O19", "O20", "O21", "O22", "O23", "O24", "O25", "O26", "O27", "O28", "O29", "O30", "O31", "O32", "O33",
                                "O36", "O37", "O40", "O41", "O42", "O43", "O44", "O45", "O55", "O56", "O57", "O58",
                                "P17", "P18", "P19", "P20", "P21", "P22", "P23", "P24", "P25", "P26", "P27", "P28", "P29", "P30", "P31", "P57", "P58",
                                "Q18", "Q19", "Q20", "Q21", "Q22", "Q23", "Q24", "Q25", "Q26", "Q27", "Q58"};

        for(String coord : oOutliers) {
            GridSquare gs = hmGridCells.get(coord);
            gs.setStroke(Color.BLACK);
            gs.setFill(Color.OLIVE);
            gs.setOpacity(0.4);
            gs.setTerrain(1);
            gs.setElevation(3.0);
        }

        // Rock layer: (array)
        String[] rOutliers = {  "A17", "A18", "A19", "A20", "A21", "A22", "A23", "A24", "A25", "A26", "A27", "A28", "A29", "A30", "A31", "A32", "A35",
                                "A36", "A37", "A38", "A39", "A40", "A41", "A42", "A43", "A44", "A45", "A46",
                                "B17", "B18", "B19", "B20", "B21", "B22", "B23", "B24", "B25", "B26", "B27", "B28", "B29", "B30", "B31", "B32",
                                "B39", "B40", "B41", "B42", "B43", "B44", "B45", "B46",
                                "C17", "C18", "C19", "C20", "C21", "C22", "C23", "C24", "C25", "C26", "C27", "C28", "C29", "C30", "C31", "C32", "C42",
                                "C43", "C44", "C45", "C46",
                                "D17", "D18", "D19", "D20", "D21", "D22", "D23", "D24", "D25", "D26", "D27", "D28", "D29", "D30", "D31", "D32",
                                "E8" , "E9" , "E10", "E18", "E19", "E22", "E23", "E24", "E25", "E26", "E27", "E28", "E29", "E30", "E44", "E45",
                                "F17", "F18", "F21", "F22",
                                "G16", "G17", "G20", "G21", "G34",
                                "H15", "H16", "H19", "H20", "H33", "H34", "H35", "H36", "H37", "H43",
                                "I14", "I15", "I18", "I19", "I34", "I35", "I36", "I37", "I38", "I42", "I43", "I44",
                                "J11", "J12", "J13", "J14", "J17", "J18", "J35", "J36", "J37", "J38", "J41", "J42", "J43", "J44",
                                "K9" , "K10", "K16", "K17", "K36", "K37", "K38",
                                "L12", "L16",
                                "M2" , "M3" , "M4" , "M5" , "M6" , "M7" , "M8" , "M12", "M15", "M16",
                                "N7" , "N14", "N15",
                                "O6" , "O7" , "O10", "O14",
                                "P6" , "P9" , "P10", "P13",
                                "Q13"};

        for(String coord : rOutliers) {
            GridSquare gs = hmGridCells.get(coord);
            gs.setStroke(Color.BLACK);
            gs.setFill(Color.BLACK);
            gs.setOpacity(0.4);
            gs.setTerrain(4);
            gs.setElevation(4.0);
        }

        // DARKGRAY layer: (array)
        String[] gOutliers = {  "A33", "A34",
                                "B33", "B34",
                                "C21", "C22",
                                "D21", "D22",
                                "E20", "E21",
                                "F19", "F20",
                                "G18", "G19",
                                "H17", "H18",
                                "I16", "I17",
                                "J15", "J16",
                                "K11", "K12", "K13", "K14", "K15",
                                "L2" , "L3" , "L4" , "L5" , "L6" , "L7" , "L8" , "L9" , "L10", "L11", "L12", "L13", "L14", "L15",
                                "M9" , "M10", "M11", "M13", "M14",
                                "N8" , "N9" , "N10", "N11", "N12", "N13",
                                "O8" , "O9" , "O11", "O12", "O13",
                                "P7" , "P8" , "P11", "P12"};

        for(String coord : gOutliers) {
            GridSquare gs = hmGridCells.get(coord);
            gs.setStroke(Color.BLACK);
            gs.setFill(Color.DARKGRAY);
            gs.setOpacity(0.4);
            gs.setTerrain(0);
            gs.setElevation(1.0);
        }
    }

    /**
     * Method:  getGridSquareObject
     * Purpose: Retrieve GridSquare paired with coordinate
     * @param   coord String location (ie. "C22") as HashMap key
     * @return  GridSquare object for that coord as HashMap value
     *
     */
    public static GridSquare getGridSquareObject(String coord) { return hmGridCells.get(coord); }
}
