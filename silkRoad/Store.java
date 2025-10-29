package silkRoad;

import shapes.Triangle;
import shapes.Rectangle;
import java.util.Random;

/**
 * Represents a store on the silk road route.
 * @author Exael74 (Github User for Stiven Pardo)
 */
public class Store {
    private int position;
    private int tenges;
    private int initialTenges;
    private String color;
    private String type;  // Store type: "normal", "autonomous", "fighter"
    private Triangle triangle;
    private Rectangle emptySquare; // Represents the empty store
    private int emptyCount; // Counter of times the store has been emptied
    private boolean isEmpty; // Indicates if the store is empty
    private boolean isVisible; // Visibility state
    
    // Available colors for normal stores
    private static final String[] NORMAL_STORE_COLORS = {
        "red", "blue", "green", "magenta"
    };
    
    // Specific colors for special stores
    private static final String AUTONOMOUS_STORE_COLOR = "pink";
    private static final String FIGHTER_STORE_COLOR = "orange";
    
    /**
     * Constructor for Store class objects.
     * 
     * @param position store position on the route
     * @param tenges amount of coins the store starts with
     */
    public Store(int position, int tenges) {
        this(position, tenges, "normal"); // By default, type is "normal"
    }
    
    /**
     * Constructor for Store class objects with specific type.
     * 
     * @param position store position on the route
     * @param tenges amount of coins the store starts with
     * @param type store type: "normal", "autonomous", "fighter"
     */
    public Store(int position, int tenges, String type) {
        this.position = position;
        this.tenges = tenges;
        this.initialTenges = tenges;
        this.type = type.toLowerCase(); // Normalize to lowercase
        this.emptyCount = 0; // Initialize counter
        this.isEmpty = (tenges == 0); // Initialize state
        this.isVisible = false;
        
        // Assign color according to store type
        switch (this.type) {
            case "autonomous":
                this.color = AUTONOMOUS_STORE_COLOR;  // Fixed color for autonomous stores
                break;
            case "fighter":
                this.color = FIGHTER_STORE_COLOR;     // Fixed color for fighter stores
                break;
            default: // "normal" or other unknown types
                // Assign a random color from available colors for normal stores
                Random random = new Random();
                this.color = NORMAL_STORE_COLORS[random.nextInt(NORMAL_STORE_COLORS.length)];
                break;
        }
        
        // Create the triangle with assigned color
        this.triangle = new Triangle();
        this.triangle.changeSize(15, 15);
        this.triangle.changeColor(this.color);
        
        // Create the square to represent empty store
        this.emptySquare = new Rectangle();
        this.emptySquare.changeSize(15, 15);
        this.emptySquare.changeColor("black");
    }
    
    /**
     * Returns the store type.
     * 
     * @return store type ("normal", "autonomous", "fighter")
     */
    public String getType() {
        return type;
    }
    
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
        // Hide shapes
        triangle.makeInvisible();
        emptySquare.makeInvisible();
        
        // Create a new triangle to avoid problems with relative positions
        triangle = new Triangle();
        triangle.changeSize(15, 15);
        triangle.changeColor(this.color);
        
        // Create a new square for empty store
        emptySquare = new Rectangle();
        emptySquare.changeSize(15, 15);
        emptySquare.changeColor("black");
        
        // Move the triangle to the new position
        triangle.moveHorizontal(x - 140 + 3); // +3 to center
        triangle.moveVertical(y - 15 + 3);   // +3 to center
        
        // Move the square to the same position
        emptySquare.moveHorizontal(x - 70 + 3); // Adjust according to default position
        emptySquare.moveVertical(y - 15 + 3);
        
        // Visibility will now be controlled externally by SilkRoad
        if (isVisible) {
            if (isEmpty) {
                emptySquare.makeVisible();
            } else {
                triangle.makeVisible();
            }
        }
    }
    
    // The rest of the methods remain unchanged
    
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
        // If it had tenges and now becomes empty, increment counter
        if (this.tenges > 0 && tenges == 0) {
            this.emptyCount++;
            this.isEmpty = true;
            
            // Change visual representation if visible
            if (isVisible) {
                triangle.makeInvisible();
                emptySquare.makeVisible();
            }
        } 
        // If it was empty and now has tenges, return to original shape
        else if (this.tenges == 0 && tenges > 0) {
            this.isEmpty = false;
            
            // Change visual representation if visible
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
        
        // If the store was empty and now has tenges, change representation
        if (wasEmpty && this.tenges > 0) {
            this.isEmpty = false;
            
            // Update visualization if visible
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
    
    /**
     * Checks if a robot can take tenges from this store.
     * 
     * @param robot the robot attempting to take tenges
     * @return true if the robot can take tenges, false otherwise
     */
    public boolean canRobotTakeTenges(Robot robot) {
        // For "fighter" type stores, only robots with more profit can take tenges
        if ("fighter".equals(this.type)) {
            return robot.getTotalProfit() > this.tenges;
        }
        
        // Para otros tipos de tiendas, siempre pueden tomar tenges
        return true;
    }
}