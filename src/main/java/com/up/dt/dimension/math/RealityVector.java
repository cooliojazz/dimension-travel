package com.up.dt.dimension.math;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 *
 * @author Ricky
 */
public class RealityVector {
    
    private final double[] vector;

    public RealityVector(double... vector) {
        this.vector = vector;
    }
    
    public double get(int i) {
        return vector[i];
    }
    
    public double get(RealityDirection dir) {
        return get(dir.ordinal());
    }

    public int size() {
        return vector.length;
    }

    public RealityVector with(RealityDirection dir, double value) {
        double[] vec = Arrays.copyOf(vector, vector.length);
        vec[dir.ordinal()] = value;
        return new RealityVector(vec);
    }

    public RealityVector offset(RealityDirection dir, short value) {
        double[] vec = Arrays.copyOf(vector, vector.length);
        vec[dir.ordinal()] = vector[dir.ordinal()] + value;
        return new RealityVector(vec);
    }

    public double magnitude() {
        double sum = 0;
        for (double s : vector) sum += s * s;
        return Math.sqrt(sum);
    }

    public RealityVector sum(RealityVector other) {
        double[] vec = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            vec[i] = vector[i] + other.vector[i];
        }
        return new RealityVector(vec);
    }

    public RealityVector to(RealityVector other) {
        double[] vec = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            vec[i] = other.vector[i] - vector[i];
        }
        return new RealityVector(vec);
    }

    public double dot(RealityVector other) {
        double sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += other.vector[i] * vector[i];
        }
        return sum;
    }

    public AttractedRealityVector attract() {
        short[] vec = new short[vector.length];
        for (int i = 0; i < vector.length; i++) {
            vec[i] = (short)Math.max(0, Math.min(255, Math.round(vector[i])));
        }
        return new AttractedRealityVector(vec);
    }

    @Override
    public RealityVector clone() {
        return new RealityVector(vector.clone());
    }

    @Override
    public String toString() {
        return "<" + DoubleStream.of(vector).mapToObj(d -> d + "").collect(Collectors.joining(", ")) +  ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RealityVector)) return false;
        return Arrays.equals(vector, ((RealityVector)obj).vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    public static RealityVector random(int length, double min, double max) {
        double[] vector = new double[length];
        for (int i = 0; i < length; i++) {
            vector[i] = Math.random() * (min + max) - min;
        }
        return new RealityVector(vector);
    }

    public static RealityVector zero(int size) {
        return new RealityVector(new double[size]);
    }
}
