package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.client.WebSocketRunner;

/**
 * User: Oleksandr Kobets
 */
public class Launcher {
//
//    //    private Dice dice;
//    private Board board;

//    public YourSolver(Dice dice) {
//        this.dice = dice;
//    }

//    @Override
//    public String get(Board board) {
//        this.board = board;
//        Point head = board.getHead();
//        List<Point> hamilton = getHamiltonPath();
//        hamilton.forEach(System.out::print);
//        System.out.println();
//        int a = hamilton.indexOf(head);
//        System.out.println("^".repeat(35));
//        System.out.printf("head = %s\n", head.toString());
//        System.out.printf("index a = %d\n", a);
//        System.out.printf("hamilton.get(a+1) = %s\n", hamilton.get(a + 1));
//        System.out.println("^".repeat(35));
////        for (int i = a; i < hamilton.size(); i++) {
////            System.out.print(hamilton.get(i).toString());
////        }
//        System.out.println("^".repeat(35));
//        System.out.println(hamilton.get(a + 1).toString());
//
////        Direction aim = byStraightLine(head, hamilton.get(a + 1));
//
//        Direction aim = byStraightLine();
//
//        Direction me = board.getSnakeDirection();
//        Direction newDirection = changeDirection(me, aim);
//
//
////        System.out.println();
////        System.out.printf("me = %s, aim = %s, new direction = %s\n", me.toString(), aim.toString(), newDirection.toString());
////        System.out.println();
////        System.out.println(board.toString());
//        return newDirection.toString();
//    }
//
//    public String aroundTheWorld() {
//
//        Point me = board.getHead();
//        int x2 = me.getX();
//        int y2 = me.getY();
//
//        Point apple = board.getApples().get(0);
//        int x1 = apple.getX();
//        int y1 = apple.getY();
//
//        Direction newDirection = board.getSnakeDirection().clockwise();
//        return newDirection.toString();
//    }
//
//    public Direction turnRight() {
//        return board.getSnakeDirection().clockwise();
//    }
//
//    public Direction turnLeft() {
//        return board.getSnakeDirection().clockwise().inverted();
//    }
//
//    public Direction byStraightLine() {
//        Point me = board.getHead();
//        Point aim = board.getApples().get(0);
//        return byStraightLine(me, aim);
//    }
//
//    public Direction byStraightLine(Point me, Point aim) {
//        DirectionService ds = new DirectionService(me, aim);
//        Direction needed = Direction.STOP;
//        if (ds.isBelow() && ds.isOnRight()) {
//            if (ds.isCloserX()) needed = Direction.UP;
//            else needed = Direction.LEFT;
//        } else if (!ds.isBelow() && ds.isOnRight()) {
//            if (ds.isCloserX()) needed = Direction.DOWN;
//            else needed = Direction.LEFT;
//        } else if (ds.isBelow() && !ds.isOnRight()) {
//            if (ds.isCloserX()) needed = Direction.UP;
//            else needed = Direction.RIGHT;
//        } else if (!ds.isBelow() && !ds.isOnRight()) {
//            if (ds.isCloserX()) needed = Direction.DOWN;
//            else needed = Direction.RIGHT;
//        } else if (ds.isOnX()) {
//            if (ds.isOnRight()) needed = Direction.LEFT;
//            else needed = Direction.RIGHT;
//        } else if (ds.isOnY()) {
//            if (ds.isBelow()) needed = Direction.UP;
//            else needed = Direction.DOWN;
//        }
//        return needed;
//    }
//
//    public Direction changeDirection(Direction current, Direction needed) {
//        int[][] rules = {{0, 1, 1, -1},
//                {-1, 0, -1, 1},
//                {-1, 1, 0, 1},
//                {1, -1, 1, 0}};
//        if (current.value() <= 3 && needed.value() <= 3) {
//            if (rules[current.value()][needed.value()] < 0) return turnLeft();
//            else if (rules[current.value()][needed.value()] > 0) return turnRight();
//        }
//        return current;
//    }
//
//    public List<Point> getHamiltonPath() {
//        int xlimit = 12;
//        int ylimit = 13;
//        int x = 1;
//        int y = 1;
//        List<Point> pathList = new ArrayList<>();
//        pathList.add(new PointImpl(x, y));
//        while (x < xlimit) {
//            while (y < ylimit) {
//                y += 1;
//                pathList.add(new PointImpl(x, y));
//            }
//            x += 1;
//            pathList.add(new PointImpl(x, y));
//            if (x == xlimit) break;
//            while (y > 2) {
//                y -= 1;
//                pathList.add(new PointImpl(x, y));
//            }
//            x += 1;
//            pathList.add(new PointImpl(x, y));
//        }
//        while (y > 1) {
//            y -= 1;
//            pathList.add(new PointImpl(x, y));
//        }
//        while (x > 2) {
//            x -= 1;
//            pathList.add(new PointImpl(x, y));
//        }
//        return pathList;
//    }

    public static void main(String[] args) {
        String url = "http://64.226.126.93/codenjoy-contest/board/player/x8binh3lbui73a0maxqq?code=4533984493249419084";
        MySolver solver = new MySolver();
        Board board = new Board();
        WebSocketRunner.runClient(url, solver, board);
    }
}
