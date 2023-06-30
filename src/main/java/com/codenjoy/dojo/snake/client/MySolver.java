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
        System.out.println("Запущен template1");
        Optional<Path> op;
            op = PathMaker.goToApple(board);
            if (op.isEmpty()) {
                System.out.println("Path hasn't found, make one step to nearest random free cell");
                op = PathMaker.goToNextFreeCell(board);
                if (op.isEmpty()) {
                    System.out.println("free cells haven't been found, making one step forward");
                    return board.getSnakeDirection().toString();
                }
            }

        Path path = op.get();
        System.out.println("Path :" + path);
        return path.get(0).getWay().toString();
    }

    public String template2(Board board) {

        Point targetPoint;
        Point apple = board.getApples().get(0);
        Point snakeHead = board.getHead();
        if (snakeHead == null) return Direction.STOP.toString();
        Area hamiltonBox = null;
        Area snakeBox = null;
        List<Point> snake = board.getSnake();
        Optional<Direction> od;
        Optional<Path> op;

        //        определяем размеры h-box
        hamiltonBox = AreaService.getHamiltonBox(snake.size());
        System.out.println("Сформован HamiltonBox:");
        System.out.println(hamiltonBox);

        // выполняем привязку h-box
        Optional<Area> oa = AreaService.getWorkBox(hamiltonBox, snakeHead);
        if (oa.isPresent()) {
            snakeBox = oa.get();
            System.out.println("SnakeBox это HamiltonBox с привязкой по координатам к голове змеи:");
            System.out.println(snakeBox);
        } else {
            System.out.println("Не могу выполнить привязку бокса на карте");
            System.out.println("Перехожу к поиску яблока");
            od = lookForApple(board, hamiltonBox);
            //если прямой путь к яблоку или блоку с яблоком найден, то делаем шаг по прямому пути
            if (od.isPresent()) {
                System.out.println("прямой путь к яблоку или блоку с яблоком найден - " + od.get() + "\n делаем шаг по прямому пути");
                return od.get().toString();
            }
            else {
                System.out.println("Путь к яблоку не найден, запускаю упрощенный алгоритм TEMPLATE1");
                return template1(board);
            }
        }

        // наполняем h-box маршрутом (трасформируем из Area в Path)
        Path hPath;
        op = PathMaker.goByHamilton(snakeBox);
        if (op.isPresent()) {
            hPath = op.get();
            System.out.println("Hamilton маршрут:");
            System.out.println(hPath);
        } else {
            System.out.println("Не могу выполнить построение маршрута");
            throw new RuntimeException("Не могу выполнить построение маршрута");
        }


        // синхронизируем движение змейки по маршруту
        Direction d = board.getSnakeDirection();
        PathPoint entryPoint = new PathPoint(board.getHead(), d);
        od = PathService.pathSync(hPath, entryPoint);
        if (od.isPresent()) {
//            System.out.println(" od.isPresent");
//            System.out.println(" od.get() = " + od.get());
            d = od.get();
//            System.out.println(" d = od.get(), d = " + d);
//            System.out.println(" od.isPresent, Synchronized path: ");
            System.out.println(hPath);
//            System.out.println("new direction for next step:  " + d);
        } else {
            System.out.println("Can't synchronize path.");
            System.out.println("Try to reverse path.");
            op = PathMaker.goByReverseHamilton(board, hPath);
            if (op.isPresent()) {
                hPath = op.get();
//              System.out.println("Synchronized path: ");
                System.out.println(hPath);
                od = PathService.pathSync(hPath, entryPoint);
                if (od.isPresent()) {
                    d = od.get();
//                System.out.println("new direction for next step:  ");
                    System.out.println(d);
                } else {
                    System.out.println("Can't find direction in reversed path.");
                    throw new RuntimeException("Не найдено направление в  обращенном пути");
                }
            } else {
                System.out.println("Can't synchronize reversed path.");
                throw new RuntimeException("Не синхронизируемый путь");
            }
        }

        //проверяем -  целиком ли змейка заползла в бокс ? Если нет - делаем шаг по пути гамильтона
//        System.out.println("проверяем -  целиком ли змейка заползла в бокс ?");
//        if (!PathService.isSnakeCurled(snakeBox, snake)) {
//            System.out.println("Не вся змейка в боксе - делаем шаг по пути гамильтона");
////            return d.toString();
//            return nextStepWithCheck(board, d);
//        }

        System.out.println("вся змейка в боксе - переходим к поиску яблока");
        //проверяем находится ли яблоко в нашем боксе ? Если да - делаем шаг по пути гамильтона
        System.out.println("проверяем -  находится ли яблоко в нашем боксе ?");
        if (PathService.isPointInArea(snakeBox, apple)) {
            System.out.println("яблоко в нашем боксе - продолжаем движение по гамильтону");
//            return d.toString();
            return nextStepWithCheck(board, d);
        }

        //вызываем метод по поиску яблока.
        // Если яблоко на перекрестке, и к нему есть прямой маршрут,  то вернется направление движения по этому маршруту.
        // Если яблоко в другой зоне, и к ближайшей точке єтой зоні есть прямой маршрут, то вернется направление движения по этому маршруту.
        // Если яблоко в текущей зоне или к нему нет прямых маршрутов то вернется empty.
        System.out.println("Яблока нет в текущем боксе. \n Вызываем метод по поиску яблока - lookForApple ");
        od = lookForApple(board, snakeBox);
        if (od.isPresent()) d = od.get();

//        return d.toString();
        return nextStepWithCheck(board, d);
    }

    public Optional<Direction> lookForApple(Board board, Area hamiltonBox) {
        System.out.println("Запущен метод lookForApple");
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
            System.out.println("Найден AppleBox: " + appleBox);
            //если да  - находим  в боксе с яблоком ближайшую точку к голове змеи, делаем ее целевой точкой
            System.out.println("Запускаю метод :  getCloserPointOfArea, " + appleBox);
            targetPoint = AreaService.getCloserPointOfArea(appleBox, snakeHead);
        } else {
            System.out.println("Не могу найти бокс для яблока");
            //если нет - яблоко становится целевой точкой
            targetPoint = apple;
        }
        //проверить - есть ли прямой путь до целевой точки ?
        Optional<Path> optPath;
        optPath = PathMaker.goToPoint(board, targetPoint);
//        если да - сделать шаг по прямому пути
        if (optPath.isPresent() && optPath.get().size() > 0) {
            System.out.println("есть прямой путь до целевой точки:\n" + optPath.get());
            System.out.println("делаю шаг по прямому пути в направлении " + optPath.get().get(0).getWay());
            od = Optional.of(optPath.get().get(0).getWay());
        }
        return od;
    }

    public String nextStepWithCheck(Board board, Direction d) {
        System.out.println("вызвали метод последней проверки");
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
        System.out.println("Иду на таран");
        return d.toString();
    }


}

