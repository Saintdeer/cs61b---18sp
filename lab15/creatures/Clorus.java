package creatures;

import huglife.Action;
import huglife.Creature;
import huglife.Occupant;
import huglife.Direction;
import huglife.HugLifeUtils;

import java.util.List;
import java.util.Map;
import java.awt.Color;

public class Clorus extends Creature {
    /**
     * red/green/blue color.
     */
    private int r, g, b;

    public Clorus(double e) {
        super("clorus");
        energy = e;
        r = 34;
        g = 0;
        b = 231;
    }

    public Clorus() {
        this(1);
    }

    @Override
    public void move() {
        energy -= 0.03;
    }

    @Override
    public void attack(Creature c) {
        energy += c.energy();
    }

    @Override
    public Creature replicate() {
        energy /= 2;
        return new Clorus(energy);
    }

    @Override
    public void stay() {
        energy -= 0.01;
    }

    @Override
    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        List<Direction> empties = getNeighborsOfType(neighbors, "empty");
        List<Direction> plips = getNeighborsOfType(neighbors, "plip");

        if (empties.isEmpty()) {
            return new Action(Action.ActionType.STAY);
        } else if (!plips.isEmpty()) {
            return new Action(Action.ActionType.ATTACK, HugLifeUtils.randomEntry(plips));
        } else if (energy >= 1.0) {
            return new Action(Action.ActionType.REPLICATE, HugLifeUtils.randomEntry(empties));
        }

        return new Action(Action.ActionType.MOVE, HugLifeUtils.randomEntry(empties));
    }

    @Override
    public Color color() {
        return new Color(r, g, b);
    }
}
