package com.codenjoy.dojo.snake.client.Controller;


import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.client.Entity.Area;
import com.codenjoy.dojo.snake.client.Entity.Path;
import com.codenjoy.dojo.snake.client.Entity.PathPoint;
import com.codenjoy.dojo.snake.client.Service.HamiltonService;
import com.codenjoy.dojo.snake.client.Service.PathService;

import java.util.Optional;

public class PathMaker {

    public static Optional<Path> goToApple(Board board) {
        PathPoint apple = new PathPoint(board.getApples().get(0));
        return goToPoint(board, apple);
    }

    public static Optional<Path> goToPoint(Board board, Point point) {
        PathPoint from;
        PathPoint to;

        //Отримуємо координати цільового поінта
        to = new PathPoint(point);

        //Отримуємо координати голови змії
        if (board.getHead() != null) {
            from = new PathPoint(board.getHead(), board.getSnakeDirection());
        } else {
            return Optional.empty();
        }

        PathService ps = PathService.getInstance(board, to, from);
        Optional<Direction> od;
        Direction targetDirection;
        while (!ps.getCurr().equals(ps.getTo())) {
            od = ps.getTargetDirection(ps.getTo(), ps.getFrom());
            if (od.isPresent()) {
                targetDirection = od.get();
                if (!ps.nextStep(targetDirection)) {
                    return Optional.empty();
                }
            }
        }
        return Optional.of(ps.getPath());
    }

    public static Optional<Path> goToNextFreeCell(Board board) {
        //Отримуємо координати голови змії
        PathPoint from;
        if (board.getHead() != null) {
            from = new PathPoint(board.getHead(), board.getSnakeDirection());
        } else {
            return Optional.empty();
        }

        PathService ps = PathService.getInstance(board, from);
        if (!ps.stepForward()) {
            if (!ps.turn()) {
                return Optional.empty();
            }
        }
        return Optional.of(ps.getPath());
    }

    public static Optional<Path> goByHamilton(Area area) {
        Direction streamDirection = Direction.RIGHT;
        PathPoint startPoint = new PathPoint(area.getStart());
        return getHamiltonPath(area, startPoint, streamDirection);
    }

    private static Optional<Path> getHamiltonPath(Area area, PathPoint startPoint, Direction streamDirection) {
        HamiltonService hs = HamiltonService.getInstance(area, startPoint, streamDirection);
        while (hs.getPath().size() < hs.getArea().getSizeX() * hs.getArea().getSizeY()) {
            hs.moveR();
            hs.turn();
            if (hs.getArea().getSizeX() != 2 && hs.getArea().getSizeY() != 2)
                if (hs.getCurr().getWay() == streamDirection.inverted()) hs.turn();
        }
        Optional<Path> op;
        if (hs.getPath() == null) {
            op = Optional.empty();
            return op;
        } else op = Optional.of(hs.getPath());
        return op;
    }

    public static Optional<Path> goByReverseHamilton(Path path) {
        Direction streamDirection = path.get(0).getWay().inverted();

        int maxX = path.get(0).getX();
        int maxY = path.get(0).getY();
        int minX = path.get(0).getX();
        int minY = path.get(0).getY();
        for (Point p : path.getPath()
        ) {
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() > maxY) maxY = p.getY();
            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();
        }
        Area area = new Area(new PointImpl(minX, minY), new PointImpl(maxX, maxY));
        PathPoint startPoint = new PathPoint(area.getFinish().getX(), area.getStart().getY(), streamDirection);

        return getHamiltonPath(area, startPoint, streamDirection);
    }


}
