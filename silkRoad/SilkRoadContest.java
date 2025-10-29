package silkRoad;

import java.util.*;

/**
 * Clase que implementa la solución al problema de la Ruta de la Seda
 * y proporciona funcionalidades para simularlo visualmente.
 * * @author Exael74 (Github User for Stiven Pardo)
 */
public class SilkRoadContest {
    
    /**
     * Resuelve el problema de maximizar las ganancias.
     * @param days Array bidimensional con información de cada día
     * @return Array con las máximas ganancias posibles después de cada día
     */
    public static int[] solve(int[][] days) {
        if (days == null || days.length == 0) {
            return new int[0];
        }
        
        int n = days[0][0]; // Número de días
        int[] result = new int[n]; // Array para almacenar las ganancias máximas por día
        
        List<Integer> robots = new ArrayList<>();
        List<int[]> stores = new ArrayList<>();
        
        // Procesar cada día
        for (int day = 0; day < n; day++) {
            int dayIndex = day + 1;
            
            // Verificar que hay datos para este día
            if (dayIndex < days.length) {
                int[] event = days[dayIndex];
                
                if (event.length >= 2) {
                    int type = event[0];
                    
                    if (type == 1) { // Añadir robot
                        int robotPos = event[1];
                        robots.add(robotPos);
                    } 
                    else if (type == 2 && event.length >= 3) { // Añadir tienda
                        int storePos = event[1];
                        int storeMoney = event[2];
                        stores.add(new int[]{storePos, storeMoney});
                    }
                }
            }
            
            // Calcular máxima ganancia para este día
            result[day] = calculateMaxProfit(robots, stores);
        }
        
        return result;
    }
    
    /**
     * Simula la solución paso a paso.
     * @param days Array bidimensional con información de cada día
     * @param slow Indica si la simulación debe ser lenta (true) o rápida (false)
     */
    public static void simulate(int[][] days, boolean slow) {
        if (days == null || days.length == 0) {
            System.out.println("No hay datos para simular.");
            return;
        }
        
        int n = days[0][0]; // Número de días
        
        // Primero calculamos los resultados esperados con solve()
        int[] expectedResults = solve(days);
        
        // Crear una instancia de SilkRoad para la simulación visual
        SilkRoad silkRoad = new SilkRoad(100);
        
        System.out.println("=== SIMULACIÓN DE LA RUTA DE LA SEDA ===");
        System.out.println("Total de días: " + n);
        System.out.println();
        
        // Listas para mantener el estado acumulado
        List<Integer> robots = new ArrayList<>();
        List<int[]> stores = new ArrayList<>();
        
        // Simular cada día
        for (int day = 1; day <= n; day++) {
            System.out.println("--- DÍA " + day + " ---");
            
            if (day < days.length) {
                int[] action = days[day];
                
                if (action.length >= 2) {
                    int actionType = action[0];
                    int position = action[1];
                    
                    if (actionType == 1) {
                        // Añadir robot
                        System.out.println("Acción: Añadir robot en posición " + position);
                        silkRoad.placeRobot(position);
                        robots.add(position);
                    } else if (actionType == 2 && action.length >= 3) {
                        // Añadir tienda con tenges
                        int tenges = action[2];
                        System.out.println("Acción: Añadir tienda en posición " + position + " con " + tenges + " tenges");
                        silkRoad.placeStore(position, tenges);
                        stores.add(new int[]{position, tenges});
                    }
                }
            }
            
            // Mostrar estado actual
            System.out.println("Robots disponibles: " + robots.size());
            System.out.println("Tiendas disponibles: " + stores.size());
            
            // Mostrar ganancia máxima esperada (del solve)
            int expectedProfit = expectedResults[day - 1];
            System.out.println("Ganancia máxima posible para el día " + day + ": " + expectedProfit + " tenges");
            
            // Verificar si el simulador sigue activo
            if (!silkRoad.ok()) {
                System.out.println("\nEl simulador ha sido cerrado. Terminando la simulación.");
                return;
            }
            
            System.out.println();
            
            // Pausa entre días para mejor visualización
            try {
                if (slow) {
                    Thread.sleep(2000); // 2 segundos en modo lento
                } else {
                    Thread.sleep(500); // 0.5 segundos en modo rápido
                }
            } catch (InterruptedException e) {
                System.out.println("Simulación interrumpida: " + e.getMessage());
                return;
            }
        }
        
        System.out.println("=== SIMULACIÓN COMPLETADA ===");
        System.out.println("\nResumen de resultados:");
        for (int i = 0; i < expectedResults.length; i++) {
            System.out.println("Día " + (i + 1) + ": " + expectedResults[i] + " tenges");
        }
    }
    
