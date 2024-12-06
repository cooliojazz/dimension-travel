package com.up.dt.dimension.math;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Only allows square matrices
 */
public class RealityMatrix {

    // column-major
    private final double[][] matrix;

    public RealityMatrix(double[]... matrix) {
        this.matrix = matrix;
    }

    public int size() {
        return matrix.length;
    }

    public RealityMatrix compose(RealityMatrix mat) {
        if (mat.size() != matrix.length) throw new RuntimeException("Matrix sizes must match!");
        double[][] mult = new double[mat.size()][mat.size()];
        for (int i = 0; i < mat.size(); i++) {
            for (int j = 0; j < mat.size(); j++) {
                double sum = 0;
                for (int k = 0; k < mat.size(); k++) {
                    sum += matrix[k][j] * mat.matrix[i][k];
                }
                mult[i][j] = sum;
            }
        }
        return new RealityMatrix(mult);
    }

    public RealityVector apply(RealityVector vec) {
        if (vec.size() != matrix.length) throw new RuntimeException("Vector and matrix sizes must match!");
        double[] mult = new double[vec.size()];
        for (int i = 0; i < vec.size(); i++) {
            double sum = 0;
            for (int j = 0; j < vec.size(); j++) {
                sum += matrix[j][i] * vec.get(j);
            }
            mult[i] = sum;
        }
        return new RealityVector(mult);
    }

    @Override
    public String toString() {
        int longest = (int)Stream.of(matrix).flatMapToDouble(DoubleStream::of).map(Math::abs).map(Math::log10).max().orElse(0) + 1 + 3;
        return "[" + Stream.of(matrix).map(r -> DoubleStream.of(r).mapToObj(d -> String.format("%1$ " + longest + ".1f", d)).collect(Collectors.joining(" "))).collect(Collectors.joining("\n ")) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RealityMatrix m) {
            return Arrays.deepEquals(matrix, m.matrix);
        }
        return false;
    }

    public static RealityMatrix identity(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            matrix[i][i] = 1;
        }
        return new RealityMatrix(matrix);
    }
}
