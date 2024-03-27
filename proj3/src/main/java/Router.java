import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Objects;
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
        long startId = g.closest(stlon, stlat), destId = g.closest(destlon, destlat);
        Map<Long, Double> idToMinMoves = new HashMap<>();
        PriorityQueue<SearchNode> pq = new PriorityQueue<>(
                Comparator.comparingDouble(node -> node.moves + node.distanceToGoal));

        SearchNode startNode = new SearchNode(
                g.getNode(startId), 0, g.distance(startId, destId), null);
        idToMinMoves.put(startId, 0.0);
        pq.add(startNode);

        long lastNodeId = Long.MAX_VALUE;
        SearchNode destNode = null;
        while (!pq.isEmpty()) {
            SearchNode node = pq.remove();
            long parentId = node.id;
            if (parentId == destId) {
                destNode = node;
                break;
            }
            for (long id : g.getNode(parentId).adjacent) {
                if (id != lastNodeId) {
                    double newMoves = idToMinMoves.get(parentId) + g.distance(parentId, id);
                    if (idToMinMoves.containsKey(id) && newMoves >= idToMinMoves.get(id)) {
                        continue;
                    }
                    idToMinMoves.put(id, newMoves);
                    pq.add(new SearchNode(g.getNode(id), newMoves, g.distance(id, destId), node));
                }
            }

            lastNodeId = parentId;
        }

        List<Long> result = new LinkedList<>();
        while (destNode != null) {
            result.add(0, destNode.id);
            destNode = destNode.pre;
        }
        return result;
    }

    private static class SearchNode {
        final long id;
        final double moves, distanceToGoal;
        SearchNode pre;

        SearchNode(GraphDB.Node node, double moves, double distanceToGoal, SearchNode pre) {
            id = node.id;
            this.moves = moves;
            this.distanceToGoal = distanceToGoal;
            this.pre = pre;
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
