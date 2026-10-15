package game.generator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import game.*;

public class RecursiveBacktracker implements Generator {
    private final Maze maze;
    private final Random random;

    public RecursiveBacktracker(Maze maze) {
        this(maze, new Random());
    }

    public RecursiveBacktracker(Maze maze, Random random) {
        this.maze = java.util.Objects.requireNonNull(maze);
        this.random = java.util.Objects.requireNonNull(random);
    }

    @Override
    public void generate(Position start) {
        maze.beginGeneration(start);
        Deque<Position> stack = new ArrayDeque<>();
        Position first = start;
        maze.cellAt(first).visit();
        stack.push(first);

        while (!stack.isEmpty()) {
            Position current = stack.peek();
            List<Direction> available = new ArrayList<>();
            for (Direction direction : maze.getDirections()) {
                Position next = current.cloneMoved(direction);
                if (maze.isInBounds(next) && maze.canVisit(next)) {
                    available.add(direction);
                }
            }

            if (available.isEmpty()) {
                stack.pop();
                continue;
            }

            Collections.shuffle(available, random);
            Direction direction = available.get(0);
            Position next = current.cloneMoved(direction);

            maze.openPassage(current, direction);
            maze.cellAt(next).visit();
            stack.push(next);
        }
    }
}
