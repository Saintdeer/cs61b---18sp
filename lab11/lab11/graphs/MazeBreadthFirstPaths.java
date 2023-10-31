package lab11.graphs;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private final int t;
    private final Maze maze;
    private final Queue<Integer> queue = new ArrayDeque<>();

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        int s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
        queue.add(s);
    }

    /**
     * Conducts a breadth first search of the maze starting at the source.
     */
    private void bfs() {
        /* Your code here. Don't forget to update distTo,
         edgeTo, and marked, as well as call announce()*/
        while (!queue.isEmpty()) {
            int v = queue.remove();
            marked[v] = true;
            announce();

            if (v == t) {
                return;
            }

            for (int nb : maze.adj(v)) {
                // nb != maze.xyTo1D(maze.toX(edgeTo[v]), maze.toY(edgeTo[v]))
                if (!marked[nb]) {
                    queue.add(nb);
                    edgeTo[nb] = v;
                    distTo[nb] = distTo[v] + 1;
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

