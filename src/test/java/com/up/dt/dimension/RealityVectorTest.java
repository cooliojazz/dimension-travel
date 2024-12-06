package com.up.dt.dimension;

import com.up.dt.dimension.expression.ApplicationOperator;
import com.up.dt.dimension.expression.CompositionOperator;
import com.up.dt.dimension.expression.Expression;
import com.up.dt.dimension.expression.ProviderOperator;
import com.up.dt.dimension.math.RealityDirection;
import com.up.dt.dimension.math.RealityMatrix;
import com.up.dt.dimension.math.RealityVector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RealityVectorTest {

    @Test
    public void testSize() {
        RealityVector v = new RealityVector(1);
        Assertions.assertEquals(1, v.size());
    }

    @Test
    public void testGet() {
        double[] arr = new double[] {0, 1, 2, 3};
        RealityVector v = new RealityVector(arr);
        Assertions.assertAll(IntStream.iterate(0, i -> i < v.size(), i -> ++i).mapToObj(i -> () -> Assertions.assertEquals(arr[i], v.get(i))));
    }

    @Test
    public void testGetDirection() {
        RealityVector v = new RealityVector(Stream.of(RealityDirection.values()).mapToDouble(Enum::ordinal).toArray());
        Assertions.assertAll(IntStream.iterate(0, i -> i < v.size(), i -> ++i).mapToObj(i -> () -> Assertions.assertEquals(v.get(i), v.get(RealityDirection.values()[i]))));
    }

    @Test
    public void testEquals() {
        RealityVector v1 = new RealityVector(0, 1, 2, 3);
        Assertions.assertTrue(v1.equals(v1));

        RealityVector v2 = new RealityVector(0, 1, 2, 3);
        Assertions.assertTrue(v1.equals(v2));

        Assertions.assertFalse(v1.equals(""));

        RealityVector v3 = new RealityVector(0, 1, 1, 2);
        Assertions.assertFalse(v1.equals(v3));
        Assertions.assertFalse(v2.equals(v3));


        Expression<RealityVector> exp = new Expression.BinaryExpression<>(new ApplicationOperator(), new ProviderOperator<>(RealityMatrix.identity(2)), new ProviderOperator<>(new RealityVector(1, 2)));
        RealityVector result = exp.operate();
        Expression<RealityMatrix> exp2 = new Expression.BinaryExpression<>(new CompositionOperator(), new ProviderOperator<>(RealityMatrix.identity(2)), new ProviderOperator<>(new RealityMatrix(new double[] {1, 2}, new double[] {-3, 4})));
        RealityMatrix result2 = exp2.operate();
        int i = 0;
    }

    @Test
    public void testClone() {
        RealityVector v1 = new RealityVector(1, 2, 3, 4, 5);
        RealityVector v2 = v1.clone();

        Assertions.assertNotSame(v1, v2);
        Assertions.assertArrayEquals(getInternal(v1), getInternal(v2));
    }

    @Test
    public void testZero() {
        RealityVector v = RealityVector.zero(10);
        Assertions.assertArrayEquals(new double[10], getInternal(v));
    }

    private static double[] getInternal(RealityVector vec) {
        return IntStream.iterate(0, i -> i < vec.size(), i -> ++i).mapToDouble(vec::get).toArray();
    }
}
