package silkRoad;

import shapes.Circle;

/**
 * Abstract base class representing a robot that can move along the silk road route.
 * This class implements the common behavior for all robot types.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public abstract class Robot {
    protected int initialPosition;
    protected int currentPosition;
    protected String color;
    protected Circle circle;
    protected int totalProfit;
    protected int[] profitPerMove;
    protected int moveCount;
    protected boolean isBlinking;
    protected boolean isVisible;
    
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
        
        this.circle = new Circle();
        this.circle.changeSize(15);
        this.circle.changeColor(color);
        
        this.totalProfit = 0;
        this.profitPerMove = new int[100];
        this.moveCount = 0;
        this.isBlinking = false;
        this.isVisible = false;
    }
    
    /**
     * Returns the robot type.
     * Must be implemented by subclasses.
     * 
     * @return robot type string
     */
    public abstract String getType();
    
    /**
     * Checks if a robot can move in a specific direction.
     * Can be overridden by subclasses to implement movement restrictions.
     * 
     * @param moveDistance the movement distance (positive forward, negative backward)
     * @return true if the movement is valid for this robot type
     */
    public abstract boolean canMove(int moveDistance);
    
    /**
     * Calculates the profit this robot will obtain from collecting tenges.
     * Can be overridden by subclasses to implement different profit calculations.
     * 
     * @param storeTenges the tenges available at the store
     * @param distance the distance traveled to reach the store
     * @return the actual profit this robot will obtain
     */
    public abstract int calculateProfit(int storeTenges, int distance);
    
    /**
     * Returns the amount of tenges this robot will take from a store.
     * Can be overridden by subclasses to implement different collection behaviors.
     * 
     * @param storeTenges the tenges available at the store
     * @return the amount of tenges this robot will take
     */
    public abstract int getTengesToTake(int storeTenges);
    
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
        
        circle = new Circle();
        circle.changeSize(15);
        circle.changeColor(color);
        
        circle.moveHorizontal(x - 20 + 3);
        circle.moveVertical(y - 15 + 3);
        
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
        int[] history = new int[moveCount];
        System.arraycopy(profitPerMove, 0, history, 0, moveCount);
        return history;
    }
    
    /**
     * Resets the profit counter for this robot.
     */
    public void resetProfit() {
        this.totalProfit = 0;
        this.profitPerMove = new int[100];
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
     * Indicates if the robot is currently visible.
     * 
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }
}