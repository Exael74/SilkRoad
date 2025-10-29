package silkRoad;

import shapes.Circle;
import java.util.Random;

/**
 * Represents a robot that can move along the silk road route.
 * @author Exael74 (Github User for Stiven Pardo)
 */
public class Robot {
    private int initialPosition;
    private int currentPosition;
    private String color;
    private String type; // Robot type: "normal", "neverback", "tender"
    private Circle circle;
    private int totalProfit;
    private int[] profitPerMove;
    private int moveCount;
    private boolean isBlinking;
    private boolean isVisible;
    
    // Specific colors for special robot types
    private static final String NEVERBACK_ROBOT_COLOR = "blue";
    private static final String TENDER_ROBOT_COLOR = "green";
    
    /**
     * Constructor for Robot class objects.
     * 
     * @param position initial position of the robot on the route
     * @param color robot color
     */
    public Robot(int position, String color) {
        this.initialPosition = position;
        this.currentPosition = position;
        this.color = color;
        this.type = "normal"; // Default is normal type
        
        this.circle = new Circle();
        this.circle.changeSize(15); // Robot size
        this.circle.changeColor(color);
        
        // Variable initialization
        this.totalProfit = 0;
        this.profitPerMove = new int[100]; // Reasonable initial size
        this.moveCount = 0;
        this.isBlinking = false;
        this.isVisible = false;
    }
    
    /**
     * Constructor for Robot class objects with specific type.
     * 
     * @param position initial position of the robot on the route
     * @param type robot type: "normal", "neverback", "tender"
     * @param isTypeSpecified parameter to distinguish this constructor
     */
    public Robot(int position, String type, boolean isTypeSpecified) {
        this.initialPosition = position;
        this.currentPosition = position;
        this.type = type.toLowerCase(); // Normalize to lowercase
        
        // Assign color according to type
        switch (this.type) {
            case "neverback":
                this.color = NEVERBACK_ROBOT_COLOR;
                break;
            case "tender":
                this.color = TENDER_ROBOT_COLOR;
                break;
            default: // "normal" or other unknown types
                // Assign a random color for normal robots
                String[] availableColors = {"red", "magenta", "pink", "orange", "cyan", "black"};
                Random random = new Random();
                this.color = availableColors[random.nextInt(availableColors.length)];
                this.type = "normal"; // Ensure it's normal if type is not recognized
                break;
        }
        
        this.circle = new Circle();
        this.circle.changeSize(15); // Robot size
        this.circle.changeColor(this.color);
        
        // Variable initialization
        this.totalProfit = 0;
        this.profitPerMove = new int[100]; // Reasonable initial size
        this.moveCount = 0;
        this.isBlinking = false;
        this.isVisible = false;
    }
    
    /**
     * Returns the robot type.
     * 
     * @return robot type ("normal", "neverback", "tender")
     */
    public String getType() {
        return type;
    }
    
    /**
     * Checks if a robot can move in a specific direction.
     * 
     * @param moveDistance the movement distance (positive forward, negative backward)
     * @return true if the movement is valid for this robot type
     */
    public boolean canMove(int moveDistance) {
        // Neverback robots cannot move backwards
        if ("neverback".equals(this.type) && moveDistance < 0) {
            return false;
        }
        return true;
    }
    
    /**
     * Makes the robot visible on the canvas.
     */
    public void makeVisible() {
        circle.makeVisible();
        isVisible = true;
    }
    
    /**
     * Makes the robot invisible on the canvas.
     */
    public void makeInvisible() {
        circle.makeInvisible();
        isVisible = false;
    }
    
