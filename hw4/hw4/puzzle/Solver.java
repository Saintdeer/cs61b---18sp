package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class Solver {
    private int moves = 0;
    private final MinPQ<SearchNode> snPQ = new MinPQ<>(new SNComparator());
    private final List<WorldState> wsList = new ArrayList<>(); // for solution()
    // int n = 0;

    public Solver(WorldState initial) {
        snPQ.insert(new SearchNode(initial, null));
        solverHelper();
    }

    private void solverHelper() {
        while(true) {
            SearchNode min = snPQ.delMin();
            if (min.ws.isGoal()) {
                moves = min.moves;
                findPath(min);
                break;
            }
            getNeighbors(min);
        }
    }

    private void findPath(SearchNode sn){
        while(sn != null){
            wsList.add(0, sn.ws);
            sn = sn.previous;
        }
    }

    private void getNeighbors(SearchNode removedSN) {
        Iterable<WorldState> nbs = removedSN.ws.neighbors();
        SearchNode nbGrandParent = removedSN.previous;
        for (WorldState nb: nbs) {
            if (nbGrandParent != null && nb.equals(nbGrandParent.ws)) {
                continue; // enqueued WorldState can't be its own grandparent
            }
            snPQ.insert(new SearchNode(nb, removedSN));
            // n += 1;
        }
    }

    private class SearchNode {
        WorldState ws;
        int moves = 0;
        SearchNode previous;
        int distanceToGoal = -1;

        SearchNode(WorldState ws, SearchNode previous) {
            this.ws = ws;
            if (previous != null) {
                this.moves = previous.moves + 1;
            }
            this.previous = previous;
        }
    }

    private class SNComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode o1, SearchNode o2) {
            if(o1.distanceToGoal < 0){
                o1.distanceToGoal = o1.ws.estimatedDistanceToGoal();
            }
            if(o2.distanceToGoal < 0){
                o2.distanceToGoal = o2.ws.estimatedDistanceToGoal();
            }
            int ob1 = o1.moves + o1.distanceToGoal;
            int ob2 = o2.moves + o2.distanceToGoal;
            return ob1 - ob2;
        }
    }
    public int moves() {
        return moves;
    }

    public Iterable<WorldState> solution() {
        return wsList;
    }
}
