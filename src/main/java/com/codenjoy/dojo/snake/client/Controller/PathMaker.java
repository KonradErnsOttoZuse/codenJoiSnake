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
        System.out.println("Запущен метод goToPoint - движение к целевой точке по прямой");
        PathPoint from;
        PathPoint to;

        //Отримуємо координати цільового поінта
        to = new PathPoint(point);
        System.out.println("TargetPoint: " + to);

        //Отримуємо координати голови змії
        if (board.getHead() != null) {
            from = new PathPoint(board.getHead(), board.getSnakeDirection());
            System.out.println("Snake head: " + from);
        } else {
            System.out.println("Snake head coordinates out of range");
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
        System.out.println("Inside goByHamilton method");
        HamiltonService hs = HamiltonService.getInstance(area, startPoint, streamDirection);
        while (hs.getPath().size() < hs.getArea().getSizeX() * hs.getArea().getSizeY()) {
            System.out.printf("ps.getPath().size() - %d < SizeX() - %d * SizeY() - %d\n", hs.getPath().size(), hs.getArea().getSizeX(), hs.getArea().getSizeY());
            System.out.println("area = " + area);
            System.out.println(hs.getPath());
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

    public static Optional<Path> goByReverseHamilton(Board board, Path path) {
        Direction streamDirection = path.get(0).getWay().inverted();

        int maxIndX = path.get(0).getX();
        int maxIndY = path.get(0).getY();
        int minIndX = path.get(0).getX();
        int minIndY = path.get(0).getY();
        for (Point p : path.getPath()
        ) {
            if (p.getX() > maxIndX) maxIndX = p.getX();
            if (p.getY() > maxIndY) maxIndY = p.getY();
            if (p.getX() < minIndX) minIndX = p.getX();
            if (p.getY() < minIndY) minIndY = p.getY();
        }
        Area area = new Area(new PointImpl(minIndX, minIndY), new PointImpl(maxIndX, maxIndY));
        PathPoint startPoint = new PathPoint(area.getFinish().getX(), area.getStart().getY(), streamDirection);

        return getHamiltonPath(area, startPoint, streamDirection);
    }


}
