import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /* id map to the closest distance to source for now */
    static Map<Long, Double> idToBestDistance;

    /* id map to the best parent corresponding to bestDistance above.
    * for example, id A and id B, B's best parent is A only if
    * distance(source, A) + distance(A, B) is the closest way
    * from source to B for now, otherwise it will be updated */
    static Map<Long, Long> idToParent;

    /* distance from id to goal */
    static Map<Long, Double> idToDistanceToGoal;
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     *
     * @param g       The graph to use.
     * @param stlon   The longitude of the start location.
     * @param stlat   The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        idToBestDistance = new HashMap<>();
        idToParent = new HashMap<>();
        idToDistanceToGoal = new HashMap<>();

        long startId = g.closest(stlon, stlat),
                destId = g.closest(destlon, destlat);

        long endId = spHelper(g, startId, destId);

        return findPath(g, endId); // done
    }

    private static long spHelper(GraphDB g, long startId, long destId) {
        PriorityQueue<Long> minPQ = new PriorityQueue<>(new NDComparator());
        minPQ.add(startId);

        idToParent.put(startId, startId);
        idToDistanceToGoal.put(startId, g.distance(startId, destId));
        idToBestDistance.put(startId, 0.0);

        long preId = Long.MAX_VALUE;
        long endId = startId; // final destination id

        while (true) {
            if (minPQ.isEmpty()) {
                break;
            }

            long minId = minPQ.remove();

            /* find the endId closest to destination, in case destId can't be reached */
            if (idToDistanceToGoal.get(endId) > idToDistanceToGoal.get(minId)) {
                endId = minId;
            }

            if (minId == destId) {
                break;
            }
            for (Long neighborId : g.getNode(minId).adjacent) {
                double newDistance = g.distance(minId, neighborId) + idToBestDistance.get(minId);
                boolean idAdded = idToBestDistance.containsKey(neighborId);

                if (neighborId == preId
                        || (idAdded && idToBestDistance.get(neighborId) < newDistance)) {
                    continue; // the distance didn't be updated, no need to add to minPQ
                } else if (!idAdded) {
                    idToDistanceToGoal.put(neighborId, g.distance(neighborId, destId));
                }
                idToBestDistance.put(neighborId, newDistance);
                idToParent.put(neighborId, minId);

                minPQ.add(neighborId);
            }
            preId = minId;
        }
        return endId;
    }

    private static List<Long> findPath(GraphDB g, long destId) {
        List<Long> path = new ArrayList<>();
        while (destId != idToParent.get(destId)) {
            path.add(0, destId);
            destId = idToParent.get(destId);
        }
        if (!path.isEmpty()) {
            path.add(0, destId);
        }
        return path;
    }

    private static class NDComparator implements Comparator<Long> {
        @Override
        public int compare(Long o1, Long o2) {
            double ob1 = idToBestDistance.get(o1) + idToDistanceToGoal.get(o1),
                    ob2 = idToBestDistance.get(o2) + idToDistanceToGoal.get(o2);

            double result = ob1 - ob2;
            if (result > 0) {
                return 1;
            } else if (result == 0) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     *
     * @param g     The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        // int i = 1;

        List<NavigationDirection> directionList = new ArrayList<>();

        long idOne = route.get(0), idTwo = route.get(1);

        double preBearing = g.bearing(idOne, idTwo);
        double currentBearing = preBearing;

        Set<String> preCommonWay = findCommonWay(g.getNode(idOne).way, g.getNode(idTwo).way);
        NavigationDirection currentNd = new NavigationDirection();
        currentNd.direction = NavigationDirection.START;
        directionList.add(currentNd);

        long preId = idOne;
        for (long id : route) {
            if (preId != id) {
                currentBearing = g.bearing(preId, id);
            }
            double offset = preBearing - currentBearing;
            if (currentBearing * preBearing < 0 && (offset > 180 || offset < -180)) {
                if (preBearing > 0) {
                    offset -= 360;
                } else {
                    offset += 360;
                }
            }

            offset *= -1;

            Set<String> currentCommonWay = findCommonWay(g.getNode(id).way, g.getNode(preId).way);
            Set<String> contrast = findCommonWay(currentCommonWay, preCommonWay);

            if (contrast.isEmpty() || (Math.abs(offset) > 15 && currentCommonWay.size() > 1)) {
                currentNd = new NavigationDirection();
                directionList.add(currentNd);

                if (!contrast.isEmpty()) {
                    currentCommonWay = findUnCommonWay(preCommonWay, currentCommonWay);
                }

                if (offset < -100) {
                    currentNd.direction = NavigationDirection.SHARP_LEFT;
                } else if (offset < -30) {
                    currentNd.direction = NavigationDirection.LEFT;
                } else if (offset < -15) {
                    currentNd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (offset <= 15) {
                    currentNd.direction = NavigationDirection.STRAIGHT;
                } else if (offset <= 30) {
                    currentNd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (offset <= 100) {
                    currentNd.direction = NavigationDirection.RIGHT;
                } else {
                    currentNd.direction = NavigationDirection.SHARP_RIGHT;
                }

            }

            // i++;
            NavigationDirection lastNd = directionList.get(directionList.size() - 1);
            lastNd.distance += g.distance(preId, id);
            if (currentCommonWay.size() == 1) {
                lastNd.way = currentCommonWay.iterator().next();
            }

            preBearing = currentBearing;
            preCommonWay = currentCommonWay;

            preId = id;
        }
        return directionList; // done
    }

    private static Set<String> findCommonWay(Set<String> one, Set<String> two) {
        Set<String> way = new HashSet<>();
        if (one.isEmpty()) { // if one's name is null, two too.
            way.add("");
            return way;
        }
        for (String nameOne : one) {
            for (String nameTwo : two) {
                if (nameOne.equals(nameTwo)) {
                    way.add(nameOne);
                }
            }
        }
        return way;
    }

    private static Set<String> findUnCommonWay(Set<String> pre, Set<String> now) {
        Set<String> way = new HashSet<>();

        for (String nameOne : now) {
            if (!pre.contains(nameOne)) {
                way.add(nameOne);
            }
        }
        return way;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /**
         * Integer constants representing directions.
         */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /**
         * Number of directions supported.
         */
        public static final int NUM_DIRECTIONS = 8;

        /**
         * A mapping of integer values to directions.
         */
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /**
         * Default name for an unknown way.
         */
        public static final String UNKNOWN_ROAD = "unknown road";

        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /**
         * The direction a given NavigationDirection represents.
         */
        int direction;
        /**
         * The name of the way I represent.
         */
        String way;
        /**
         * The distance along this way I represent.
         */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         *
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                        && way.equals(((NavigationDirection) o).way)
                        && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
