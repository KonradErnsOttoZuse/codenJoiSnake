package com.codenjoy.dojo.snake.client.Entity;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.ArrayList;
import java.util.List;

public class Area {
    private Point start;
    private Point finish;
    int sizeX;
    int sizeY;

    public Area(Point start, Point finish) {
        this.start = start;
        this.finish = finish;
        sizeX = finish.getX() - start.getX() + 1;
        sizeY = finish.getY() - start.getY() + 1;
        if (sizeX <= 0 || sizeY <= 0) throw new IllegalArgumentException("Wrong points value");
    }

    public Area(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.start = new PointImpl(1, 1);
        this.finish = new PointImpl(sizeX, sizeY);
        if (sizeX <= 0 || sizeY <= 0) throw new IllegalArgumentException("Wrong points value");
    }

    public List<Point> getAllPoints() {
        List<Point> lp = new ArrayList<>();
        for (int i = start.getX(); i <= finish.getX(); i++) {
            for (int j = start.getY(); j <= finish.getY(); j++) {
                lp.add(new PointImpl(i, j));
            }
        }
        return lp;
    }

    public Point getStart() {
        return start;
    }

    public Point getFinish() {
        return finish;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setStartPoint(Point point) {
        finish.setX(finish.getX() + (point.getX() - start.getX()));
        finish.setY(finish.getY() + (point.getY() - start.getY()));
        start = point;
    }

    public boolean contains(Point point) {
        return getAllPoints().contains(point);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Area{")
                .append("start=").append(start)
                .append(", finish=").append(finish)
                .append(", sizeX=").append(sizeX)
                .append(", sizeY=").append(sizeY)
                .append("}\n");
        getAllPoints().forEach(sb::append);
        return sb.toString();
    }
}
