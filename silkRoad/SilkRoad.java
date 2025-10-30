package silkRoad;

import shapes.Rectangle;
import shapes.Canvas;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/**
 * SilkRoad creates a square spiral-shaped route.
 * Each square represents a position (one meter) in the route.
 * This class manages the simulation of robots moving along the Silk Road,
 * collecting tenges from stores and calculating optimal profit strategies.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 4.0
 */
public class SilkRoad {
    private int length; // Length of the route in meters
    private int squareSize; // Size of each square in pixels
    private int margin; // Margin between squares in pixels
    private ArrayList<Rectangle> road; // List of squares forming the route
    private ArrayList<Robot> robots; // List of robots on the route
    private ArrayList<Store> stores; // List of stores on the route
    private HashMap<Integer, int[]> positions; // Position map (index -> [x,y])
    private int profit; // Profit accumulator
    private boolean isVisible; // Visibility state of the simulator
    private boolean lastActionSuccessful = true; // Indicates if the last action was successful
    private String lastActionMessage = "No action has been performed yet"; // Last action message
    
    // Variables to store previous state for undo
    private HashMap<Robot, Integer> previousRobotPositions;
    private int previousProfit;
    private boolean undoAvailable = false;
    private Robot lastMovedRobot;
    
    // Progress bar elements
    private Rectangle progressBarBackground;
    private Rectangle progressBarFill;
    private int progressBarWidth;
    private int progressBarHeight;
    
    // Variables for the robot with highest profit
    private Robot highestProfitRobot; // Robot with highest profit
    private Timer blinkTimer; // Timer to control blinking
    private boolean isBlinking = false; // Current blinking state
    
    // Variables needed for undoLastMovement() method
    private Store lastVisitedStore; // Stores the last visited store
    private int lastStoreOriginalTenges; // Original tenges of the store before visit
    
    /**
     * Constructor for SilkRoad objects.
     * Creates a spiral route with the specified length.
     * 
     * @param length the length of the route in meters
     */
    public SilkRoad(int length) {
        this.length = length;
        
        this.squareSize = 20; // Size of each square
        this.margin = 5; // Margin between squares
        this.road = new ArrayList<>();
        this.robots = new ArrayList<>();
        this.stores = new ArrayList<>();
        this.positions = new HashMap<>();
        this.profit = 0; // Initialize profit to 0
        this.isVisible = true; // By default, the simulator is visible
        this.previousRobotPositions = new HashMap<>();
        
        createSpiralRoad();
        initializeProgressBar();
        updateProgressBar();
        
        // Initialize the timer for blinking
        startBlinkTimer();
        
        lastActionSuccessful = true;
        lastActionMessage = "Simulator successfully created with a route of " + length + " meters";
    }
    
    /**
     * Constructor for SilkRoad objects using a 2D array of days/actions.
     * The first element of the array specifies the number of actions to process.
     * Action codes: 1 = add robot, 2 = add store.
     * 
     * @param days 2D array containing actions to perform: 
     *             [[numDays], [action, position], [action, position, tenges], ...]
     */
    public SilkRoad(int[][] days) {
        // Validate that the array is not empty
        if (days == null || days.length == 0) {
            this.length = 100; // Default length
        } else {
            this.length = 100; // Use default length of 100
            
            // Extract the number of days/actions
            int numDays = days[0][0];
            
            this.squareSize = 20; // Size of each square
            this.margin = 5; // Margin between squares
            this.road = new ArrayList<>();
            this.robots = new ArrayList<>();
            this.stores = new ArrayList<>();
            this.positions = new HashMap<>();
            this.profit = 0; // Initialize profit to 0
            this.isVisible = true; // By default, the simulator is visible
            this.previousRobotPositions = new HashMap<>();
            
            createSpiralRoad();
            initializeProgressBar();
            updateProgressBar();
            
            // Initialize the timer for blinking
            startBlinkTimer();
            
            // Process the actions
            for (int i = 1; i < days.length && i <= numDays; i++) {
                if (days[i].length < 2) {
                    continue;
                }
                
                int action = days[i][0];
                
                if (action == 1) { // Add robot
                    int position = days[i][1];
                    placeRobot(position);
                } 
                else if (action == 2 && days[i].length >= 3) { // Add store
                    int position = days[i][1];
                    int tenges = days[i][2];
                    placeStore(position, tenges);
                }
            }
            
            lastActionSuccessful = true;
            lastActionMessage = "Simulator created from array with " + numDays + " actions";
        }
    }
    
