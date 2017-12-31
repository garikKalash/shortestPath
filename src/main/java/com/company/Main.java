package com.company;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Garik
 */

public class Main {

    public static void main(String[] args) {
        Matrix  matrix = new Matrix(4,5);
        Set<Matrix.Cell> disableCells = new HashSet<>();

        disableCells.add(matrix.cell(Pair.of(2,2)));
        disableCells.add(matrix.cell(Pair.of(1,2)));

        matrix.fillDisabledCells(disableCells);

        matrix.showShortestPath(Pair.of(3,1),Pair.of(1,3));


    }
}
