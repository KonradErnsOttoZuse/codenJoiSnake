package com.codenjoy.dojo.snake.client.Entity;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;


public class PathPoint extends PointImpl {
    Direction direction;

    public PathPoint(int x, int y, Direction direction) {
        super(x, y);
        this.direction = direction;
    }

    public PathPoint(Point point) {
        super(point != null ? point.getX() : 0, point != null ? point.getY() : 0);
        this.direction = Direction.DOWN;
    }

    public PathPoint(Point point, Direction direction) {
        super(point);
        this.direction = direction;
    }
    
    public Direction getWay() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d, %s]", x, y, direction);
    }
}
