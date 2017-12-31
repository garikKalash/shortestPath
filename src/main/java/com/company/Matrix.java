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
    private final List<ArrayList<Sell>> matrix;
    private Set<Sell> disabledSells = new HashSet<>();


    public Matrix(int x, int y) {
        this.length = x;
        this.width = y;

        matrix = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            List<Sell> column = new ArrayList<>();
            for (int j = 0; j < width; ++j) {
                column.add(new Sell(Pair.of(i, j)));
            }
            matrix.add((ArrayList<Sell>) column);
        }
    }

    public void fillDisabledSells(@NotNull Set<Sell> sells) {
        disabledSells = sells;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public void showShortestPath(Pair<Integer, Integer> source, Pair<Integer, Integer> destination) {
        Set<Sell> start = new HashSet<>();
        start.add(new Sell().of(source.getLeft(), source.getRight()));

        Set<Sell> shortestPath = getShortestPathInSells(-1, new HashSet<Sell>(), start, destination, source);

        TreeSet<Sell> sortedSells = new TreeSet<>(new Comparator<Sell>() {
            @Override
            public int compare(Sell o1, Sell o2) {
                return o1.distanceFromSource - o2.distanceFromSource;
            }
        });
        sortedSells.addAll(shortestPath);

        for(Sell sell: sortedSells){
            System.out.println(sell);
        }
    }

    private Set<Sell> getShortestPathInSells(int step , HashSet<Sell> path, Set<Sell> sells, Pair<Integer, Integer> destination, Pair<Integer,Integer> source) {
        ++step;
        Set<Sell> neighbor = new HashSet<>();
        Sell sellOperation = new Sell();
        Sell destinationSell = sellOperation.of(destination.getLeft(),destination.getRight());

        resolveDistancesOfSells(step,sells);


        for (Sell sell : sells) {
            if (canBeTheNextStepOfPath(sell.right(),source)){
                Sell right = sell.right();
                if(right.equals(destinationSell)){
                    resolveSellTrace(sell, path, sellOperation.of(source.getLeft(), source.getRight()));
                   return path;
                }
                neighbor.add(right);
            }
            if (canBeTheNextStepOfPath(sell.up(),source)){
                Sell up = sell.up();
                if(up.equals(destinationSell)){
                    resolveSellTrace(sell, path, sellOperation.of(source.getLeft(), source.getRight()));
                    return path;
                }
                neighbor.add(up);
            }
            if (canBeTheNextStepOfPath(sell.left(),source)){
                Sell left = sell.left();
                if(left.equals(destinationSell)){
                    resolveSellTrace(sell, path, sellOperation.of(source.getLeft(), source.getRight()));
                    return path;
                }
                neighbor.add(left);
            }
            if (canBeTheNextStepOfPath(sell.down(),source)){
                Sell down = sell.down();
                if(down.equals(destinationSell)){
                    resolveSellTrace(sell, path, sellOperation.of(source.getLeft(), source.getRight()));
                    return path;
                }
                neighbor.add(down);
            }
        }
        return getShortestPathInSells(step, path, neighbor,destination,source);
    }

    private void resolveSellTrace(Sell sell, HashSet<Sell> trace,Sell source){
        if(trace.isEmpty()){
            trace.add(sell);
        }
        int dest = sell.distanceFromSource;
        if(canBeThePreviousStepOfPath(sell.up(),dest)){
            if(sell.up().equals(source)){
                return;
            }
            trace.add(sell.up());
            resolveSellTrace(sell.up(),trace,source);
        }else if(canBeThePreviousStepOfPath(sell.down(),dest)){
            if(sell.down().equals(source)){
                return;
            }
            trace.add(sell.down());
            resolveSellTrace(sell.down(),trace,source);
        }else if(canBeThePreviousStepOfPath(sell.right(),dest)){
            if(sell.right().equals(source)){
                return;
            }
            trace.add(sell.right());
            resolveSellTrace(sell.right(),trace,source);
        }else if(canBeThePreviousStepOfPath(sell.left(),dest)){
            if(sell.left().equals(source)){
                return;
            }
            trace.add(sell.left());
            resolveSellTrace(sell.left(),trace,source);
        }


    }

    private boolean canBeThePreviousStepOfPath(Sell sell, int dest){
        return sell != null && !disabledSells.contains(sell) && sell.distanceFromSource - dest == -1;
    }

    private boolean canBeTheNextStepOfPath(Sell sell,Pair<Integer,Integer> source){
        return  sell != null
                && !disabledSells.contains(sell)
                && !sell.equals(new Sell().of(source.getLeft(),source.getRight()))
                && sell.distanceFromSource == 0;
    }

    private void resolveDistancesOfSells(int distance,Set<Sell> sells){
        for(Sell sell: sells){
            sell.setDistance(sell.coordinate.getLeft(), sell.coordinate.getRight(),distance);
        }

    }

    Sell sell(Pair<Integer, Integer> pair){
        return new Sell().of(pair.getLeft(), pair.getRight());
    }


    class Sell{
        private Pair<Integer, Integer> coordinate;
        private int distanceFromSource = 0;

        Sell of(Integer left, Integer right){
            return matrix.get(left).get(right);
        }

        void setDistance(Integer left, Integer right, int dist) {
            of(left,right).distanceFromSource = dist;
        }

        Sell(Pair<Integer, Integer> coordinate) {
            this.coordinate = coordinate;
        }

        Sell(){}


        Pair<Integer, Integer> getCoordinate() {
            return coordinate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Sell)) return false;
            Sell sell = (Sell) o;
            return getCoordinate().equals(sell.getCoordinate());
        }

        @Override
        public int hashCode() {
            return getCoordinate().hashCode();
        }

        @Override
        public String toString() {
            return "Sell{" +
                    "coordinate=" + coordinate +
                    ", distanceFromSource=" + distanceFromSource +
                    '}';
        }

        Sell up(){
            return getCoordinate().getRight() != width - 1 ?
                    of(getCoordinate().getLeft(), getCoordinate().getRight() + 1)
                    : null;
        }

        Sell down(){
             return getCoordinate().getRight() != 0 ?
                     of(getCoordinate().getLeft(), getCoordinate().getRight() - 1)
                     : null;
        }

        Sell left(){
            return getCoordinate().getLeft() != 0 ?
                    of(getCoordinate().getLeft() - 1, getCoordinate().getRight())
                    : null;
        }

        Sell right(){
            return getCoordinate().getLeft() != length - 1 ?
                    of(getCoordinate().getLeft() + 1, getCoordinate().getRight())
                    : null;
        }
    }




}
