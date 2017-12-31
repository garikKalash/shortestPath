package com.company;

import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by Garik Kalashyan on 31-Dec-17.
 */
public class Matrix {
    private final int length;
    private final int width;
    private final List<ArrayList<Cell>> matrix;
    private Set<Cell> disabledCells = new HashSet<>();


    public Matrix(int x, int y) {
        this.length = x;
        this.width = y;

        matrix = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            List<Cell> column = new ArrayList<>();
            for (int j = 0; j < width; ++j) {
                column.add(new Cell(Pair.of(i, j)));
            }
            matrix.add((ArrayList<Cell>) column);
        }
    }

    public void fillDisabledCells(@NotNull Set<Cell> cells) {
        disabledCells = cells;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public void showShortestPath(Pair<Integer, Integer> source, Pair<Integer, Integer> destination) {
        Set<Cell> start = new HashSet<>();
        start.add(new Cell().of(source.getLeft(), source.getRight()));

        Set<Cell> shortestPath = getShortestPathInCells(-1, new HashSet<Cell>(), start, destination, source);

        TreeSet<Cell> sortedCells = new TreeSet<>(new Comparator<Cell>() {
            @Override
            public int compare(Cell o1, Cell o2) {
                return o1.distanceFromSource - o2.distanceFromSource;
            }
        });
        sortedCells.addAll(shortestPath);

        for(Cell cell: sortedCells){
            System.out.println(cell);
        }
    }

    private Set<Cell> getShortestPathInCells(int step , HashSet<Cell> path, Set<Cell> cells, Pair<Integer, Integer> destination, Pair<Integer,Integer> source) {
        ++step;
        Set<Cell> neighbor = new HashSet<>();
        Cell cellOperation = new Cell();
        Cell destinationCell = cellOperation.of(destination.getLeft(),destination.getRight());

        resolveDistancesOfCells(step,cells);


        for (Cell cell : cells) {
            if (canBeTheNextStepOfPath(cell.right(),source)){
                Cell right = cell.right();
                if(right.equals(destinationCell)){
                    resolveCellTrace(cell, path, cellOperation.of(source.getLeft(), source.getRight()));
                   return path;
                }
                neighbor.add(right);
            }
            if (canBeTheNextStepOfPath(cell.up(),source)){
                Cell up = cell.up();
                if(up.equals(destinationCell)){
                    resolveCellTrace(cell, path, cellOperation.of(source.getLeft(), source.getRight()));
                    return path;
                }
                neighbor.add(up);
            }
            if (canBeTheNextStepOfPath(cell.left(),source)){
                Cell left = cell.left();
                if(left.equals(destinationCell)){
                    resolveCellTrace(cell, path, cellOperation.of(source.getLeft(), source.getRight()));
                    return path;
                }
                neighbor.add(left);
            }
            if (canBeTheNextStepOfPath(cell.down(),source)){
                Cell down = cell.down();
                if(down.equals(destinationCell)){
                    resolveCellTrace(cell, path, cellOperation.of(source.getLeft(), source.getRight()));
                    return path;
                }
                neighbor.add(down);
            }
        }
        return getShortestPathInCells(step, path, neighbor,destination,source);
    }

    private void resolveCellTrace(Cell cell, HashSet<Cell> trace,Cell source){
        if(trace.isEmpty()){
            trace.add(cell);
        }
        int dest = cell.distanceFromSource;
        if(canBeThePreviousStepOfPath(cell.up(),dest)){
            if(cell.up().equals(source)){
                return;
            }
            trace.add(cell.up());
            resolveCellTrace(cell.up(),trace,source);
        }else if(canBeThePreviousStepOfPath(cell.down(),dest)){
            if(cell.down().equals(source)){
                return;
            }
            trace.add(cell.down());
            resolveCellTrace(cell.down(),trace,source);
        }else if(canBeThePreviousStepOfPath(cell.right(),dest)){
            if(cell.right().equals(source)){
                return;
            }
            trace.add(cell.right());
            resolveCellTrace(cell.right(),trace,source);
        }else if(canBeThePreviousStepOfPath(cell.left(),dest)){
            if(cell.left().equals(source)){
                return;
            }
            trace.add(cell.left());
            resolveCellTrace(cell.left(),trace,source);
        }


    }

    private boolean canBeThePreviousStepOfPath(Cell cell, int dest){
        return cell != null && !disabledCells.contains(cell) && cell.distanceFromSource - dest == -1;
    }

    private boolean canBeTheNextStepOfPath(Cell cell,Pair<Integer,Integer> source){
        return  cell != null
                && !disabledCells.contains(cell)
                && !cell.equals(new Cell().of(source.getLeft(),source.getRight()))
                && cell.distanceFromSource == 0;
    }

    private void resolveDistancesOfCells(int distance,Set<Cell> cells){
        for(Cell cell: cells){
            cell.setDistance(cell.coordinate.getLeft(), cell.coordinate.getRight(),distance);
        }

    }

    Cell cell(Pair<Integer, Integer> pair){
        return new Cell().of(pair.getLeft(), pair.getRight());
    }


    class Cell{
        private Pair<Integer, Integer> coordinate;
        private int distanceFromSource = 0;

        Cell of(Integer left, Integer right){
            return matrix.get(left).get(right);
        }

        void setDistance(Integer left, Integer right, int dist) {
            of(left,right).distanceFromSource = dist;
        }

        Cell(Pair<Integer, Integer> coordinate) {
            this.coordinate = coordinate;
        }

        Cell(){}


        Pair<Integer, Integer> getCoordinate() {
            return coordinate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Cell)) return false;
            Cell cell = (Cell) o;
            return getCoordinate().equals(cell.getCoordinate());
        }

        @Override
        public int hashCode() {
            return getCoordinate().hashCode();
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "coordinate=" + coordinate +
                    ", distanceFromSource=" + distanceFromSource +
                    '}';
        }

        Cell up(){
            return getCoordinate().getRight() != width - 1 ?
                    of(getCoordinate().getLeft(), getCoordinate().getRight() + 1)
                    : null;
        }

        Cell down(){
             return getCoordinate().getRight() != 0 ?
                     of(getCoordinate().getLeft(), getCoordinate().getRight() - 1)
                     : null;
        }

        Cell left(){
            return getCoordinate().getLeft() != 0 ?
                    of(getCoordinate().getLeft() - 1, getCoordinate().getRight())
                    : null;
        }

        Cell right(){
            return getCoordinate().getLeft() != length - 1 ?
                    of(getCoordinate().getLeft() + 1, getCoordinate().getRight())
                    : null;
        }
    }




}
