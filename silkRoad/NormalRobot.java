package silkRoad;

import java.util.Random;

/**
 * Normal robot that takes all tenges and can move in any direction.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class NormalRobot extends Robot {
    private static final String[] AVAILABLE_COLORS = {
        "red", "magenta", "pink", "orange", "cyan", "black"
    };
    
    /**
     * Constructor for NormalRobot.
     * 
     * @param position initial position of the robot
     */
    public NormalRobot(int position) {
        super(position, selectRandomColor());
    }
    
    /**
     * Constructor for NormalRobot with specific color.
     * 
     * @param position initial position of the robot
     * @param color specific color for the robot
     */
    public NormalRobot(int position, String color) {
        super(position, color);
    }
    
    /**
     * Selects a random color for the robot.
     * 
     * @return a random color string
     */
    private static String selectRandomColor() {
        Random random = new Random();
        return AVAILABLE_COLORS[random.nextInt(AVAILABLE_COLORS.length)];
    }
    
    @Override
    public String getType() {
        return "normal";
    }
    
    @Override
    public boolean canMove(int moveDistance) {
        return true; // Normal robots can move in any direction
    }
    
    @Override
    public int calculateProfit(int storeTenges, int distance) {
        return storeTenges - distance;
    }
    
    @Override
    public int getTengesToTake(int storeTenges) {
        return storeTenges; // Takes all tenges
    }
}