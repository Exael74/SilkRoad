package silkRoad;

import java.util.Random;

/**
 * Normal store that allows any robot to take tenges.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 5.0
 */
public class NormalStore extends Store {
    private static final String[] AVAILABLE_COLORS = {
        "red", "blue", "green", "magenta"
    };
    
    /**
     * Constructor for NormalStore.
     * 
     * @param position store position on the route
     * @param tenges initial amount of tenges
     */
    public NormalStore(int position, int tenges) {
        super(position, tenges, selectRandomColor());
    }
    
    /**
     * Selects a random color for the store.
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
    public boolean canRobotTakeTenges(Robot robot) {
        return true; // Any robot can take tenges
    }
}
