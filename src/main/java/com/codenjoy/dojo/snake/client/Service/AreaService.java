package com.codenjoy.dojo.snake.client.Service;

import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Entity.Area;

import java.util.List;
import java.util.Optional;

public class AreaService {
    public static final int CROSSROAD = 7;

    public static Area getHamiltonBox(int snakeLength) {
        if (snakeLength >= 1 && snakeLength < 6) {
            return new Area(3, 2);
        } else if (snakeLength < 12) {
            return new Area(6, 2);
        } else if (snakeLength < 18) {
            return new Area(6, 3);
        } else if (snakeLength < 36) {
            return new Area(6, 6);
        } else if (snakeLength < 78) {
            return new Area(6, 13);
        } else if (snakeLength <= 169) {
            return new Area(12, 13);
        }
        throw new IllegalArgumentException("Wrong snake length value");
    }

    public static Optional<Area> getWorkBox(Area local, Point target) {
        //Если яблоко лежит на перекрестке
        if (target.getX() == CROSSROAD || target.getY() == CROSSROAD) {
            return Optional.empty();
        }

        Area global = new Area(new PointImpl(1, 1), new PointImpl(13, 13));
        Area box = new Area(local.getStart(), local.getFinish());

        int x = 1;
        int y = 1;

        int x0 = global.getStart().getX();
        int y0 = global.getStart().getY();

        int x2 = global.getFinish().getX();
        int y2 = global.getFinish().getY();

        int x1 = target.getX();
        int y1 = target.getY();


        if (y2 == box.getSizeY()) {
            y = y0;
        }
        else if (y1 < CROSSROAD) {
            for (int i = y0; i <= y2; i += box.getSizeY()) {
                if (i <= y1) {
                    y = i;
                }
            }
        } else {
            for (int i = CROSSROAD + 1; i <= y2; i += box.getSizeY()) {
                if (i <= y1) {
                    y = i;
                }
            }
        }

        if (x2 == box.getSizeX()) {
            x = x0;
        }
        else if (x1 < CROSSROAD) {
            for (int i = x0; i <= x2; i += box.getSizeX()) {
                if (i <= x1) {
                    x = i;
                }
            }
        } else {
            for (int i = CROSSROAD + 1; i <= x2; i += box.getSizeX()) {
                if (i <= x1) {
                    x = i;
                }
            }
        }
        System.out.println("Найдена точка привязки: " + new PointImpl(x, y));
        box.setStartPoint(new PointImpl(x, y));
        System.out.println("Сформирован бокс: " + box);
        return Optional.of(box);
    }

    public static Point getCloserPointOfArea(Area area, Point point) {
        System.out.println("Запущен метод getCloserPointOfArea");
        List<Point> allPoints = area.getAllPoints();
        Point minPoint = allPoints.get(0);
        Point tmpPoint;
        double minDistance = minPoint.distance(point);
        double tmpDistance;
        for (int i = 1; i < allPoints.size(); i++) {
            tmpPoint = allPoints.get(i);
            tmpDistance = tmpPoint.distance(point);
            if (tmpDistance < minDistance) minPoint = tmpPoint;
        }
        System.out.println("Найдена ближайшая точка: " + minPoint);
        return minPoint;
    }

}