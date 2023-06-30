package com.codenjoy.dojo.snake.client.Service;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.client.Entity.Area;
import com.codenjoy.dojo.snake.client.Entity.Path;
import com.codenjoy.dojo.snake.client.Entity.PathPoint;

import java.util.Optional;

public class PathService {
    private final Path path;
    private PathPoint curr;
    private final PathPoint from;
    private final PathPoint to;

    Board board;
    Area area = new Area(new PointImpl(0, 0), new PointImpl(14, 14));

    private PathService(Board board, PathPoint to, PathPoint from) {
        this.path = Path.getInstance();
        this.to = to;
        this.from = from;
        this.board = board;
        this.curr = from;
    }

    public static PathService getInstance(Board board, PathPoint to, PathPoint from) {
        return new PathService(board, to, from);
    }

    public static PathService getInstance(Board board, PathPoint from) {
        PathPoint to = new PathPoint(0, 0, Direction.RIGHT);
        return new PathService(board, to, from);
    }

    // hold direction, move one step forward
    public boolean stepForward() {
        return setPathPoint(curr.getWay());
    }

    // change direction - turns Right and moves one step forward
    public boolean turnRight() {
        return setPathPoint(curr.getWay().clockwise());
    }

    // change direction - turns Left and moves one step forward
    public boolean turnLeft() {
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
        //проверка на границу области
        return point.getX() >= area.getStart().getX() &&
                point.getX() <= area.getFinish().getX() &&
                point.getY() >= area.getStart().getY() &&
                point.getY() <= area.getFinish().getY() &&
                //проверка на повторяющиеся точки маршрута
                !path.contains(point) &&
                //проверка на стены
                !board.getWalls().contains(point) &&
                // проверка на тело змейки
                !board.getSnake().contains(point) &&
                //проверка - не жрать плохое яблоко
                !board.getStones().contains(point);
    }

    public Path getPath() {
        return path;
    }

    public PathPoint getCurr() {
        return curr;
    }

    public PathPoint getTo() {
        return to;
    }

    public boolean isXcloser() {
        int x2 = curr.getX(),
                y2 = curr.getY(),
                x1 = to.getX(),
                y1 = to.getY();
        return Math.abs(x2 - x1) < Math.abs(y2 - y1);
    }

    public Optional<Direction> getTargetDirection(Point to, Point from) {
        int dx = getDx(to, from);
        int dy = getDy(to, from);
        if (dx == -1 && dy == 1) {
            return Optional.of(isXcloser() ? Direction.UP : Direction.LEFT);
        } else if (dx == 0 && dy == 1) {
            return Optional.of(Direction.UP);
        } else if (dx == 1 && dy == 1) {
            return Optional.of(isXcloser() ? Direction.UP : Direction.RIGHT);
        } else if (dx == 1 && dy == 0) {
            return Optional.of(Direction.RIGHT);
        } else if (dx == 1 && dy == -1) {
            return Optional.of(isXcloser() ? Direction.DOWN : Direction.RIGHT);
        } else if (dx == 0 && dy == -1) {
            return Optional.of(Direction.DOWN);
        } else if (dx == -1 && dy == -1) {
            return Optional.of(isXcloser() ? Direction.DOWN : Direction.LEFT);
        } else if (dx == -1 && dy == 0) {
            return Optional.of(Direction.LEFT);
        }
        return Optional.empty();
    }

    public Point getFrom() {
        return from;
    }

    public boolean nextStep(Direction needed) {
        int[][] rules = {{0, 1, 1, -1},
                {-1, 0, -1, 1},
                {-1, 1, 0, 1},
                {1, -1, 1, 0}};
        if (curr.getWay().value() <= 3 && needed.value() <= 3) {
            if (rules[curr.getWay().value()][needed.value()] < 0) return turnLeft();
            else if (rules[curr.getWay().value()][needed.value()] > 0) return turnRight();
            else return stepForward();
        }

        return false;
    }

    public static Optional<Direction> pathSync(Path path, PathPoint point) {
        Optional<Direction> od;
        int index = path.indexOf(point);
        int indexNext = index + 1 < path.size() ? index + 1 : 0;
        Direction nextWay = path.get(indexNext).getWay();
        Direction currWay = point.getWay();
        //текущее направление движения совпадает с направлением следующего шага пути ?
        if (currWay.equals(nextWay)) {
            return Optional.of(currWay);
        }

        //получаем направление после поворота направо
        Direction turnRight = point.getWay().clockwise();
        //получаем направление после поворота налево
        Direction turnLeft = point.getWay().clockwise().inverted();
        PathPoint nextPointR = new PathPoint(turnRight.changeX(point.getX()), turnRight.changeY(point.getY()), turnRight);
        PathPoint nextPointL = new PathPoint(turnLeft.changeX(point.getX()), turnLeft.changeY(point.getY()), turnLeft);
        if (path.contains(nextPointR) && path.get(path.indexOf(nextPointR)).getWay().equals(turnRight)) {
            return Optional.of(turnRight);
        }
        if (path.contains(nextPointL) && path.get(path.indexOf(nextPointL)).getWay().equals(turnLeft)) {
            return Optional.of(turnLeft);
        }
        return Optional.empty();
    }

    public static int getDx(Point to, Point from) {
        return Integer.compare(to.getX() - from.getX(), 0);
    }

    public static int getDy(Point to, Point from) {
        return Integer.compare(to.getY() - from.getY(), 0);
    }

    public static boolean isPointInArea(Area area, Point point) {
        return area.getStart().getX() <= point.getX() &&
                area.getStart().getY() <= point.getY() &&
                area.getFinish().getY() >= point.getY() &&
                area.getFinish().getX() >= point.getX();
    }

}