    /**
     * Updates the robot's position on the canvas.
     * 
     * @param x x coordinate on the canvas
     * @param y y coordinate on the canvas
     */
    public void updateCanvasPosition(int x, int y) {
        circle.makeInvisible();
        
        // Create a new circle to avoid problems with relative positions
        circle = new Circle();
        circle.changeSize(15);
        circle.changeColor(color);
        
        // Move the circle to the new position (adjusting for default values)
        circle.moveHorizontal(x - 20 + 3); // +3 to center on the square
        circle.moveVertical(y - 15 + 3);   // +3 to center on the square
        
        // Visibility will now be controlled externally by SilkRoad
        if (isVisible && !isBlinking) {
            circle.makeVisible();
        }
    }
    
    /**
     * Updates the robot's logical position on the route.
     * 
     * @param newPosition new position on the route
     */
    public void updatePosition(int newPosition) {
        this.currentPosition = newPosition;
    }
    
    /**
     * Registers a profit for this robot.
     * 
     * @param profit profit obtained
     * @param position position where profit was obtained
     */
    public void addProfit(int profit, int position) {
        // For tender robots, only take half the profit
        if ("tender".equals(this.type)) {
            profit = profit / 2; // Half profit
        }
        
        this.totalProfit += profit;
        this.profitPerMove[moveCount] = profit;
        moveCount++;
    }
    
    /**
     * Returns the robot's current position.
     * 
     * @return current position on the route
     */
    public int getPosition() {
        return currentPosition;
    }
    
    /**
     * Returns the robot's initial position.
     * 
     * @return initial position on the route
     */
    public int getInitialPosition() {
        return initialPosition;
    }
    
    /**
     * Returns the robot's color.
     * 
     * @return robot color
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Returns the total profit accumulated by this robot.
     * 
     * @return total accumulated profit
     */
    public int getTotalProfit() {
        return totalProfit;
    }
    
    /**
     * Returns the profit history per movement.
     * 
     * @return array with profits for each movement
     */
    public int[] getProfitHistory() {
        // Create an array of the exact size of movements made
        int[] history = new int[moveCount];
        System.arraycopy(profitPerMove, 0, history, 0, moveCount);
        return history;
    }
    
    /**
     * Resets the profit counter for this robot.
     */
    public void resetProfit() {
        this.totalProfit = 0;
        this.profitPerMove = new int[100]; // Reasonable initial size
        this.moveCount = 0;
    }
    
    /**
     * Starts/stops the robot's blinking.
     * 
     * @param blinking true to start blinking, false to stop it
     */
    public void setBlinking(boolean blinking) {
        this.isBlinking = blinking;
        if (!blinking && isVisible) {
            makeVisible();
        }
    }
    
    /**
     * Performs a blink cycle.
     */
    public void blink() {
        if (isBlinking) {
            if (isVisible) {
                circle.makeInvisible();
                isVisible = false;
            } else {
                circle.makeVisible();
                isVisible = true;
            }
        }
    }
    
    /**
     * Sets the robot's visibility state.
     * 
     * @param visible true to make visible, false for invisible
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (visible && !isBlinking) {
            circle.makeVisible();
        } else if (!visible) {
            circle.makeInvisible();
        }
    }
    
    /**
     * Calculates the expected benefit of moving this robot to a specific position.
     * 
     * @param targetPosition target position
     * @param storeTenges tenges available at the store in that position
     * @return expected benefit (can be negative)
     */
    public int calculateExpectedProfit(int targetPosition, int storeTenges) {
        int distance = calculateDistance(initialPosition, targetPosition);
        int profit = storeTenges - distance;
        
        // For tender robots, only consider half the profit
        if ("tender".equals(this.type)) {
            profit = profit / 2;
        }
        
        return profit;
    }
    
    /**
     * Auxiliary method to calculate distance between two positions on the route.
     * 
     * @param length total route length
     */
    public int calculateDistance(int startPos, int endPos) {
        int directDistance = Math.abs(endPos - startPos);
        // We can't access road.getLength(), so this will have to be adjusted by SilkRoad
        return directDistance;
    }
    
    /**
     * Indicates if the robot is currently visible.
     * 
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }
}