    /**
     * Calcula la máxima ganancia posible con los robots y tiendas disponibles.
     * @param robots Lista de posiciones de robots
     * @param stores Lista de tiendas (posición, tenges)
     * @return La ganancia máxima posible
     */
    static int calculateMaxProfit(List<Integer> robots, List<int[]> stores) {
        if (robots.isEmpty() || stores.isEmpty()) {
            return 0;
        }

        int numStores = stores.size();
        int numRobots = robots.size();

        // Precalcular: mejor ganancia para cada robot visitando cada subconjunto de tiendas
        List<int[]> robotProfits = new ArrayList<>();
        for (int robotPos : robots) {
            int[] profits = new int[1 << numStores];
            for (int mask = 1; mask < (1 << numStores); mask++) {
                List<Integer> storeIndices = new ArrayList<>();
                for (int i = 0; i < numStores; i++) {
                    if ((mask & (1 << i)) != 0) {
                        storeIndices.add(i);
                    }
                }
                profits[mask] = bestRoute(robotPos, stores, storeIndices);
            }
            robotProfits.add(profits);
        }

        // dp[mask][k] = mejor ganancia particionando tiendas en mask usando exactamente k robots
        int INF = Integer.MIN_VALUE / 2;
        int[][] dp = new int[1 << numStores][numRobots + 1];
        
        // Inicializar con infinito negativo
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        dp[0][0] = 0; // 0 tiendas, 0 robots = ganancia 0

        for (int mask = 0; mask < (1 << numStores); mask++) {
            for (int k = 0; k <= numRobots; k++) {
                if (dp[mask][k] == INF) {
                    continue;
                }

                // Asignar un subconjunto de tiendas no visitadas a un nuevo robot
                int unvisited = ((1 << numStores) - 1) ^ mask;
                if (unvisited == 0 || k >= numRobots) {
                    continue;
                }

                int submask = unvisited;
                while (submask > 0) {
                    // Robot k+1 visitará tiendas en submask
                    int bestProfitForSubmask = Integer.MIN_VALUE;
                    for (int r = 0; r < numRobots; r++) {
                        bestProfitForSubmask = Math.max(bestProfitForSubmask, robotProfits.get(r)[submask]);
                    }

                    int newMask = mask | submask;
                    int newK = k + 1;
                    dp[newMask][newK] = Math.max(dp[newMask][newK], dp[mask][k] + bestProfitForSubmask);

                    submask = (submask - 1) & unvisited;
                }
            }
        }

        // Encontrar el máximo entre todos los posibles estados
        int result = 0;
        for (int mask = 0; mask < (1 << numStores); mask++) {
            for (int k = 0; k <= numRobots; k++) {
                if (dp[mask][k] > INF) {
                    result = Math.max(result, dp[mask][k]);
                }
            }
        }

        return Math.max(0, result);
    }

    /**
     * Calcula la mejor ruta para un robot visitando un conjunto de tiendas.
     * @param startPos Posición inicial del robot
     * @param stores Lista de todas las tiendas
     * @param indices Índices de tiendas a visitar
     * @return La ganancia máxima para esta ruta
     */
    static int bestRoute(int startPos, List<int[]> stores, List<Integer> indices) {
        int n = indices.size();

        if (n == 0) {
            return 0;
        }

        if (n == 1) {
            int idx = indices.get(0);
            int pos = stores.get(idx)[0];
            int money = stores.get(idx)[1];
            int profit = money - Math.abs(startPos - pos);
            return Math.max(0, profit);
        }

        int INF = Integer.MIN_VALUE / 2;
        int[][] dp = new int[1 << n][n];
        
        // Inicializar con infinito negativo
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }

        // Estados iniciales: visitar solo una tienda desde la posición inicial
        for (int i = 0; i < n; i++) {
            int idx = indices.get(i);
            int pos = stores.get(idx)[0];
            int money = stores.get(idx)[1];
            int distance = Math.abs(startPos - pos);
            dp[1 << i][i] = money - distance;
        }

        // Iterar sobre todos los posibles subconjuntos y últimas tiendas visitadas
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0 || dp[mask][last] == INF) {
                    continue;
                }

                int lastIdx = indices.get(last);
                int lastPos = stores.get(lastIdx)[0];

                // Intentar añadir una tienda más al recorrido
                for (int nextI = 0; nextI < n; nextI++) {
                    if ((mask & (1 << nextI)) != 0) {
                        continue;
                    }

                    int nextIdx = indices.get(nextI);
                    int nextPos = stores.get(nextIdx)[0];
                    int nextMoney = stores.get(nextIdx)[1];

                    int distance = Math.abs(lastPos - nextPos);
                    int newProfit = dp[mask][last] + nextMoney - distance;

                    int newMask = mask | (1 << nextI);
                    dp[newMask][nextI] = Math.max(dp[newMask][nextI], newProfit);
                }
            }
        }

        // Encontrar la máxima ganancia entre todos los posibles recorridos
        int maxProfit = 0;
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] > INF) {
                    maxProfit = Math.max(maxProfit, dp[mask][last]);
                }
            }
        }

        return Math.max(0, maxProfit);
    }
}