    /**
     * Initializes the timer for blinking the robot with the highest profit.
     * The timer triggers a blink every 500ms for visual feedback.
     */
    private void startBlinkTimer() {
        if (blinkTimer != null) {
            blinkTimer.cancel();
        }
        
        blinkTimer = new Timer();
        blinkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (highestProfitRobot != null) {
                    highestProfitRobot.blink();
                }
            }
        }, 0, 500); // Blink every 500ms
    }
    
    /**
     * Updates the robot with the highest profit and manages blinking behavior.
     * Stops blinking for the previous leader and starts blinking for the new one.
     */
    private void updateHighestProfitRobot() {
        if (robots.isEmpty()) {
            return;
        }
        
        Robot previousHighestRobot = highestProfitRobot;
        highestProfitRobot = null;
        int highestProfit = Integer.MIN_VALUE;
        
        // Find the robot with highest profit
        for (Robot robot : robots) {
            int robotProfit = robot.getTotalProfit();
            if (robotProfit > highestProfit) {
                highestProfit = robotProfit;
                highestProfitRobot = robot;
            }
        }
        
        // If the robot with highest profit changed
        if (highestProfitRobot != null && previousHighestRobot != highestProfitRobot) {
            // Previous robot stops blinking
            if (previousHighestRobot != null) {
                previousHighestRobot.setBlinking(false);
            }
            
            // New robot starts blinking
            highestProfitRobot.setBlinking(true);
        }
        else if (highestProfitRobot != null && !isBlinking) {
            // If this is the first time there is a leader
            highestProfitRobot.setBlinking(true);
            isBlinking = true;
        }
    }
    
    /**
     * Initializes the progress bar visual components.
     * Creates background (black) and fill (green) rectangles to display
     * the simulation progress relative to maximum possible profit.
     */
    private void initializeProgressBar() {
        this.progressBarWidth = 200;  // Total width of horizontal bar
        this.progressBarHeight = 20;  // Height of the bar
        
        // Create the bar background (black)
        progressBarBackground = new Rectangle();
        progressBarBackground.changeColor("black");
        progressBarBackground.changeSize(progressBarHeight, progressBarWidth); // height=20, width=200
        
        // Position the bar below the route
        progressBarBackground.moveHorizontal(100);
        progressBarBackground.moveVertical(400);
        progressBarBackground.makeVisible();
        
        // Create the bar fill (green)
        progressBarFill = new Rectangle();
        progressBarFill.changeColor("green");
        progressBarFill.changeSize(progressBarHeight, 0); // height=20, width=0 (initially)
        
        // Position the fill in the same place
        progressBarFill.moveHorizontal(100);
        progressBarFill.moveVertical(400);
        progressBarFill.makeVisible();
    }

    /**
     * Calculates the maximum possible profit with all existing stores.
     * Includes empty stores using their initial tenges values.
     * For each store, finds the robot that can achieve the maximum benefit.
     * 
     * @return the maximum possible profit in tenges
     */
    private int calculateMaximumPossibleProfit() {
        int maxPossibleProfit = 0;
        
        // For each store (including empty ones), use their initial tenges
        for (Store store : stores) {
            int initialTenges = store.getInitialTenges(); // Use initial tenges, not current
            if (initialTenges <= 0) continue;
            
            int maxBenefitForStore = 0;
            
            // Check all robots for this store
            for (Robot robot : robots) {
                // MODIFIED: Use SilkRoad method to calculate distance
                int distance = calculateDistance(robot.getInitialPosition(), store.getPosition());
                int benefit = initialTenges - distance; // Use initial tenges
                
                // Only consider positive benefits
                if (benefit > 0 && benefit > maxBenefitForStore) {
                    maxBenefitForStore = benefit;
                }
            }
            
            // Add the best benefit found for this store
            maxPossibleProfit += maxBenefitForStore;
        }
        
        return maxPossibleProfit;
    }

    /**
     * Updates the progress bar fill width based on current profit ratio.
     * The bar fills proportionally to the current profit divided by
     * the maximum possible profit.
     */
    private void updateProgressBar() {
        int maxPossibleProfit = calculateMaximumPossibleProfit();
        
        // Avoid division by zero
        if (maxPossibleProfit <= 0) {
            progressBarFill.changeSize(progressBarHeight, 0); // height=20, width=0
            return;
        }
        
        // Calculate the proportion of current profit relative to maximum possible
        double proportion = (double) profit / maxPossibleProfit;
        if (proportion > 1.0) {
            proportion = 1.0;
        }
        
        // Calculate the width of the bar fill
        int fillWidth = (int) (progressBarWidth * proportion);
        
        // KEY: Use parameters in the correct order
        progressBarFill.changeSize(progressBarHeight, fillWidth); // height=20, width=variable
    }

    /**
     * Creates the route in a square spiral shape.
     * The spiral expands outward from the center point, with each position
     * represented by a yellow square on the canvas.
     */
    private void createSpiralRoad() {
        // Starting point (approximate center of canvas)
        int centerX = 150;
        int centerY = 150;
        
        // Variables for the spiral
        int x = 0;
        int y = 0;
        int step = 1; // Current segment length
        int direction = 0; // 0: right, 1: down, 2: left, 3: up
        int stepCount = 0; // Step counter in current direction
        
        for (int i = 0; i < length; i++) {
            // Create square
            Rectangle square = new Rectangle();
            square.changeSize(squareSize, squareSize);
            square.changeColor("yellow");
            
            // Calculate absolute position
            int xPos = centerX + x * (squareSize + margin);
            int yPos = centerY + y * (squareSize + margin);
            
            // Adjust position relative to rectangle origin
            square.moveHorizontal(xPos - 70); // 70 is the default xPosition
            square.moveVertical(yPos - 15);   // 15 is the default yPosition
            
            square.makeVisible(); // Initially visible
            road.add(square);
            
            // Save the position for future reference
            positions.put(i, new int[] {xPos, yPos});
            
            // Move to next position following the spiral
            stepCount++;
            
            // If we completed the steps in this direction
            if (stepCount == step) {
                direction = (direction + 1) % 4; // Change direction
                stepCount = 0; // Reset step counter
                
                // Increase step size every two turns (half complete cycle)
                if (direction == 0 || direction == 2) {
                    step++; // Increment the size of the next segment
                }
            }
            
            // Calculate next position according to direction
            switch (direction) {
                case 0: x++; break; // Right
                case 1: y++; break; // Down
                case 2: x--; break; // Left
                case 3: y--; break; // Up
            }
        }
    }
    
    /**
     * Makes the entire simulator visible, including all elements.
     * This includes the road, robots, stores, and progress bar.
     */
    public void makeVisible() {
        if (!isVisible) {
            // Make all road squares visible
            for (Rectangle square : road) {
                square.makeVisible();
            }
            
            // Make all robots visible
            for (Robot robot : robots) {
                robot.setVisible(true);
            }
            
            // Make all stores visible
            for (Store store : stores) {
                store.setVisible(true);
            }
            
            // Make progress bar visible
            progressBarBackground.makeVisible();
            progressBarFill.makeVisible();
            
            startBlinkTimer();
            updateHighestProfitRobot();
            
            isVisible = true;
            
            lastActionSuccessful = true;
            lastActionMessage = "The simulator is now visible";
        } else {
            lastActionSuccessful = true;
            lastActionMessage = "The simulator was already visible";
        }
    }
    
    /**
     * Makes the entire simulator invisible, including all elements.
     * The simulation continues running in the background.
     */
    public void makeInvisible() {
        if (isVisible) {
            // Make all road squares invisible
            for (Rectangle square : road) {
                square.makeInvisible();
            }
            
            // Make all robots invisible
            for (Robot robot : robots) {
                robot.setVisible(false);
                
                if(robot == highestProfitRobot){
                    robot.setBlinking(false);                
                }
            }
            
            // Make all stores invisible
            for (Store store : stores) {
                store.setVisible(false);
            }
            
            // Make progress bar invisible
            progressBarBackground.makeInvisible();
            progressBarFill.makeInvisible();
            
            if(blinkTimer != null){
                blinkTimer.cancel();
                blinkTimer = null;
            }
            
            isVisible = false;
            
            lastActionSuccessful = true;
            lastActionMessage = "The simulator is now invisible, but continues running in the background";
        } else {
            lastActionSuccessful = true;
            lastActionMessage = "The simulator was already invisible";
        }
    }
    
    /**
     * Returns the current visibility state of the simulator.
     * 
     * @return true if the simulator is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * Places a robot on the route at the specified position.
     * Creates a normal robot by default.
     * 
     * @param position the initial position of the robot (0 to length-1)
     */
    public void placeRobot(int position) {
        try {
            if (position < 0 || position >= length) {
                lastActionSuccessful = false;
                lastActionMessage = "Error: Invalid position. Must be between 0 and " + (length-1);
                return;
            }
            
            // Check if there's already a robot at that position
            for (Robot r : robots) {
                if (r.getPosition() == position) {
                    lastActionSuccessful = false;
                    lastActionMessage = "Error: There is already a robot at position " + position;
                    return;
                }
            }
            
            // Create a normal robot with automatic color
            Robot robot = new NormalRobot(position);
            robots.add(robot);
            
            // Get position on canvas and place the robot
            int[] pos = positions.get(position);
            robot.updateCanvasPosition(pos[0], pos[1]);
            
            // Only make visible if simulator is visible
            if (isVisible) {
                robot.setVisible(true);
            } else {
                robot.setVisible(false);
            }
            
            // Update progress bar
            updateProgressBar();
            
            // Update robot with highest profit
            updateHighestProfitRobot();
            
            lastActionSuccessful = true;
            lastActionMessage = "Normal robot with color " + robot.getColor() + 
                               " successfully placed at position " + position;
            
        } catch (NumberFormatException e) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: Please enter a valid number for the position";
        }
    }
    
    /**
     * Moves a robot along the route.
     * Uses polymorphism to handle different robot and store behaviors.
     * 
     * @param posToMove the current position of the robot to move
     * @param moveCount the number of positions to move (positive = forward, negative = backward)
     */
    public void moveRobot(int posToMove, int moveCount) {
        if (robots.isEmpty()) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No robots to move";
            return;
        }
        
        // Search for robot at the indicated position
        Robot robotToMove = null;
        for (Robot r : robots) {
            if (r.getPosition() == posToMove) {
                robotToMove = r;
                break;
            }
        }
        
        if (robotToMove == null) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No robot at position " + posToMove;
            return;
        }
        
        // POLYMORPHISM: Check if the robot can make this move
        if (!robotToMove.canMove(moveCount)) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: " + robotToMove.getType() + 
                               " type robots cannot move backwards";
            return;
        }
        
        try {
            // Save current state for undo
            previousRobotPositions.clear();
            for (Robot r : robots) {
                previousRobotPositions.put(r, r.getPosition());
            }
            previousProfit = profit;
            lastMovedRobot = robotToMove;
            undoAvailable = true;
            
            // Reset information about last visited store
            lastVisitedStore = null;
            lastStoreOriginalTenges = 0;
            
            // Move the robot
            int currentPos = robotToMove.getPosition();
            int newPos = (currentPos + moveCount) % length;
            if (newPos < 0) newPos += length;
            
            // Update robot position
            int[] newCoords = positions.get(newPos);
            robotToMove.updatePosition(newPos);
            robotToMove.updateCanvasPosition(newCoords[0], newCoords[1]);
            
            // Make robot visible only if simulator is visible
            if (isVisible) {
                robotToMove.setVisible(true);
            } else {
                robotToMove.setVisible(false);
            }
            
            // Check if there is a store at the new position
            boolean passedStore = false;
            
            for (Store store : stores) {
                if (store.getPosition() == newPos) {
                    int distanceTraveled = calculateDistance(robotToMove.getInitialPosition(), newPos);
                    int storeTenges = store.getTenges();
                    
                    // Save information for undo
                    lastVisitedStore = store;
                    lastStoreOriginalTenges = storeTenges;
                    
                    passedStore = true;
                    
                    // POLYMORPHISM: Check if robot takes any tenges (lazy robots don't)
                    int tengesToTake = robotToMove.getTengesToTake(storeTenges);
                    
                    // Special case: Lazy robot
                    if ("lazy".equals(robotToMove.getType())) {
                        lastActionSuccessful = true;
                        lastActionMessage = "Lazy robot " + robotToMove.getColor() + 
                                         " moved to position " + newPos + 
                                         " and passed through a " + store.getType() + 
                                         " store but was too lazy to collect tenges";
                        
                        lastVisitedStore = null;
                        lastStoreOriginalTenges = 0;
                        break;
                    }
                    
                    // POLYMORPHISM: Check if robot can take tenges from this store
                    if (!store.canRobotTakeTenges(robotToMove)) {
                        lastActionSuccessful = true;
                        lastActionMessage = "Robot " + robotToMove.getColor() + 
                                         " (type " + robotToMove.getType() + 
                                         ") moved to position " + newPos + 
                                         " but could not take tenges from " + store.getType() + 
                                         " store (insufficient profit)";
                        
                        lastVisitedStore = null;
                        lastStoreOriginalTenges = 0;
                    }
                    else if (storeTenges > 0 && tengesToTake > 0) {
                        // POLYMORPHISM: Calculate profit using robot's method
                        int profitCalculated = robotToMove.calculateProfit(storeTenges, distanceTraveled);
                        
                        // Update store tenges
                        store.setTenges(storeTenges - tengesToTake);
                        
                        // Add to total game profit
                        profit += profitCalculated;
                        
                        // Register the profit in the robot
                        robotToMove.addProfit(profitCalculated, newPos);
                        
                        lastActionSuccessful = true;
                        lastActionMessage = "Robot " + robotToMove.getColor() + 
                                         " (type " + robotToMove.getType() + 
                                         ") moved to position " + newPos + 
                                         " and took " + tengesToTake + " tenges from " + 
                                         store.getType() + " store. Profit: " + 
                                         profitCalculated + " tenges";
                    } else {
                        lastActionSuccessful = true;
                        lastActionMessage = "Robot " + robotToMove.getColor() + 
                                         " moved to position " + newPos + 
                                         " and passed through a " + store.getType() + 
                                         " store, but it was empty";
                        
                        lastVisitedStore = null;
                        lastStoreOriginalTenges = 0;
                    }
                    
                    // Update progress bar
                    updateProgressBar();
                    
                    // Update robot with highest profit
                    updateHighestProfitRobot();
                    
                    break;
                }
            }
            
            if (!passedStore) {
                lastActionSuccessful = true;
                lastActionMessage = "Robot " + robotToMove.getColor() + 
                                 " successfully moved from position " + 
                                 currentPos + " to position " + newPos;
            }
            
        } catch (NumberFormatException e) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: Please enter valid numbers";
        }
    }
    
    /**
     * Removes a robot from the route at the specified position.
     * If the removed robot was the highest profit robot, updates the leader.
     * 
     * @param position the position of the robot to remove
     */
    public void removeRobot(int position) {
        if (robots.isEmpty()) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No robots to remove";
            return;
        }
        
        // Search for robot at the indicated position
        Robot robotToRemove = null;
        int indexToRemove = -1;
        
        for (int i = 0; i < robots.size(); i++) {
            Robot r = robots.get(i);
            if (r.getPosition() == position) {
                robotToRemove = r;
                indexToRemove = i;
                break;
            }
        }
        
        if (robotToRemove == null) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No robot at position " + position;
            return;
        }
        
        // If the robot to remove is the one blinking, stop the blinking
        if (robotToRemove == highestProfitRobot) {
            robotToRemove.setBlinking(false);
            highestProfitRobot = null;
            
            // Search for a new robot with highest profit among the remaining ones
            if (robots.size() > 1) {
                updateHighestProfitRobot();
            }
        }
        
        // Remove the robot
        String robotColor = robotToRemove.getColor();
        robotToRemove.makeInvisible();
        robots.remove(indexToRemove);
        
        // Update progress bar
        updateProgressBar();
        
        lastActionSuccessful = true;
        lastActionMessage = "Robot " + robotColor + " correctly removed from position " + position;
    }
    
    /**
     * Returns all robots to their initial positions.
     * Does not reset their profit or other statistics.
     */
    public void returnRobots() {
        if (robots.isEmpty()) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No robots to reset";
            return;
        }
        
        for (Robot robot : robots) {
            int initialPos = robot.getInitialPosition();
            int[] coords = positions.get(initialPos);
            robot.updatePosition(initialPos);
            robot.updateCanvasPosition(coords[0], coords[1]);
            
            // Make robot visible only if simulator is visible
            if (isVisible) {
                robot.setVisible(true);
            } else {
                robot.setVisible(false);
            }
        }
        
        // Update progress bar
        updateProgressBar();
        
        lastActionSuccessful = true;
        lastActionMessage = "All robots have returned to their initial positions";
    }
    
    /**
     * Undoes the last robot movement if available.
     * Restores robot positions, store tenges, and total profit to their
     * previous state before the last move.
     */
    public void undoLastMovement() {
        if (!undoAvailable || lastMovedRobot == null) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No movements to undo";
            return;
        }
        
        // Restore positions of all robots
        for (Robot r : robots) {
            Integer previousPos = previousRobotPositions.get(r);
            if (previousPos != null) {
                r.updatePosition(previousPos);
                int[] coords = positions.get(previousPos);
                r.updateCanvasPosition(coords[0], coords[1]);
            }
        }
        
        // If a store was visited, restore its tenges
        if (lastVisitedStore != null) {
            // Restore store tenges
            lastVisitedStore.setTenges(lastStoreOriginalTenges);
        }
        
        // Restore total profit
        profit = previousProfit;
        
        // Deactivate undo option until next movement
        undoAvailable = false;
        lastMovedRobot = null;
        
        // Update progress bar
        updateProgressBar();
        
        // Update robot with highest profit
        updateHighestProfitRobot();
        
        lastActionSuccessful = true;
        lastActionMessage = "Last movement successfully undone";
    }
    
    /**
     * Returns information about all robots on the route.
     * Includes position, color, and profit details for each robot.
     * 
     * @return a formatted string with information about all robots
     */
    public String getRobotsInfo() {
        if (robots.isEmpty()) {
            lastActionSuccessful = true;
            lastActionMessage = "No robots on the silk road";
            return "No robots on the silk road.";
        }
        
        StringBuilder info = new StringBuilder("Information about robots on the silk road:\n\n");
        
        for (int i = 0; i < robots.size(); i++) {
            Robot robot = robots.get(i);
            info.append("Robot ").append(i).append(":\n")
                .append(" Color: ").append(robot.getColor()).append("\n")
                .append(" Current position: ").append(robot.getPosition()).append("\n")
                .append(" Initial position: ").append(robot.getInitialPosition()).append("\n")
                .append(" Total profit: ").append(robot.getTotalProfit()).append(" tenges\n");
                
            if (robot == highestProfitRobot) {
                info.append(" This robot has the highest profit!\n");
            }
            
            info.append("\n");
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Robot information obtained correctly";
        
        return info.toString();
    }
    
    /**
     * Places a store on the route at the specified position.
     * Creates a normal store by default.
     * 
     * @param position the position of the store (0 to length-1)
     * @param tenges the initial amount of tenges in the store
     */
    public void placeStore(int position, int tenges) {
        try {
            if (position < 0 || position >= length) {
                lastActionSuccessful = false;
                lastActionMessage = "Error: Invalid position. Must be between 0 and " + (length-1);
                return;
            }
            
            // Check if there's already a store at that position
            for (Store s : stores) {
                if (s.getPosition() == position) {
                    lastActionSuccessful = false;
                    lastActionMessage = "Error: There is already a store at position " + position;
                    return;
                }
            }
            
            if (tenges < 0) {
                lastActionSuccessful = false;
                lastActionMessage = "Error: The amount of tenges cannot be negative";
                return;
            }
            
            // Create a normal store
            Store store = new NormalStore(position, tenges);
            stores.add(store);
            
            // Get position on canvas and place the store
            int[] pos = positions.get(position);
            store.updateCanvasPosition(pos[0], pos[1]);
            
            // Only make visible if simulator is visible
            if (isVisible) {
                store.setVisible(true);
            } else {
                store.setVisible(false);
            }
            
            // Update progress bar
            updateProgressBar();
            
            lastActionSuccessful = true;
            lastActionMessage = "Normal store successfully placed at position " + 
                               position + " with " + tenges + " tenges";
            
        } catch (NumberFormatException e) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: Please enter valid numbers";
        }
    }
    
    /**
     * Removes a store from the route at the specified position.
     * 
     * @param position the position of the store to remove
     */
    public void removeStore(int position) {
        if (stores.isEmpty()) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No stores to remove";
            return;
        }
        
        // Search for store at the indicated position
        Store storeToRemove = null;
        int indexToRemove = -1;
        
        for (int i = 0; i < stores.size(); i++) {
            Store s = stores.get(i);
            if (s.getPosition() == position) {
                storeToRemove = s;
                indexToRemove = i;
                break;
            }
        }
        
        if (storeToRemove == null) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No store at position " + position;
            return;
        }
        
        // Remove the store
        storeToRemove.makeInvisible();
        stores.remove(indexToRemove);
        
        // Update progress bar
        updateProgressBar();
        
        lastActionSuccessful = true;
        lastActionMessage = "Store correctly removed from position " + position;
    }
    
    /**
     * Returns the length of the route in meters.
     * 
     * @return the route length
     */
    public int getLength() {
        return length;
    }
    

    /**
     * Returns the canvas coordinates for a given position index.
     * 
     * @param index the position index in the route
     * @return an int array with [x, y] canvas coordinates
     */
    public int[] getPositionForIndex(int index) {
        return positions.get(index);
    }
    
    /**
     * Calculates the shortest distance between two positions on the route.
     * Considers the route as circular and returns the minimum distance.
     * 
     * @param startPos the starting position
     * @param endPos the ending position
     * @return the shortest distance between the two positions
     */
    public int calculateDistance(int startPos, int endPos) {
        // Calculate shortest distance considering the route is circular
        int directDistance = Math.abs(endPos - startPos);
        int wrappedDistance = length - directDistance;
        
        // Return shortest distance
        return Math.min(directDistance, wrappedDistance);
    }
    
    /**
     * Returns the accumulated profit.
     * 
     * @return the total accumulated profit in tenges
     */
    public int getProfit() {
        return profit;
    }
    
    /**
     * Returns the total accumulated profit with percentage of maximum possible.
     * Also updates the last action message with detailed profit information.
     * 
     * @return the total accumulated profit in tenges
     */
    public int profit() {
        int maxPossibleProfit = calculateMaximumPossibleProfit();
        double percentage = 0;
        if (maxPossibleProfit > 0) {
            percentage = (profit * 100.0) / maxPossibleProfit;
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Total accumulated profit: " + profit + " tenges (" + 
                           String.format("%.2f", percentage) + "% of maximum possible)";
        
        return profit;
    }
    
    /**
     * Resupplies all stores with their initial tenges values.
     * Restores all stores to their original inventory.
     */
    public void resupplyStores() {
        if (stores.isEmpty()) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No stores to resupply";
            return;
        }
        
        int totalStores = 0;
        int totalTenges = 0;
        
        for (Store store : stores) {
            int previousTenges = store.getTenges();
            store.resupply();
            totalStores++;
            totalTenges += (store.getTenges() - previousTenges);
        }
        
        // Update progress bar
        updateProgressBar();
        
        lastActionSuccessful = true;
        lastActionMessage = "Resupply completed: " + totalStores + " stores resupplied with " + 
                           totalTenges + " tenges in total";
    }
    
    /**
     * Displays complete simulation information to the console.
     * Includes details about the route, robots, stores, and profit statistics.
     */
    public void showSimulationInfo() {
        int maxPossibleProfit = calculateMaximumPossibleProfit();
        double percentage = 0;
        if (maxPossibleProfit > 0) {
            percentage = (profit * 100.0) / maxPossibleProfit;
        }
        
        StringBuilder info = new StringBuilder();
        
        // General route information
        info.append("=== SILK ROAD SIMULATOR INFORMATION ===\n\n");
        info.append("Route length: ").append(length).append(" meters\n");
        info.append("Accumulated profit: ").append(profit).append(" tenges (").append(String.format("%.2f", percentage)).append("% of maximum possible)\n");
        info.append("Maximum possible profit: ").append(maxPossibleProfit).append(" tenges\n");
        info.append("Simulator status: ").append(isVisible ? "Visible" : "Invisible").append("\n\n");
        
        // Store information
        info.append("=== STORE INFORMATION ===\n");
        if (stores.isEmpty()) {
            info.append("No stores on the route.\n");
        } else {
            info.append("Total stores: ").append(stores.size()).append("\n\n");
            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
                info.append("Store ").append(i).append(":\n")
                    .append("  - Position: ").append(store.getPosition()).append("\n")
                    .append("  - Current tenges: ").append(store.getTenges()).append("\n")
                    .append("  - Initial tenges: ").append(store.getInitialTenges()).append("\n")
                    .append("  - Color: ").append(store.getColor()).append("\n");
                
                // Calculate stock percentage
                int stockPercentage = (store.getTenges() * 100) / Math.max(1, store.getInitialTenges());
                info.append("  - Current stock: ").append(stockPercentage).append("%\n\n");
            }
        }
        
        // Robot information
        info.append("=== ROBOT INFORMATION ===\n");
        if (robots.isEmpty()) {
            info.append("No robots on the route.\n");
        } else {
            info.append("Total robots: ").append(robots.size()).append("\n\n");
            for (int i = 0; i < robots.size(); i++) {
                Robot robot = robots.get(i);
                info.append("Robot ").append(i).append(" (").append(robot.getColor()).append("):\n")
                    .append("  - Initial position: ").append(robot.getInitialPosition()).append("\n")
                    .append("  - Current position: ").append(robot.getPosition()).append("\n")
                    .append("  - Total profit: ").append(robot.getTotalProfit()).append(" tenges\n");
                
                // Calculate distance traveled from initial position
                int distance = calculateDistance(robot.getInitialPosition(), robot.getPosition());
                info.append("  - Distance from start: ").append(distance).append(" meters\n");
                
                if (robot == highestProfitRobot) {
                    info.append("  - This robot has the highest profit!\n");
                }
                
                info.append("\n");
            }
        }
        
        // Calculate how many stores are empty
        int emptyStores = 0;
        for (Store store : stores) {
            if (store.getTenges() == 0) {
                emptyStores++;
            }
        }
        
        info.append("Empty stores: ").append(emptyStores).append(" of ").append(stores.size()).append("\n");
        info.append("Average profit per store: ");
        if (!stores.isEmpty()) {
            info.append(profit / (double)stores.size()).append(" tenges\n");
        } else {
            info.append("N/A\n");
        }
        
        // PRINT TO CONSOLE
        System.out.println(info.toString());
        
        lastActionSuccessful = true;
        lastActionMessage = "Simulator information displayed correctly";
    }
    
    /**
     * Completely resets the simulator.
     * Resets profit to 0, resupplies all stores, returns robots to initial
     * positions, and resets robot profit statistics.
     */
    public void resetSimulator() {
        // Reset profit to 0
        this.profit = 0;
        
        // Resupply all stores
        for (Store store : stores) {
            store.resupply();
        }
        
        // Stop blinking on all robots
        for (Robot robot : robots) {
            robot.setBlinking(false);
            robot.resetProfit(); // Reset each robot's profit
            
            int initialPos = robot.getInitialPosition();
            int[] coords = positions.get(initialPos);
            robot.updatePosition(initialPos);
            robot.updateCanvasPosition(coords[0], coords[1]);
            
            // Make robot visible only if simulator is visible
            if (isVisible) {
                robot.setVisible(true);
            } else {
                robot.setVisible(false);
            }
        }
        
        // Reset robot with highest profit
        highestProfitRobot = null;
        isBlinking = false;
        
        // Update progress bar
        updateProgressBar();
        
        lastActionSuccessful = true;
        lastActionMessage = "Simulator completely reset";
    }
    
    /**
     * Finishes and cleans up the simulator.
     * Stops timers, displays final summary, makes everything invisible,
     * and releases resources.
     */
    public void finish() {
        // Stop blinking timer
        if (blinkTimer != null) {
            blinkTimer.cancel();
            blinkTimer = null;
        }
        
        // Stop blinking on all robots
        for (Robot robot : robots) {
            robot.setBlinking(false);
        }
        
        // Calculate final maximum possible profit
        int maxPossibleProfit = calculateMaximumPossibleProfit();
        double percentage = 0;
        if (maxPossibleProfit > 0) {
            percentage = (profit * 100.0) / maxPossibleProfit;
        }
        
        // Show final summary before finishing
        StringBuilder summary = new StringBuilder();
        summary.append("=== SIMULATOR FINAL SUMMARY ===\n\n");
        summary.append("Route length: ").append(length).append(" meters\n");
        summary.append("Final profit: ").append(profit).append(" tenges (").append(String.format("%.2f", percentage)).append("% of maximum possible)\n");
        summary.append("Maximum possible profit: ").append(maxPossibleProfit).append(" tenges\n\n");
        
        // Robot statistics
        summary.append("Total robots: ").append(robots.size()).append("\n");
        
        // Store statistics
        summary.append("Total stores: ").append(stores.size()).append("\n");
        
        // Calculate empty stores
        int emptyStores = 0;
        for (Store store : stores) {
            if (store.getTenges() == 0) {
                emptyStores++;
            }
        }
        summary.append("Empty stores: ").append(emptyStores).append(" of ").append(stores.size()).append("\n\n");
        
        summary.append("Thank you for using the Silk Road simulator.\n");
        summary.append("The simulator will now close.");
        
        // Hide all visual elements
        makeInvisible();
        
        // Release resources
        for (Rectangle square : road) {
            square = null;
        }
        
        road.clear();
        robots.clear();
        stores.clear();
        positions.clear();
        
        progressBarBackground = null;
        progressBarFill = null;
        
        lastActionSuccessful = true;
        lastActionMessage = "Simulator finished correctly";
    }
    
    /**
     * Returns a 2D array with store information, sorted by position.
     * Each row contains: [position, tenges, typeNumeric]
     * where typeNumeric is: 0=normal, 1=autonomous, 2=fighter
     * 
     * @return 2D array with store data [position][tenges][type]
     */
    public int[][] stores() {
        if (stores.isEmpty()) {
            return new int[0][0];
        }
        
        // Sort stores by position
        ArrayList<Store> sortedStores = new ArrayList<>(stores);
        sortedStores.sort((s1, s2) -> Integer.compare(s1.getPosition(), s2.getPosition()));
        
        // Create array with store data [position, tenges, numericType]
        int[][] storesArray = new int[sortedStores.size()][3]; // Now with 3 columns
        
        for (int i = 0; i < sortedStores.size(); i++) {
            Store store = sortedStores.get(i);
            storesArray[i][0] = store.getPosition();
            storesArray[i][1] = store.getTenges();
            
            // Add store type as numeric value
            switch (store.getType().toLowerCase()) {
                case "autonomous":
                    storesArray[i][2] = 1;
                    break;
                case "fighter":
                    storesArray[i][2] = 2;
                    break;
                default: // "normal" or others
                    storesArray[i][2] = 0;
                    break;
            }
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Store information obtained and displayed on console";
        
        return storesArray;
    }
    
    /**
     * Returns a 2D array with robot information, sorted by position.
     * Each row contains: [position, totalProfit, typeNumeric]
     * where typeNumeric is: 0=normal, 1=neverback, 2=tender, 3=lazy
     * 
     * @return 2D array with robot data [position][profit][type]
     */
    public int[][] robots() {
        if (robots.isEmpty()) {
            return new int[0][0];
        }
        
        // Sort robots by position
        ArrayList<Robot> sortedRobots = new ArrayList<>(robots);
        sortedRobots.sort((r1, r2) -> Integer.compare(r1.getPosition(), r2.getPosition()));
        
        // Create array with robot data [position, totalProfit, numericType]
        int[][] robotsArray = new int[sortedRobots.size()][3];
        
        for (int i = 0; i < sortedRobots.size(); i++) {
            Robot robot = sortedRobots.get(i);
            robotsArray[i][0] = robot.getPosition();
            robotsArray[i][1] = robot.getTotalProfit();
            
            // Add robot type as numeric value
            switch (robot.getType().toLowerCase()) {
                case "neverback":
                    robotsArray[i][2] = 1;
                    break;
                case "tender":
                    robotsArray[i][2] = 2;
                    break;
                case "lazy":
                    robotsArray[i][2] = 3;
                    break;
                default: // "normal" or others
                    robotsArray[i][2] = 0;
                    break;
            }
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Robot information obtained and displayed on console";
        
        return robotsArray;
    }   
    
    /**
     * Performs optimal movements for all available robots
     * using the optimal assignment algorithm.
     */
    public void moveRobots() {
        if (robots.isEmpty() || stores.isEmpty()) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: No robots or stores to perform optimal movements";
            return;
        }
        
        // Get optimal assignments using the solve() algorithm
        Map<Integer, Integer> optimalAssignments = calculateOptimalAssignments();
        
        if (optimalAssignments.isEmpty()) {
            lastActionSuccessful = true;
            lastActionMessage = "No beneficial movements available";
            return;
        }
        
        // Execute each optimal assignment
        int movementsMade = 0;
        for (Map.Entry<Integer, Integer> assignment : optimalAssignments.entrySet()) {
            int robotIndex = assignment.getKey();
            int storeIndex = assignment.getValue();
            
            if (robotIndex >= 0 && robotIndex < robots.size() && 
                storeIndex >= 0 && storeIndex < stores.size()) {
                
                Robot robot = robots.get(robotIndex);
                Store store = stores.get(storeIndex);
                
                int currentPosition = robot.getPosition();
                int targetPosition = store.getPosition();
                
                // Calculate shortest distance
                int distanceForward = (targetPosition - currentPosition) % length;
                if (distanceForward < 0) distanceForward += length;
                
                int distanceBackward = (currentPosition - targetPosition) % length;
                if (distanceBackward < 0) distanceBackward += length;
                
                // Choose shortest direction
                int moveDistance = (distanceForward <= distanceBackward) ? distanceForward : -distanceBackward;
                
                // Execute the movement
                moveRobot(currentPosition, moveDistance);
                movementsMade++;
            }
        }
        
        lastActionSuccessful = true;
        lastActionMessage = movementsMade + " optimal movements were made";
    }
    
    /**
     * Calculates optimal robot-to-store assignments using the same
     * algorithm as solve() in SilkRoadContest.
     * Builds a profit matrix and applies the optimal assignment algorithm.
     * 
     * @return map with assignments (robot index -> store index)
     */
    private Map<Integer, Integer> calculateOptimalAssignments() {
        Map<Integer, Integer> assignments = new HashMap<>();
        
        if (robots.isEmpty() || stores.isEmpty()) {
            return assignments;
        }
        
        // Construir la matriz de ganancias
        int[][] profitsMatrix = new int[robots.size()][stores.size()];
        
        for (int i = 0; i < robots.size(); i++) {
            Robot robot = robots.get(i);
            int robotPos = robot.getPosition();
            
            for (int j = 0; j < stores.size(); j++) {
                Store store = stores.get(j);
                int storePos = store.getPosition();
                int tenges = store.getTenges();
                
                // Calculate distance (consider circular route)
                // MODIFIED: Use SilkRoad method to calculate distance
                int distance = calculateDistance(robotPos, storePos);
                
                // Calculate profit (don't allow negative profits)
                int profit = Math.max(0, tenges - distance);
                profitsMatrix[i][j] = profit;
            }
        }
        
        // Use optimal assignment algorithm
        assignments = optimalAssignment(profitsMatrix);
        
        return assignments;
    }
    
    /**
     * Finds the optimal robot-to-store assignment.
     * Uses exhaustive search for small cases (≤10 robots and stores),
     * and greedy approximation for larger cases.
     * 
     * @param profitsMatrix 2D array where profitsMatrix[i][j] is the profit
     *                      of assigning robot i to store j
     * @return map with optimal assignments (robot index -> store index)
     */
    private Map<Integer, Integer> optimalAssignment(int[][] profitsMatrix) {
        Map<Integer, Integer> assignments = new HashMap<>();
        
        if (profitsMatrix.length == 0 || profitsMatrix[0].length == 0) {
            return assignments;
        }
        
        int numRobots = profitsMatrix.length;
        int numStores = profitsMatrix[0].length;
        
        // For small cases, use exhaustive search
        if (numRobots <= 10 && numStores <= 10) {
            return findMaxAssignment(profitsMatrix);
        }
        
        // For larger cases, use greedy approximation
        return greedyAssignment(profitsMatrix);
    }
    
    /**
     * Exhaustive search for small problem sizes.
     * Determines whether to enumerate store or robot combinations based on counts.
     * 
     * @param profitsMatrix the profit matrix
     * @return map with optimal assignments
     */
    private Map<Integer, Integer> findMaxAssignment(int[][] profitsMatrix) {
        int numRobots = profitsMatrix.length;
        int numStores = profitsMatrix[0].length;
        
        if (numStores >= numRobots) {
            return findMaxAssignmentStores(profitsMatrix, numRobots, numStores);
        } else {
            return findMaxAssignmentRobots(profitsMatrix, numRobots, numStores);
        }
    }
    
    /**
     * Exhaustive search when there are more stores than robots.
     * Generates all combinations of stores and all permutations to find maximum profit.
     * 
     * @param profitsMatrix the profit matrix
     * @param numRobots number of robots
     * @param numStores number of stores
     * @return map with optimal assignments
     */
    private Map<Integer, Integer> findMaxAssignmentStores(int[][] profitsMatrix, int numRobots, int numStores) {
        int maxProfit = 0;
        Map<Integer, Integer> bestAssignments = new HashMap<>();
        
        List<Integer> storeIndices = new ArrayList<>();
        for (int i = 0; i < numStores; i++) {
            storeIndices.add(i);
        }
        
        // Generate all combinations of stores
        List<List<Integer>> combinations = generateCombinations(storeIndices, numRobots);
        
        for (List<Integer> storeSubset : combinations) {
            // Generate all permutations
            List<List<Integer>> permutations = generatePermutations(storeSubset);
            
            for (List<Integer> perm : permutations) {
                int profit = 0;
                Map<Integer, Integer> currentAssignments = new HashMap<>();
                
                for (int i = 0; i < numRobots; i++) {
                    profit += profitsMatrix[i][perm.get(i)];
                    currentAssignments.put(i, perm.get(i));
                }
                
                if (profit > maxProfit) {
                    maxProfit = profit;
                    bestAssignments = new HashMap<>(currentAssignments);
                }
            }
        }
        
        return bestAssignments;
    }
    
    /**
     * Exhaustive search when there are more robots than stores.
     * Generates all combinations of robots and all permutations of stores.
     * 
     * @param profitsMatrix the profit matrix
     * @param numRobots number of robots
     * @param numStores number of stores
     * @return map with optimal assignments
     */
    private Map<Integer, Integer> findMaxAssignmentRobots(int[][] profitsMatrix, int numRobots, int numStores) {
        int maxProfit = 0;
        Map<Integer, Integer> bestAssignments = new HashMap<>();
        
        List<Integer> robotIndices = new ArrayList<>();
        for (int i = 0; i < numRobots; i++) {
            robotIndices.add(i);
        }
        
        // Generate all combinations of robots
        List<List<Integer>> combinations = generateCombinations(robotIndices, numStores);
        
        for (List<Integer> robotSubset : combinations) {
            List<Integer> storeIndices = new ArrayList<>();
            for (int i = 0; i < numStores; i++) {
                storeIndices.add(i);
            }
            
            // Generate all permutations of stores
            List<List<Integer>> permutations = generatePermutations(storeIndices);
            
            for (List<Integer> perm : permutations) {
                int profit = 0;
                Map<Integer, Integer> currentAssignments = new HashMap<>();
                
                for (int i = 0; i < numStores; i++) {
                    profit += profitsMatrix[robotSubset.get(i)][perm.get(i)];
                    currentAssignments.put(robotSubset.get(i), perm.get(i));
                }
                
                if (profit > maxProfit) {
                    maxProfit = profit;
                    bestAssignments = new HashMap<>(currentAssignments);
                }
            }
        }
        
        return bestAssignments;
    }
    
    /**
     * Generates all combinations of k elements from the given list.
     * Uses backtracking to build combinations recursively.
     * 
     * @param elements the list of elements to choose from
     * @param k the number of elements to choose
     * @return list of all possible k-combinations
     */
    private List<List<Integer>> generateCombinations(List<Integer> elements, int k) {
        List<List<Integer>> result = new ArrayList<>();
        generateCombinationsHelper(elements, k, 0, new ArrayList<>(), result);
        return result;
    }
    
    /**
     * Helper method for generating combinations using backtracking.
     * 
     * @param elements the source list of elements
     * @param k target combination size
     * @param start starting index for this iteration
     * @param current current combination being built
     * @param result accumulator for all combinations found
     */
    private void generateCombinationsHelper(List<Integer> elements, int k, int start,
                                          List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            generateCombinationsHelper(elements, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    /**
     * Generates all permutations of the given list.
     * Uses heap's algorithm for efficient permutation generation.
     * 
     * @param elements the list of elements to permute
     * @return list of all possible permutations
     */
    private List<List<Integer>> generatePermutations(List<Integer> elements) {
        List<List<Integer>> result = new ArrayList<>();
        generatePermutationsHelper(new ArrayList<>(elements), 0, result);
        return result;
    }
    
    /**
     * Helper method for generating permutations using backtracking.
     * Swaps elements to generate all possible orderings.
     * 
     * @param elements the list being permuted (modified in place)
     * @param start current starting position
     * @param result accumulator for all permutations found
     */
    private void generatePermutationsHelper(List<Integer> elements, int start,
                                          List<List<Integer>> result) {
        if (start == elements.size()) {
            result.add(new ArrayList<>(elements));
            return;
        }
        
        for (int i = start; i < elements.size(); i++) {
            Collections.swap(elements, start, i);
            generatePermutationsHelper(elements, start + 1, result);
            Collections.swap(elements, start, i);
        }
    }
    
    /**
     * Greedy assignment algorithm for large problem sizes.
     * Assigns robots to stores by selecting the highest profit pairing
     * at each step, ensuring each robot and store is assigned at most once.
     * 
     * @param profitsMatrix the profit matrix
     * @return map with greedy assignments
     */
    private Map<Integer, Integer> greedyAssignment(int[][] profitsMatrix) {
        int numRobots = profitsMatrix.length;
        int numStores = profitsMatrix[0].length;
        
        // Inner class for assignments
        class Assignment implements Comparable<Assignment> {
            int profit;
            int robotIndex;
            int storeIndex;
            
            Assignment(int profit, int robotIndex, int storeIndex) {
                this.profit = profit;
                this.robotIndex = robotIndex;
                this.storeIndex = storeIndex;
            }
            
            @Override
            public int compareTo(Assignment other) {
                return Integer.compare(other.profit, this.profit);
            }
        }
        
        // Create list of all possible assignments
        List<Assignment> assignments = new ArrayList<>();
        for (int i = 0; i < numRobots; i++) {
            for (int j = 0; j < numStores; j++) {
                assignments.add(new Assignment(profitsMatrix[i][j], i, j));
            }
        }
        
        // Sort by descending profit
        Collections.sort(assignments);
        
        Set<Integer> usedRobots = new HashSet<>();
        Set<Integer> usedStores = new HashSet<>();
        Map<Integer, Integer> result = new HashMap<>();
        
        // Assign greedily
        for (Assignment assignment : assignments) {
            if (!usedRobots.contains(assignment.robotIndex) && 
                !usedStores.contains(assignment.storeIndex) &&
                assignment.profit > 0) {
                
                result.put(assignment.robotIndex, assignment.storeIndex);
                usedRobots.add(assignment.robotIndex);
                usedStores.add(assignment.storeIndex);
                
                // If we've already assigned all robots or stores possible
                if (usedRobots.size() == numRobots || usedStores.size() == numStores) {
                    break;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Legacy method maintained for compatibility but no longer used.
     * 
     * @return true if an optimal move was made, false otherwise
     * @deprecated Use moveRobots() instead
     */
    @Deprecated
    private boolean makeOptimalMove() {
        if (robots.isEmpty() || stores.isEmpty()) {
            return false;
        }
        
        // Redirect to new optimized method
        moveRobots();
        return lastActionSuccessful;
    }
    
    /**
     * Queries the number of times each store has been emptied.
     * 
     * @return map with store position as key and empty count as value
     */
    public HashMap<Integer, Integer> getStoresEmptyCount() {
        HashMap<Integer, Integer> emptyCountMap = new HashMap<>();
        
        if (stores.isEmpty()) {
            lastActionSuccessful = true;
            lastActionMessage = "No stores on the silk road";
            return emptyCountMap;
        }
        
        // Sort stores by position to display them in organized manner
        ArrayList<Store> sortedStores = new ArrayList<>(stores);
        sortedStores.sort((s1, s2) -> Integer.compare(s1.getPosition(), s2.getPosition()));
        
        int totalEmptyCount = 0;
        
        for (Store store : sortedStores) {
            int position = store.getPosition();
            int emptyCount = store.getEmptyCount();
            emptyCountMap.put(position, emptyCount);
            
            totalEmptyCount += emptyCount;
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Empty stores query completed successfully";
        
        return emptyCountMap;
    }
    
    /**
     * Queries the number of times each store has been emptied.
     * Sorted by position from lowest to highest.
     * 
     * @return 2D array with [position, empty_count] for each store
     */
    public int[][] emptiedStores() {
        if (stores.isEmpty()) {
            return new int[0][0];
        }
        
        // Sort stores by position
        ArrayList<Store> sortedStores = new ArrayList<>(stores);
        sortedStores.sort((s1, s2) -> Integer.compare(s1.getPosition(), s2.getPosition()));
        
        // Create 2D array [position, times_empty]
        int[][] emptyCountArray = new int[sortedStores.size()][2];
        
        int totalEmptyCount = 0;
        
        for (int i = 0; i < sortedStores.size(); i++) {
            Store store = sortedStores.get(i);
            int position = store.getPosition();
            int emptyCount = store.getEmptyCount();
            
            emptyCountArray[i][0] = position;
            emptyCountArray[i][1] = emptyCount;
            
            totalEmptyCount += emptyCount;
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Empty stores query completed successfully";
        
        return emptyCountArray;
    }
    
    /**
     * Queries the profits each robot has achieved in each movement.
     * Sorted by position from lowest to highest.
     * 
     * @return 2D array with [position, profit_move_1, profit_move_2, ...]
     */
    public int[][] profitPerMove() {
        if (robots.isEmpty()) {
            return new int[0][0];
        }
        
        // Sort robots by position
        ArrayList<Robot> sortedRobots = new ArrayList<>(robots);
        sortedRobots.sort((r1, r2) -> Integer.compare(r1.getPosition(), r2.getPosition()));
        
        // Determine maximum number of movements made by any robot
        int maxMoves = 0;
        for (Robot robot : sortedRobots) {
            int[] history = robot.getProfitHistory();
            if (history.length > maxMoves) {
                maxMoves = history.length;
            }
        }
        
        // Create array with [position, profit_move_1, profit_move_2, ...]
        int[][] profitArray = new int[sortedRobots.size()][maxMoves + 1]; // +1 to include position
        
        for (int i = 0; i < sortedRobots.size(); i++) {
            Robot robot = sortedRobots.get(i);
            int position = robot.getPosition();
            int[] history = robot.getProfitHistory();
            
            // Save position in first column
            profitArray[i][0] = position;
            
            // Fill array with profits per movement
            for (int j = 0; j < maxMoves; j++) {
                if (j < history.length) {
                    profitArray[i][j+1] = history[j];
                } else {
                    profitArray[i][j+1] = 0; // If no data for this movement
                }
            }
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Profit per move information obtained successfully";
        
        return profitArray;
    }
    
    /**
     * Checks if the last action was performed successfully.
     * 
     * @return true if the last action succeeded, false otherwise
     */
    public boolean ok() {
        return lastActionSuccessful;
    }
    
    /**
     * Reboots the simulator for a new day.
     * Keeps robots and stores but returns robots to initial positions
     * and resets the profit counter.
     */
    public void reboot() {
        // Reset profit to 0
        this.profit = 0;
        
        // Return all robots to their initial positions
        returnRobots();
        
        // Reset profit for each robot
        for (Robot robot : robots) {
            robot.resetProfit();
        }
        
        lastActionSuccessful = true;
        lastActionMessage = "Simulator rebooted for a new day";
        
        // Update progress bar
        updateProgressBar();
    }
    
    /**
     * Places a store on the route with a specific type.
     * Uses polymorphism to create the appropriate store subclass.
     * 
     * @param position the position of the store (ignored for autonomous type)
     * @param tenges the initial amount of tenges in the store
     * @param type the store type: "normal", "autonomous", "fighter"
     */
    public void placeStore(int position, int tenges, String type) {
        try {
            if (tenges < 0) {
                lastActionSuccessful = false;
                lastActionMessage = "Error: The amount of tenges cannot be negative";
                return;
            }
            
            // Normalize type
            type = type.toLowerCase();
            
            // Create the appropriate store subclass using polymorphism
            Store store;
            int finalPosition = position;
            
            if ("autonomous".equals(type)) {
                // Get occupied positions
                ArrayList<Integer> occupiedPositions = new ArrayList<>();
                for (Store s : stores) {
                    occupiedPositions.add(s.getPosition());
                }
                
                store = new AutonomousStore(position, tenges, length, occupiedPositions);
                finalPosition = store.getPosition();
            }
            else {
                // For non-autonomous stores, validate the position
                if (position < 0 || position >= length) {
                    lastActionSuccessful = false;
                    lastActionMessage = "Error: Invalid position. Must be between 0 and " + (length-1);
                    return;
                }
                
                // Check if there's already a store at that position
                for (Store s : stores) {
                    if (s.getPosition() == position) {
                        lastActionSuccessful = false;
                        lastActionMessage = "Error: There is already a store at position " + position;
                        return;
                    }
                }
                
                // Create the appropriate store type
                if ("fighter".equals(type)) {
                    store = new FighterStore(position, tenges);
                } else {
                    store = new NormalStore(position, tenges);
                }
            }
            
            stores.add(store);
            
            // Get position on canvas and place the store
            int[] pos = positions.get(finalPosition);
            store.updateCanvasPosition(pos[0], pos[1]);
            
            // Only make visible if simulator is visible
            if (isVisible) {
                store.setVisible(true);
            } else {
                store.setVisible(false);
            }
            
            // Update progress bar
            updateProgressBar();
            
            lastActionSuccessful = true;
            String positionMessage = "autonomous".equals(type) && finalPosition != position ? 
                "position randomly chosen: " + finalPosition : "position " + finalPosition;
            lastActionMessage = "Store of type " + store.getType() + 
                               " successfully placed at " + positionMessage + 
                               " with " + tenges + " tenges";
            
        } catch (NumberFormatException e) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: Please enter valid numbers";
        }
    }
    
    /**
     * Places a robot on the route with a specific type.
     * Uses polymorphism to create the appropriate robot subclass.
     * 
     * @param position the initial position of the robot
     * @param type the robot type: "normal", "neverback", "tender", "lazy"
     */
    public void placeRobot(int position, String type) {
        try {
            if (position < 0 || position >= length) {
                lastActionSuccessful = false;
                lastActionMessage = "Error: Invalid position. Must be between 0 and " + (length-1);
                return;
            }
            
            // Check if there's already a robot at that position
            for (Robot r : robots) {
                if (r.getPosition() == position) {
                    lastActionSuccessful = false;
                    lastActionMessage = "Error: There is already a robot at position " + position;
                    return;
                }
            }
            
            // Create the appropriate robot subclass using polymorphism
            Robot robot;
            type = type.toLowerCase();
            
            switch (type) {
                case "neverback":
                    robot = new NeverbackRobot(position);
                    break;
                case "tender":
                    robot = new TenderRobot(position);
                    break;
                case "lazy":
                    robot = new LazyRobot(position);
                    break;
                case "normal":
                default:
                    robot = new NormalRobot(position);
                    break;
            }
            
            robots.add(robot);
            
            // Get position on canvas and place the robot
            int[] pos = positions.get(position);
            robot.updateCanvasPosition(pos[0], pos[1]);
            
            // Only make visible if simulator is visible
            if (isVisible) {
                robot.setVisible(true);
            } else {
                robot.setVisible(false);
            }
            
            // Update progress bar
            updateProgressBar();
            
            // Update robot with highest profit
            updateHighestProfitRobot();
            
            lastActionSuccessful = true;
            lastActionMessage = "Robot of type " + robot.getType() + 
                               " successfully placed at position " + position;
            
        } catch (NumberFormatException e) {
            lastActionSuccessful = false;
            lastActionMessage = "Error: Please enter a valid number for position";
        }
    }
}