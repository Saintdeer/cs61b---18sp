package lab11.graphs;

/**
 * @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */
    private final Maze maze;
    private final int[] edge;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        edge = new int[maze.V()];
    }

    @Override
    public void solve() {
        // Your code here!
        distTo[0] = 0;
        solveHelper(0);
    }

    // Helper methods go here
    private void solveHelper(int s) {
        marked[s] = true;
        announce();

        for (int nb : maze.adj(s)) {
            // nb != maze.xyTo1D(maze.toX(edgeTo[v]), maze.toY(edgeTo[v]))
            if (!marked[nb]) {
                if (distTo[nb] < (distTo[s] + 1)) {
                    edge[nb] = s;
                    findPath(nb);
                    return;
                }
                distTo[nb] = distTo[s] + 1;
                edge[nb] = s;
                solveHelper(nb);
            }
        }
    }

    private void findPath(int v) {
        int parent = edge[v];
        edgeTo[v] = parent;
        while (parent != v) {
            edgeTo[parent] = edge[parent];
            parent = edge[parent];
        }
    }
}

