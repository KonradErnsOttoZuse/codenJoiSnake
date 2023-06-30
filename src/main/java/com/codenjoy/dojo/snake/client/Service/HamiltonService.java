package com.codenjoy.dojo.snake.client.Service;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.snake.client.Entity.Area;
import com.codenjoy.dojo.snake.client.Entity.Path;
import com.codenjoy.dojo.snake.client.Entity.PathPoint;

public class HamiltonService {

    Area area;
    Path path;

    PathPoint curr;

    private HamiltonService(Area area, PathPoint startPoint, Direction d) {
        this.area = area;
        this.path = Path.getInstance();
        this.curr = new PathPoint(startPoint, d);
    }

    public static HamiltonService getInstance(Area area, PathPoint startPoint, Direction d) {
        return new HamiltonService(area, startPoint, d);
    }

    // hold direction, move one step forward
    public boolean stepForward() {
        return setPathPoint(curr.getWay());
    }

    // hold a direction, keep moving forward while haven't a collision
    public void moveR() {
        if (stepForward()) moveR();
    }

    // change direction - turns Right and moves one step forward
    public boolean turnRight() {
        return setPathPoint(curr.getWay().clockwise());
    }

    // change direction - turns Left and moves one step forward
    public boolean turnLeft() {
//        System.out.println("Метод turnLeft()");
        return setPathPoint(curr.getWay().clockwise().inverted());
    }

    // change direction - turns to free cell (or to left if free cells more than one) and moves one step forward
    public boolean turn() {
        if (turnLeft()) return true;
        return turnRight();
    }

    public boolean setPathPoint(Direction d) {
        PathPoint next = new PathPoint(d.changeX(curr.getX()), d.changeY(curr.getY()), d);
        if (checkCollision(next)) {
            path.add(next);
            curr = next;
            return true;
        }
        return false;
    }

    public boolean checkCollision(PathPoint point) {
//        System.out.println("checkCollision(), checking point: " + point);
        //проверка на границу области
        return point.getX() >= area.getStart().getX() &&
                point.getX() <= area.getFinish().getX() &&
                point.getY() >= area.getStart().getY() &&
                point.getY() <= area.getFinish().getY() &&
                //проверка на повторяющиеся точки маршрута
                !path.contains(point);
    }

    public Path getPath() {
        return path;
    }

    public Area getArea() {
        return area;
    }

    public PathPoint getCurr() {
        return curr;
    }
}
