package com.codenjoy.dojo.snake.client.Entity;

import java.util.LinkedList;

public class Path {
    private LinkedList<PathPoint> path;

    private Path() {
        this.path = new LinkedList<>();
    }

    public static Path getInstance() {
        return new Path();
    }

    public LinkedList<PathPoint> getPath() {
        return path;
    }

    public int size() {
        return path.size();
    }

    public void add(PathPoint point) {
        path.add(point);
    }

    public PathPoint get(int index) {
        return path.get(index);
    }

    public int indexOf(PathPoint point) {
        return path.indexOf(point);
    }

    public void reverse() {
        LinkedList<PathPoint> tmp = new LinkedList<>();
        for (int i = path.size() - 1; i > 0; i--) {
            tmp.add(path.get(i));
        }
        path = tmp;
    }


    public boolean contains(PathPoint point) {
        return path.contains(point);
    }

    @Override
    public String toString() {
        if (path.size() == 0) return "{ path empty }";
        StringBuilder sb = new StringBuilder();
        sb.append("Path size: ")
                .append(path.size())
                .append(", first el: ")
                .append(path.get(0))
                .append(", last el: ")
                .append(path.get(path.size() - 1))
                .append("\n").append("{ ");
        StringBuilder finalSb = sb;
        this.path.forEach(x -> finalSb.append(x).append(", "));
        sb = new StringBuilder(finalSb.substring(0, sb.length() - 2));
        sb.append(" }\n");
        return sb.toString();
    }
}
