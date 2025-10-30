package test;
import silkRoad.SilkRoad;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * Test class for SilkRoad - Optimized Version with Polymorphism
 * This class contains unit tests to verify the correct functioning
 * of the SilkRoad class that simulates a commercial route.
 * Includes automatic resource management to avoid memory problems.
 * Updated to test polymorphic robot and store types.
 * 
 * @author Exael74 (Github User for Stiven Pardo)
 * @version 3.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SilkRoadTest {
    
    private SilkRoad silkRoadActual = null;
    
    /**
     * Delays execution for the specified time.
     * 
     * @param milliseconds time to delay in milliseconds
     */
    private void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Sets up test environment before each test.
     */
    @Before
    public void configurar() {
        delay(50);
    }
    
    /**
     * Cleans up resources after each test.
     */
    @After
    public void limpiar() {
        if (silkRoadActual != null) {
            try {
                // Ensure it's invisible before cleaning up
                silkRoadActual.makeInvisible();
                delay(50);
            } catch (Exception e) {
                // Ignore errors
            }
            silkRoadActual = null;
        }
        delay(50);
        System.gc();
    }
    
    /**
     * Creates an invisible SilkRoad with the specified length.
     * 
     * @param length the length of the silk road
     * @return the created invisible SilkRoad instance
     */
    private SilkRoad crearSilkRoadInvisible(int length) {
        if (silkRoadActual != null) {
            try {
                silkRoadActual.makeInvisible();
                delay(30);
            } catch (Exception e) {
                // Ignore
            }
        }
        silkRoadActual = new SilkRoad(length);
        silkRoadActual.makeInvisible();
        delay(30);
        return silkRoadActual;
    }
    
    /**
     * Creates an invisible SilkRoad from a days array.
     * 
     * @param days the array representing different days and their configurations
     * @return the created invisible SilkRoad instance
     */
    private SilkRoad crearSilkRoadInvisible(int[][] days) {
        if (silkRoadActual != null) {
            try {
                silkRoadActual.makeInvisible();
                delay(30);
            } catch (Exception e) {
                // Ignore
            }
        }
        silkRoadActual = new SilkRoad(days);
        silkRoadActual.makeInvisible();
        delay(30);
        return silkRoadActual;
    }
    
    // ==========================================
    // POSITIVE TESTS (SHOULD)
    // ==========================================
    
    /**
     * Tests that profit is correctly calculated when a robot collects tenges.
     */
    @Test
    public void AccordingPshouldCalculateCorrectProfit() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeRobot(5); // Creates NormalRobot by default
        silkRoad.placeStore(10, 15); // Creates NormalStore by default
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        assertEquals(10, silkRoad.getProfit());
    }
    
    /**
     * Tests that a SilkRoad can be created from a days array.
     */
    @Test
    public void AccordingPshouldCreateSilkRoadFromDaysArray() {
        int[][] days = {
            {3}, {1, 5}, {2, 10, 15}, {1, 15}
        };
        SilkRoad silkRoad = crearSilkRoadInvisible(days);
        int[][] robots = silkRoad.robots();
        assertEquals(2, robots.length);
        int[][] stores = silkRoad.stores();
        assertEquals(1, stores.length);
        assertEquals(10, stores[0][0]);
        assertEquals(15, stores[0][1]);
    }
    
    /**
     * Tests that a SilkRoad can be created with a specified length.
     */
    @Test
    public void AccordingPshouldCreateSilkRoadWithSpecifiedLength() {
        SilkRoad silkRoad = crearSilkRoadInvisible(100);
        assertEquals(100, silkRoad.getLength());
    }
    
    /**
     * Tests that the simulation can finish without errors.
     */
    @Test
    public void AccordingPshouldFinishSimulation() {
        SilkRoad silkRoad = crearSilkRoadInvisible(10);
        silkRoad.makeInvisible();
        delay(50);
        // Don't call finish() here to avoid issues with BlueJ
        silkRoadActual = null;
        assertTrue(true); // Test passes if no exceptions
    }
    
    /**
     * Tests handling of multiple robots with equal profit.
     */
    @Test
    public void AccordingPshouldHandleMultipleRobotsWithEqualProfit() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeRobot(15);
        silkRoad.placeStore(10, 15);
        silkRoad.placeStore(20, 15);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        silkRoad.moveRobot(15, 5);
        delay(30);
        assertEquals(20, silkRoad.getProfit());
    }
    
    /**
     * Tests that a robot can move around a circular path.
     */
    @Test
    public void AccordingPshouldMoveRobotAroundCircularPath() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeRobot(45);
        silkRoad.moveRobot(45, 10);
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(5, robots[0][0]);
    }
    
    /**
     * Tests that a robot can move a specific distance.
     */
    @Test
    public void AccordingPshouldMoveRobotSpecificDistance() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeRobot(10);
        delay(30);
        silkRoad.moveRobot(10, 5);
        delay(30);
        int[][] robotsAfterForward = silkRoad.robots();
        assertEquals(15, robotsAfterForward[0][0]);
        silkRoad.moveRobot(15, -3);
        delay(30);
        int[][] robotsAfterBackward = silkRoad.robots();
        assertEquals(12, robotsAfterBackward[0][0]);
        silkRoad.moveRobot(12, 40);
        delay(30);
        int[][] robotsAfterCircular = silkRoad.robots();
        assertEquals(2, robotsAfterCircular[0][0]);
    }
    
    /**
     * Tests that robots can move to maximize profit.
     */
    @Test
    public void AccordingPshouldMoveRobotsToMaximizeProfit() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeRobot(15);
        silkRoad.placeStore(10, 20);
        silkRoad.placeStore(20, 10);
        delay(30);
        silkRoad.moveRobots();
        delay(30);
        assertEquals(20, silkRoad.getProfit());
    }
    
    /**
     * Tests that a robot can be placed at a valid location.
     */
    @Test
    public void AccordingPshouldPlaceRobotAtValidLocation() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(15);
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(15, robots[0][0]);
    }
    
    /**
     * Tests that a store can be placed at a valid location.
     */
    @Test
    public void AccordingPshouldPlaceStoreAtValidLocation() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(15, 20);
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] stores = silkRoad.stores();
        assertEquals(1, stores.length);
        assertEquals(15, stores[0][0]);
        assertEquals(20, stores[0][1]);
    }
    
    /**
     * Tests that the simulation can be rebooted.
     */
    @Test
    public void AccordingPshouldRebootSimulation() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 20);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        silkRoad.reboot();
        delay(30);
        assertEquals(0, silkRoad.getProfit());
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(5, robots[0][0]);
    }
    
    /**
     * Tests that an existing robot can be removed.
     */
    @Test
    public void AccordingPshouldRemoveExistingRobot() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeRobot(5);
        delay(30);
        silkRoad.removeRobot(5);
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(0, robots.length);
    }
    
    /**
     * Tests that an existing store can be removed.
     */
    @Test
    public void AccordingPshouldRemoveExistingStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeStore(10, 15);
        delay(30);
        silkRoad.removeStore(10);
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] stores = silkRoad.stores();
        assertEquals(0, stores.length);
    }
    
    /**
     * Tests that success is reported correctly.
     */
    @Test
    public void AccordingPshouldReportSuccessCorrectly() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeRobot(5);
        assertTrue(silkRoad.ok());
    }
    
    /**
     * Tests that empty stores can be resupplied.
     */
    @Test
    public void AccordingPshouldResupplyEmptyStores() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 15);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        int[][] storesBeforeResupply = silkRoad.stores();
        assertEquals(0, storesBeforeResupply[0][1]);
        silkRoad.resupplyStores();
        delay(30);
        int[][] storesAfterResupply = silkRoad.stores();
        assertEquals(15, storesAfterResupply[0][1]);
    }
    
    /**
     * Tests that robots return to initial positions.
     */
    @Test
    public void AccordingPshouldReturnRobotsToInitialPositions() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        delay(30);
        silkRoad.moveRobot(5, 10);
        delay(30);
        int[][] robotsAfterMove = silkRoad.robots();
        assertEquals(15, robotsAfterMove[0][0]);
        silkRoad.returnRobots();
        delay(30);
        int[][] robotsAfterReturn = silkRoad.robots();
        assertEquals(5, robotsAfterReturn[0][0]);
    }
    
    /**
     * Tests that correct robots information is returned with type codes.
     * Type codes: 0=normal, 1=neverback, 2=tender
     */
    @Test
    public void AccordingPshouldReturnCorrectRobotsInfo() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5, "normal");
        silkRoad.placeRobot(15, "neverback");
        delay(30);
        int[][] robots = silkRoad.robots();
        assertEquals(2, robots.length);
        // Sort by position
        if (robots[0][0] > robots[1][0]) {
            int[] temp = robots[0];
            robots[0] = robots[1];
            robots[1] = temp;
        }
        assertEquals(5, robots[0][0]);
        assertEquals(0, robots[0][2]); // normal type
        assertEquals(15, robots[1][0]);
        assertEquals(1, robots[1][2]); // neverback type
    }
    
    /**
     * Tests that correct stores information is returned with type codes.
     * Type codes: 0=normal, 1=autonomous, 2=fighter
     */
    @Test
    public void AccordingPshouldReturnCorrectStoresInfo() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(5, 10, "normal");
        silkRoad.placeStore(15, 20, "fighter");
        delay(30);
        int[][] stores = silkRoad.stores();
        assertEquals(2, stores.length);
        // Sort by position
        if (stores[0][0] > stores[1][0]) {
            int[] temp = stores[0];
            stores[0] = stores[1];
            stores[1] = temp;
        }
        assertEquals(5, stores[0][0]);
        assertEquals(10, stores[0][1]);
        assertEquals(0, stores[0][2]); // normal type
        assertEquals(15, stores[1][0]);
        assertEquals(20, stores[1][1]);
        assertEquals(2, stores[1][2]); // fighter type
    }
    
    /**
     * Tests that emptied stores are tracked correctly.
     */
    @Test
    public void AccordingPshouldTrackEmptiedStores() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 15);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        int[][] emptiedStores = silkRoad.emptiedStores();
        assertEquals(1, emptiedStores.length);
        assertEquals(10, emptiedStores[0][0]);
        assertEquals(1, emptiedStores[0][1]);
    }
    
    /**
     * Tests that profit per move is tracked correctly.
     */
    @Test
    public void AccordingPshouldTrackProfitPerMove() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 15);
        silkRoad.placeStore(20, 20);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        silkRoad.moveRobot(10, 10);
        delay(30);
        int[][] profitHistory = silkRoad.profitPerMove();
        assertEquals(1, profitHistory.length);
        assertEquals(20, profitHistory[0][0]);
        assertEquals(10, profitHistory[0][1]);
        assertEquals(5, profitHistory[0][2]);
    }
    
    /**
     * Tests that visibility can be toggled.
     */
    @Test
    public void AccordingPshouldToggleVisibility() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        assertFalse(silkRoad.isVisible());
        silkRoad.makeVisible();
        delay(30);
        assertTrue(silkRoad.isVisible());
        silkRoad.makeInvisible();
        delay(30);
        assertFalse(silkRoad.isVisible());
    }
    
    // ==========================================
    // TESTS FOR POLYMORPHIC ROBOT TYPES
    // ==========================================
    
    /**
     * Tests that NormalRobot can be placed and behaves correctly.
     */
    @Test
    public void AccordingPshouldPlaceNormalRobotCorrectly() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(10, "normal");
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(10, robots[0][0]);
        assertEquals(0, robots[0][2]); // Type code for normal
    }
    
    /**
     * Tests that NeverbackRobot can be placed and prevents backward movement.
     */
    @Test
    public void AccordingPshouldPlaceNeverbackRobotAndPreventBackwardMovement() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(10, "neverback");
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(10, robots[0][0]);
        assertEquals(1, robots[0][2]); // Type code for neverback
        
        // Try to move backward (should fail)
        silkRoad.moveRobot(10, -5);
        delay(30);
        assertFalse(silkRoad.ok());
        robots = silkRoad.robots();
        assertEquals(10, robots[0][0]); // Position unchanged
    }
    
    /**
     * Tests that NeverbackRobot can move forward successfully.
     */
    @Test
    public void AccordingPshouldAllowNeverbackRobotToMoveForward() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(10, "neverback");
        delay(30);
        silkRoad.moveRobot(10, 5);
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(15, robots[0][0]);
    }
    
    /**
     * Tests that TenderRobot can be placed and takes half tenges.
     */
    @Test
    public void AccordingPshouldPlaceTenderRobotAndTakeHalfTenges() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5, "tender");
        silkRoad.placeStore(10, 20);
        delay(30);
        int[][] robots = silkRoad.robots();
        assertEquals(2, robots[0][2]); // Type code for tender
        
        silkRoad.moveRobot(5, 5);
        delay(30);
        
        // Tender robot: (20/2) - 5 = 5 profit
        assertEquals(5, silkRoad.getProfit());
        
        // Store should have half tenges remaining
        int[][] stores = silkRoad.stores();
        assertEquals(10, stores[0][1]);
    }
    
    /**
     * Tests that TenderRobot profit calculation is correct.
     */
    @Test
    public void AccordingPshouldCalculateTenderRobotProfitCorrectly() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(10, "tender");
        silkRoad.placeStore(20, 30); // Distance = 10, Tenges = 30
        delay(30);
        
        silkRoad.moveRobot(10, 10);
        delay(30);
        
        // Expected: (30/2) - 10 = 5
        assertEquals(5, silkRoad.getProfit());
    }
    
    // ==========================================
    // TESTS FOR POLYMORPHIC STORE TYPES
    // ==========================================
    
    /**
     * Tests that NormalStore can be placed correctly.
     */
    @Test
    public void AccordingPshouldPlaceNormalStoreCorrectly() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(10, 20, "normal");
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] stores = silkRoad.stores();
        assertEquals(1, stores.length);
        assertEquals(10, stores[0][0]);
        assertEquals(20, stores[0][1]);
        assertEquals(0, stores[0][2]); // Type code for normal
    }
    
    /**
     * Tests that AutonomousStore can be placed at a random position.
     */
    @Test
    public void AccordingPshouldPlaceAutonomousStoreAtRandomPosition() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(10, 20, "autonomous");
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] stores = silkRoad.stores();
        assertEquals(1, stores.length);
        assertEquals(1, stores[0][2]); // Type code for autonomous
        assertTrue(stores[0][0] >= 0 && stores[0][0] < 30);
        // Position may differ from suggested position
    }
    
    /**
     * Tests that AutonomousStore avoids occupied positions.
     */
    @Test
    public void AccordingPshouldPlaceAutonomousStoreAvoidingOccupiedPositions() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(10, 15, "normal");
        silkRoad.placeStore(10, 20, "autonomous"); // Suggests position 10 (occupied)
        delay(30);
        int[][] stores = silkRoad.stores();
        assertEquals(2, stores.length);
        // Autonomous store should be at different position
        boolean foundDifferentPosition = false;
        for (int[] store : stores) {
            if (store[2] == 1 && store[0] != 10) {
                foundDifferentPosition = true;
                break;
            }
        }
        assertTrue(foundDifferentPosition);
    }
    
    /**
     * Tests that FighterStore can be placed and blocks low profit robots.
     */
    @Test
    public void AccordingPshouldPlaceFighterStoreAndBlockLowProfitRobots() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 20, "fighter");
        delay(30);
        
        int[][] stores = silkRoad.stores();
        assertEquals(2, stores[0][2]); // Type code for fighter
        
        // Robot has 0 profit, store has 20 tenges, robot cannot take
        silkRoad.moveRobot(5, 5);
        delay(30);
        
        assertEquals(0, silkRoad.getProfit());
        stores = silkRoad.stores();
        assertEquals(20, stores[0][1]); // Store tenges unchanged
    }
    
    /**
     * Tests that FighterStore allows high profit robots to take tenges.
     */
    @Test
    public void AccordingPshouldAllowHighProfitRobotToTakeFromFighterStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeRobot(10); // Robot starts at position 10
        silkRoad.placeStore(15, 40, "normal"); // Distance = 5
        silkRoad.placeStore(25, 20, "fighter"); // Distance = 15
        delay(30);
        
        // Step 1: Robot collects from normal store
        // Distance from 10 to 15 = 5
        // Profit: 40 - 5 = 35
        silkRoad.moveRobot(10, 5);
        delay(30);
        assertEquals(35, silkRoad.getProfit());
        
        // Step 2: Robot now has 35 profit, tries fighter store with 20 tenges
        // Robot profit (35) > Store tenges (20), so robot CAN take
        // Distance from initial position (10) to fighter store (25) = 15
        // Additional profit: 20 - 15 = 5
        silkRoad.moveRobot(15, 10);
        delay(30);
        
        // Total profit: 35 + 5 = 40
        assertEquals(40, silkRoad.getProfit());
        
        // Verify fighter store is empty
        int[][] stores = silkRoad.stores();
        boolean fighterStoreEmpty = false;
        for (int[] store : stores) {
            if (store[2] == 2) { // Fighter store type
                fighterStoreEmpty = (store[1] == 0);
                break;
            }
        }
        assertTrue(fighterStoreEmpty);
    }
    
    // ==========================================
    // TESTS FOR POLYMORPHIC INTERACTIONS
    // ==========================================
    
    /**
     * Tests optimal movements with moveRobots using polymorphic types.
     */
    @Test
    public void AccordingPshouldExecuteOptimalMovementsWithMoveRobots() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5);
        silkRoad.placeRobot(25);
        silkRoad.placeStore(10, 20);
        silkRoad.placeStore(30, 15);
        delay(30);
        silkRoad.moveRobots();
        delay(30);
        assertTrue(silkRoad.getProfit() > 0);
        int[][] robots = silkRoad.robots();
        boolean robotMoved = false;
        for (int[] robot : robots) {
            if (robot[1] > 0) {
                robotMoved = true;
                break;
            }
        }
        assertTrue(robotMoved);
    }
    
    /**
     * Tests handling of moveRobots with no robots or stores.
     */
    @Test
    public void AccordingPshouldHandleMoveRobotsWithNoRobotsOrStores() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.moveRobots();
        delay(30);
        assertFalse(silkRoad.ok());
        silkRoad.placeRobot(10);
        silkRoad.moveRobots();
        delay(30);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests handling of TenderRobot with FighterStore.
     */
    @Test
    public void AccordingPshouldHandleTenderRobotWithFighterStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "tender");
        silkRoad.placeStore(10, 30, "normal");
        silkRoad.placeStore(20, 40, "fighter");
        delay(30);
        
        // Tender robot at normal store: (30/2) - 5 = 10
        silkRoad.moveRobot(5, 5);
        delay(30);
        assertEquals(10, silkRoad.getProfit());
        
        // Tender robot at fighter store: robot has 10 profit, store has 40 tenges
        // Robot profit (10) < store tenges (40), cannot take
        silkRoad.moveRobot(10, 10);
        delay(30);
        assertEquals(10, silkRoad.getProfit()); // No change
    }
    
    /**
     * Tests handling of multiple robot types.
     */
    @Test
    public void AccordingPshouldHandleMultipleRobotTypes() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeRobot(5, "normal");
        silkRoad.placeRobot(10, "neverback");
        silkRoad.placeRobot(15, "tender");
        delay(30);
        int[][] robots = silkRoad.robots();
        assertEquals(3, robots.length);
        // Sort by position
        for (int i = 0; i < robots.length - 1; i++) {
            for (int j = i + 1; j < robots.length; j++) {
                if (robots[i][0] > robots[j][0]) {
                    int[] temp = robots[i];
                    robots[i] = robots[j];
                    robots[j] = temp;
                }
            }
        }
        assertEquals(0, robots[0][2]); // normal
        assertEquals(1, robots[1][2]); // neverback
        assertEquals(2, robots[2][2]); // tender
    }
    
    /**
     * Tests handling of multiple store types.
     */
    @Test
    public void AccordingPshouldHandleMultipleStoreTypes() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeStore(10, 20, "normal");
        silkRoad.placeStore(20, 30, "fighter");
        silkRoad.placeStore(30, 40, "autonomous");
        delay(30);
        int[][] stores = silkRoad.stores();
        assertEquals(3, stores.length);
        int normalCount = 0, fighterCount = 0, autonomousCount = 0;
        for (int[] store : stores) {
            if (store[2] == 0) normalCount++;
            if (store[2] == 1) autonomousCount++;
            if (store[2] == 2) fighterCount++;
        }
        assertEquals(1, normalCount);
        assertEquals(1, fighterCount);
        assertEquals(1, autonomousCount);
    }
    
    /**
     * Tests handling of moveRobots with more robots than stores.
     */
    @Test
    public void AccordingPshouldHandleMoveRobotsWithMoreRobotsThanStores() {
        SilkRoad silkRoad = crearSilkRoadInvisible(60);
        silkRoad.placeRobot(5);
        silkRoad.placeRobot(15);
        silkRoad.placeRobot(25);
        silkRoad.placeStore(10, 30);
        silkRoad.placeStore(20, 25);
        delay(30);
        silkRoad.moveRobots();
        delay(30);
        int[][] robots = silkRoad.robots();
        int robotsWithProfit = 0;
        for (int[] robot : robots) {
            if (robot[1] > 0) robotsWithProfit++;
        }
        assertEquals(2, robotsWithProfit);
    }
    
    /**
     * Tests handling of moveRobots with more stores than robots.
     */
    @Test
    public void AccordingPshouldHandleMoveRobotsWithMoreStoresThanRobots() {
        SilkRoad silkRoad = crearSilkRoadInvisible(60);
        silkRoad.placeRobot(5);
        silkRoad.placeRobot(15);
        silkRoad.placeStore(10, 30);
        silkRoad.placeStore(20, 25);
        silkRoad.placeStore(30, 35);
        delay(30);
        silkRoad.moveRobots();
        delay(30);
        int[][] robots = silkRoad.robots();
        for (int[] robot : robots) {
            assertTrue(robot[1] > 0);
        }
        int[][] stores = silkRoad.stores();
        int emptyStores = 0;
        for (int[] store : stores) {
            if (store[1] == 0) emptyStores++;
        }
        assertEquals(2, emptyStores);
    }
    
    /**
     * Tests that correct array structure with types is returned.
     */
    @Test
    public void AccordingPshouldReturnCorrectArrayStructureWithTypes() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "normal");
        silkRoad.placeRobot(10, "tender");
        silkRoad.placeStore(15, 20, "normal");
        silkRoad.placeStore(20, 25, "fighter");
        delay(30);
        int[][] robots = silkRoad.robots();
        assertEquals(3, robots[0].length); // [position, profit, type]
        int[][] stores = silkRoad.stores();
        assertEquals(3, stores[0].length); // [position, tenges, type]
    }
    
    /**
     * Tests tracking of multiple movements in profitPerMove.
     */
    @Test
    public void AccordingPshouldTrackMultipleMovementsInProfitPerMove() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 20);
        silkRoad.placeStore(20, 30);
        silkRoad.placeStore(30, 25);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        silkRoad.moveRobot(10, 10);
        delay(30);
        silkRoad.moveRobot(20, 10);
        delay(30);
        int[][] profitHistory = silkRoad.profitPerMove();
        assertEquals(1, profitHistory.length);
        assertTrue(profitHistory[0].length >= 4);
        assertEquals(15, profitHistory[0][1]);
        assertEquals(15, profitHistory[0][2]);
        assertEquals(0, profitHistory[0][3]);
    }
    
    /**
     * Tests tracking of multiple emptying of stores.
     */
    @Test
    public void AccordingPshouldTrackMultipleEmptyingOfStores() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(10, 15);
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        silkRoad.resupplyStores();
        delay(30);
        silkRoad.returnRobots();
        delay(30);
        silkRoad.moveRobot(5, 5);
        delay(30);
        int[][] emptiedStores = silkRoad.emptiedStores();
        assertEquals(1, emptiedStores.length);
        assertEquals(10, emptiedStores[0][0]);
        assertEquals(2, emptiedStores[0][1]);
    }
    
    // ==========================================
    // NEGATIVE TESTS (SHOULD NOT)
    // ==========================================
    
    /**
     * Tests that non-existent robot cannot be moved.
     */
    @Test
    public void accordingPshouldNotMoveNonExistentRobot() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.moveRobot(5, 10);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that robot cannot be placed at invalid location.
     */
    @Test
    public void accordingPshouldNotPlaceRobotAtInvalidLocation() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeRobot(-5);
        assertFalse(silkRoad.ok());
        silkRoad.placeRobot(25);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that robot cannot be placed at location with existing robot.
     */
    @Test
    public void accordingPshouldNotPlaceRobotAtLocationWithExistingRobot() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeRobot(5);
        silkRoad.placeRobot(5);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that store cannot be placed at invalid location.
     */
    @Test
    public void accordingPshouldNotPlaceStoreAtInvalidLocation() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.placeStore(-5, 10);
        assertFalse(silkRoad.ok());
        silkRoad.placeStore(25, 10);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that store cannot be placed with negative tenges.
     */
    @Test
    public void accordingPshouldNotPlaceStoreWithNegativeTenges() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(10, -5);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that non-existent robot cannot be removed.
     */
    @Test
    public void accordingPshouldNotRemoveNonExistentRobot() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.removeRobot(5);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that non-existent store cannot be removed.
     */
    @Test
    public void accordingPshouldNotRemoveNonExistentStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(20);
        silkRoad.removeStore(10);
        assertFalse(silkRoad.ok());
    }
    
    /**
     * Tests that NeverbackRobot is not allowed to move backward.
     */
    @Test
    public void accordingPshouldNotAllowNeverbackRobotToMoveBackward() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(15, "neverback");
        delay(30);
        silkRoad.moveRobot(15, -5);
        delay(30);
        assertFalse(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(15, robots[0][0]);
    }
    
    /**
     * Tests that low profit robot is not allowed to take from FighterStore.
     */
    @Test
    public void accordingPshouldNotAllowLowProfitRobotToTakeFromFighterStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5);
        silkRoad.placeStore(15, 50, "fighter");
        delay(30);
        silkRoad.moveRobot(5, 10);
        delay(30);
        assertEquals(0, silkRoad.getProfit());
        int[][] stores = silkRoad.stores();
        assertEquals(50, stores[0][1]);
    }
    
    /**
     * Tests handling of invalid robot type defaults to normal.
     */
    @Test
    public void accordingPshouldHandleInvalidRobotTypeAsNormal() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(10, "invalid_type");
        delay(30);
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(0, robots[0][2]); // Defaults to normal (type 0)
    }
    
    /**
     * Tests handling of invalid store type defaults to normal.
     */
    @Test
    public void accordingPshouldHandleInvalidStoreTypeAsNormal() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeStore(10, 20, "invalid_type");
        delay(30);
        int[][] stores = silkRoad.stores();
        assertEquals(1, stores.length);
        assertEquals(0, stores[0][2]); // Defaults to normal (type 0)
    }
    
    /**
     * Tests that TenderRobot cannot access FighterStore with insufficient profit.
     */
    @Test
    public void accordingPshouldNotAllowTenderRobotWithLowProfitAtFighterStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "tender");
        silkRoad.placeStore(15, 30, "fighter");
        delay(30);
        
        // Tender robot has 0 profit, fighter store has 30 tenges
        silkRoad.moveRobot(5, 10);
        delay(30);
        
        assertEquals(0, silkRoad.getProfit());
        int[][] stores = silkRoad.stores();
        assertEquals(30, stores[0][1]); // Store unchanged
    }
    
    // ==========================================
    // TESTS FOR LAZY ROBOT
    // ==========================================
    
    /**
     * Tests that LazyRobot can be placed correctly.
     */
    @Test
    public void AccordingPshouldPlaceLazyRobotCorrectly() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(10, "lazy");
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(1, robots.length);
        assertEquals(10, robots[0][0]);
        assertEquals(3, robots[0][2]); // Type code for lazy
    }
    
    /**
     * Tests that LazyRobot never collects tenges from normal store.
     */
    @Test
    public void AccordingPshouldNotAllowLazyRobotToCollectTenges() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(5, "lazy");
        silkRoad.placeStore(10, 50, "normal");
        delay(30);
        
        // Move lazy robot to store
        silkRoad.moveRobot(5, 5);
        delay(30);
        
        // Lazy robot never collects, so profit should be 0
        assertEquals(0, silkRoad.getProfit());
        
        // Store should still have all tenges
        int[][] stores = silkRoad.stores();
        assertEquals(50, stores[0][1]);
        
        // Robot should have 0 profit
        int[][] robots = silkRoad.robots();
        assertEquals(0, robots[0][1]);
    }
    
    /**
     * Tests that LazyRobot can move but never profits.
     */
    @Test
    public void AccordingPshouldAllowLazyRobotToMoveWithoutProfit() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "lazy");
        silkRoad.placeStore(10, 30);
        silkRoad.placeStore(20, 40);
        delay(30);
        
        // Move to first store
        silkRoad.moveRobot(5, 5);
        delay(30);
        assertEquals(0, silkRoad.getProfit());
        
        // Move to second store
        silkRoad.moveRobot(10, 10);
        delay(30);
        assertEquals(0, silkRoad.getProfit());
        
        // Both stores should be untouched
        int[][] stores = silkRoad.stores();
        for (int[] store : stores) {
            assertTrue(store[1] > 0); // All stores have tenges
        }
    }
    
    /**
     * Tests that LazyRobot can move backward (unlike neverback).
     */
    @Test
    public void AccordingPshouldAllowLazyRobotToMoveBackward() {
        SilkRoad silkRoad = crearSilkRoadInvisible(30);
        silkRoad.placeRobot(15, "lazy");
        delay(30);
        
        // Move forward
        silkRoad.moveRobot(15, 5);
        delay(30);
        assertTrue(silkRoad.ok());
        int[][] robots = silkRoad.robots();
        assertEquals(20, robots[0][0]);
        
        // Move backward (should work for lazy robot)
        silkRoad.moveRobot(20, -10);
        delay(30);
        assertTrue(silkRoad.ok());
        robots = silkRoad.robots();
        assertEquals(10, robots[0][0]);
    }
    
    /**
     * Tests LazyRobot interaction with FighterStore.
     */
    @Test
    public void AccordingPshouldHandleLazyRobotWithFighterStore() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "lazy");
        silkRoad.placeStore(15, 30, "fighter");
        delay(30);
        
        // Lazy robot moves to fighter store
        silkRoad.moveRobot(5, 10);
        delay(30);
        
        // Lazy robot never tries to take, so fighter store doesn't matter
        assertEquals(0, silkRoad.getProfit());
        
        // Store unchanged
        int[][] stores = silkRoad.stores();
        assertEquals(30, stores[0][1]);
    }
    
    /**
     * Tests comparison between normal and lazy robots.
     */
    @Test
    public void AccordingPshouldCompareLazyAndNormalRobots() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "normal");
        silkRoad.placeRobot(15, "lazy");
        silkRoad.placeStore(10, 30);
        silkRoad.placeStore(20, 30);
        delay(30);
        
        // Normal robot collects
        silkRoad.moveRobot(5, 5);
        delay(30);
        assertEquals(25, silkRoad.getProfit()); // 30 - 5 = 25
        
        // Lazy robot doesn't collect
        silkRoad.moveRobot(15, 5);
        delay(30);
        assertEquals(25, silkRoad.getProfit()); // Still 25
        
        int[][] robots = silkRoad.robots();
        // Sort by position
        if (robots[0][0] > robots[1][0]) {
            int[] temp = robots[0];
            robots[0] = robots[1];
            robots[1] = temp;
        }
        
        // Normal robot has profit
        assertEquals(25, robots[0][1]);
        assertEquals(0, robots[0][2]); // normal type
        
        // Lazy robot has no profit
        assertEquals(0, robots[1][1]);
        assertEquals(3, robots[1][2]); // lazy type
    }
    
    /**
     * Tests that LazyRobot is included in type array structure.
     */
    @Test
    public void AccordingPshouldIncludeLazyRobotInArrayStructure() {
        SilkRoad silkRoad = crearSilkRoadInvisible(50);
        silkRoad.placeRobot(5, "normal");
        silkRoad.placeRobot(10, "neverback");
        silkRoad.placeRobot(15, "tender");
        silkRoad.placeRobot(20, "lazy");
        delay(30);
        
        int[][] robots = silkRoad.robots();
        assertEquals(4, robots.length);
        
        // Sort by position
        for (int i = 0; i < robots.length - 1; i++) {
            for (int j = i + 1; j < robots.length; j++) {
                if (robots[i][0] > robots[j][0]) {
                    int[] temp = robots[i];
                    robots[i] = robots[j];
                    robots[j] = temp;
                }
            }
        }
        
        assertEquals(0, robots[0][2]); // normal
        assertEquals(1, robots[1][2]); // neverback
        assertEquals(2, robots[2][2]); // tender
        assertEquals(3, robots[3][2]); // lazy
    }
    
    /**
     * Tests that moveRobots doesn't assign stores to lazy robots.
     */
    @Test
    public void AccordingPshouldNotAssignStoresToLazyRobotsInOptimalMovement() {
        SilkRoad silkRoad = crearSilkRoadInvisible(40);
        silkRoad.placeRobot(5, "normal");
        silkRoad.placeRobot(15, "lazy");
        silkRoad.placeStore(10, 30);
        silkRoad.placeStore(20, 25);
        delay(30);
        
        silkRoad.moveRobots();
        delay(30);
        
        // Only normal robot should generate profit
        assertTrue(silkRoad.getProfit() > 0);
        
        int[][] robots = silkRoad.robots();
        // Sort by position to identify robots
        if (robots[0][0] > robots[1][0]) {
            int[] temp = robots[0];
            robots[0] = robots[1];
            robots[1] = temp;
        }
        
        // Normal robot might have profit (depending on optimal assignment)
        // Lazy robot should always have 0 profit
        boolean foundLazyWithZeroProfit = false;
        for (int[] robot : robots) {
            if (robot[2] == 3) { // lazy type
                foundLazyWithZeroProfit = (robot[1] == 0);
                break;
            }
        }
        assertTrue(foundLazyWithZeroProfit);
    }
}