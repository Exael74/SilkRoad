package silkRoad;

import shapes.Triangle;
import shapes.Rectangle;

/**
 * Abstract base class representing a store on the silk road route.
 * This class implements the common behavior for all store types.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public abstract class Store {
    protected int position;
    protected int tenges;
    protected int initialTenges;
    protected String color;
    protected Triangle triangle;
    protected Rectangle emptySquare;
    protected int emptyCount;
    protected boolean isEmpty;
    protected boolean isVisible;
    
    /**
     * Constructor for Store class objects.
     * 
     * @param position store position on the route
     * @param tenges amount of coins the store starts with
     * @param color store color
     */
    public Store(int position, int tenges, String color) {
        this.position = position;
        this.tenges = tenges;
        this.initialTenges = tenges;
        this.color = color;
        this.emptyCount = 0;
        this.isEmpty = (tenges == 0);
        this.isVisible = false;
        
        this.triangle = new Triangle();
        this.triangle.changeSize(15, 15);
        this.triangle.changeColor(this.color);
        
        this.emptySquare = new Rectangle();
        this.emptySquare.changeSize(15, 15);
        this.emptySquare.changeColor("black");
    }
    
    /**
     * Returns the store type.
     * Must be implemented by subclasses.
     * 
     * @return store type string
     */
    public abstract String getType();
    
    /**
     * Checks if a robot can take tenges from this store.
     * Can be overridden by subclasses to implement different access rules.
     * 
     * @param robot the robot attempting to take tenges
     * @return true if the robot can take tenges, false otherwise
     */
    public abstract boolean canRobotTakeTenges(Robot robot);
    
    /**
     * Makes the store visible on the canvas.
     */
    public void makeVisible() {
        if (isEmpty) {
            emptySquare.makeVisible();
        } else {
            triangle.makeVisible();
        }
        isVisible = true;
    }
    
    /**
     * Makes the store invisible on the canvas.
     */
    public void makeInvisible() {
        triangle.makeInvisible();
        emptySquare.makeInvisible();
        isVisible = false;
    }
    
    /**
     * Updates the store's position on the canvas.
     * 
     * @param x x coordinate on the canvas
     * @param y y coordinate on the canvas
     */
    public void updateCanvasPosition(int x, int y) {
        triangle.makeInvisible();
        emptySquare.makeInvisible();
        
        triangle = new Triangle();
        triangle.changeSize(15, 15);
        triangle.changeColor(this.color);
        
        emptySquare = new Rectangle();
        emptySquare.changeSize(15, 15);
        emptySquare.changeColor("black");
        
        triangle.moveHorizontal(x - 140 + 3);
        triangle.moveVertical(y - 15 + 3);
        
        emptySquare.moveHorizontal(x - 70 + 3);
        emptySquare.moveVertical(y - 15 + 3);
        
        if (isVisible) {
            if (isEmpty) {
                emptySquare.makeVisible();
            } else {
                triangle.makeVisible();
            }
        }
    }
    
    /**
     * Sets the store's visibility state.
     * 
     * @param visible true to make visible, false for invisible
     */
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        if (visible) {
            if (isEmpty) {
                emptySquare.makeVisible();
            } else {
                triangle.makeVisible();
            }
        } else {
            triangle.makeInvisible();
            emptySquare.makeInvisible();
        }
    }
    
    /**
     * Returns the store's position.
     * 
     * @return position on the route
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Sets the store's position.
     * Used by AutonomousStore to change its position.
     * 
     * @param position new position on the route
     */
    protected void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * Returns the quantity of tenges in the store.
     * 
     * @return quantity of tenges
     */
    public int getTenges() {
        return tenges;
    }
    
    /**
     * Sets the quantity of tenges in the store.
     * 
     * @param tenges new quantity of tenges
     */
    public void setTenges(int tenges) {
        if (this.tenges > 0 && tenges == 0) {
            this.emptyCount++;
            this.isEmpty = true;
            
            if (isVisible) {
                triangle.makeInvisible();
                emptySquare.makeVisible();
            }
        } else if (this.tenges == 0 && tenges > 0) {
            this.isEmpty = false;
            
            if (isVisible) {
                emptySquare.makeInvisible();
                triangle.makeVisible();
            }
        }
        
        this.tenges = tenges;
    }
    
    /**
     * Returns the initial quantity of tenges in the store.
     * 
     * @return initial quantity of tenges
     */
    public int getInitialTenges() {
        return initialTenges;
    }
    
    /**
     * Returns the store's color.
     * 
     * @return store color
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Resupplies the store with its initial tenges.
     */
    public void resupply() {
        boolean wasEmpty = (this.tenges == 0);
        this.tenges = this.initialTenges;
        
        if (wasEmpty && this.tenges > 0) {
            this.isEmpty = false;
            
            if (isVisible) {
                emptySquare.makeInvisible();
                triangle.makeVisible();
            }
        }
    }
    
    /**
     * Returns the number of times the store has been emptied.
     * 
     * @return counter of times empty
     */
    public int getEmptyCount() {
        return emptyCount;
    }
    
    /**
     * Resets the counter of times the store has been emptied.
     */
    public void resetEmptyCount() {
        this.emptyCount = 0;
    }
    
    /**
     * Checks if the store is empty.
     * 
     * @return true if the store is empty (no tenges)
     */
    public boolean isEmpty() {
        return isEmpty;
    }
    
    /**
     * Indicates if the store is currently visible.
     * 
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }
}