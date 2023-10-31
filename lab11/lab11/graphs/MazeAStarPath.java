package lab11.graphs;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private final int s;
    private final int t;
    // private boolean targetFound = false;
    private final Maze maze;
    private final int targetX;
    private final int targetY;
    private final Queue<Integer> pq = new PriorityQueue<>(new VertexComparator());

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        this.targetX = targetX;
        this.targetY = targetY;
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /**
     * Estimate of the distance from v to the target.
     */
    private int h(int v) {
        return Math.abs(maze.toX(v) - targetX) + Math.abs(maze.toY(v) - targetY);
    }

    /**
     * Finds vertex estimated to be closest to target.
     */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /**
     * Performs an A star search from vertex s.
     */
    private void astar() {
        pq.add(s);
        while (!pq.isEmpty()) {
            int v = pq.remove();
            marked[v] = true;
            announce();

            if (v == t) {
                return;
            }

            for (int nb : maze.adj(v)) {
                // nb != maze.xyTo1D(maze.toX(edgeTo[v]), maze.toY(edgeTo[v]))
                if (!marked[nb]) {
                    pq.add(nb);
                    edgeTo[nb] = v;
                    distTo[nb] = distTo[v] + 1;
                }
            }
        }
    }

    private class VertexComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return h(o1) - h(o2);
        }
    }

    @Override
    public void solve() {
        astar();
    }

}

