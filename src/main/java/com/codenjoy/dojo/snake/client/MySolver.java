package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Controller.PathMaker;
import com.codenjoy.dojo.snake.client.Entity.Area;
import com.codenjoy.dojo.snake.client.Entity.Path;
import com.codenjoy.dojo.snake.client.Entity.PathPoint;
import com.codenjoy.dojo.snake.client.Service.AreaService;
import com.codenjoy.dojo.snake.client.Service.PathService;

import java.util.List;
import java.util.Optional;

public class MySolver implements Solver<Board> {

    @Override
    public String get(Board board) {
        return template2(board);
    }

    public String template1(Board board) {
        Optional<Path> op;
            op = PathMaker.goToApple(board);
            if (op.isEmpty()) {
                op = PathMaker.goToNextFreeCell(board);
                if (op.isEmpty()) {
                    return board.getSnakeDirection().toString();
                }
            }

        Path path = op.get();
        return path.get(0).getWay().toString();
    }

    public String template2(Board board) {

        Point targetPoint;
        Point apple = board.getApples().get(0);
        Point snakeHead = board.getHead();
        if (snakeHead == null) return Direction.STOP.toString();
        Area hamiltonBox;
        Area snakeBox;
        List<Point> snake = board.getSnake();
        Optional<Direction> od;
        Optional<Path> op;

        //        определяем размеры h-box
        hamiltonBox = AreaService.getHamiltonBox(snake.size());

        // выполняем привязку h-box
        Optional<Area> oa = AreaService.getWorkBox(hamiltonBox, snakeHead);
        if (oa.isPresent()) {
            snakeBox = oa.get();
        } else {
            od = lookForApple(board, hamiltonBox);
            //если прямой путь к яблоку или блоку с яблоком найден, то делаем шаг по прямому пути
            if (od.isPresent()) {
                return od.get().toString();
            }
            else {
                return template1(board);
            }
        }

        // наполняем h-box маршрутом (трасформируем из Area в Path)
        Path hPath;
        op = PathMaker.goByHamilton(snakeBox);
        if (op.isPresent()) {
            hPath = op.get();
        } else {
            throw new RuntimeException("Can't make path");
        }
        // синхронизируем движение змейки по маршруту
        Direction d = board.getSnakeDirection();
        PathPoint entryPoint = new PathPoint(board.getHead(), d);
        od = PathService.pathSync(hPath, entryPoint);
        if (od.isPresent()) {
            d = od.get();
        } else {
            op = PathMaker.goByReverseHamilton(hPath);
            if (op.isPresent()) {
                hPath = op.get();
                System.out.println(hPath);
                od = PathService.pathSync(hPath, entryPoint);
                if (od.isPresent()) {
                    d = od.get();
                    System.out.println(d);
                } else {
                    throw new RuntimeException("Can't find direction in reversed path.");
                }
            } else {
                throw new RuntimeException("Can't synchronize reversed path.");
            }
        }

        //проверяем находится ли яблоко в нашем боксе ? Если да - делаем шаг по пути гамильтона
        if (PathService.isPointInArea(snakeBox, apple)) {
            return nextStepWithCheck(board, d);
        }

        //вызываем метод по поиску яблока.
        // Если яблоко на перекрестке, и к нему есть прямой маршрут,  то вернется направление движения по этому маршруту.
        // Если яблоко в другой зоне, и к ближайшей точке єтой зоні есть прямой маршрут, то вернется направление движения по этому маршруту.
        // Если яблоко в текущей зоне или к нему нет прямых маршрутов то вернется empty.
        od = lookForApple(board, snakeBox);
        if (od.isPresent()) d = od.get();
        return nextStepWithCheck(board, d);
    }

    public Optional<Direction> lookForApple(Board board, Area hamiltonBox) {
        Optional<Direction> od = Optional.empty();
        Point targetPoint;
        Point apple = board.getApples().get(0);
        Point snakeHead = board.getHead();

        //найти бокс с яблоком
        Area appleBox;
        Optional<Area> optAppleBox = AreaService.getWorkBox(hamiltonBox, apple);
        //яблоко в боксе ?
        if (optAppleBox.isPresent()) {
            appleBox = optAppleBox.get();
            //если да  - находим  в боксе с яблоком ближайшую точку к голове змеи, делаем ее целевой точкой
            targetPoint = AreaService.getCloserPointOfArea(appleBox, snakeHead);
        } else {
            //если нет - яблоко становится целевой точкой
            targetPoint = apple;
        }
        //проверить - есть ли прямой путь до целевой точки ?
        Optional<Path> optPath;
        optPath = PathMaker.goToPoint(board, targetPoint);
//        если да - сделать шаг по прямому пути
        if (optPath.isPresent() && optPath.get().size() > 0) {
            od = Optional.of(optPath.get().get(0).getWay());
        }
        return od;
    }

    public String nextStepWithCheck(Board board, Direction d) {
        PathService ps = PathService.getInstance(board, new PathPoint(board.getHead(), board.getSnakeDirection()));
        Direction tmpDirection = d;
        PathPoint next = new PathPoint(tmpDirection.changeX(ps.getCurr().getX()), tmpDirection.changeY(ps.getCurr().getY()), tmpDirection);
        if (ps.checkCollision(next)) return tmpDirection.toString();
        tmpDirection = d.clockwise();
        next = new PathPoint(tmpDirection.changeX(ps.getCurr().getX()), tmpDirection.changeY(ps.getCurr().getY()), tmpDirection);
        if (ps.checkCollision(next)) return tmpDirection.toString();
        tmpDirection = d.clockwise().inverted();
        next = new PathPoint(tmpDirection.changeX(ps.getCurr().getX()), tmpDirection.changeY(ps.getCurr().getY()), tmpDirection);
        if (ps.checkCollision(next)) return tmpDirection.toString();
        //если нет выхода делаем шаг вперед
        return d.toString();
    }


}

