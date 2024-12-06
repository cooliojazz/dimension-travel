package com.up.dt.dimension;

import com.up.dt.dimension.math.RealityMatrix;
import com.up.dt.dimension.math.RealityVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class RealityMatrixTest {

    @Test
    public void testSize() {
        RealityMatrix m = new RealityMatrix(new double[1]);
        Assertions.assertEquals(1, m.size());
    }

    @Test
    public void testEquals() {
        RealityMatrix m1 = new RealityMatrix(new double[] {0, 1}, new double[] {2, 3});
        Assertions.assertTrue(m1.equals(m1));

        RealityMatrix m2 = new RealityMatrix(new double[] {0, 1}, new double[] {2, 3});
        Assertions.assertTrue(m1.equals(m2));

        Assertions.assertFalse(m1.equals(""));

        RealityMatrix m3 = new RealityMatrix(new double[] {0, 1}, new double[] {1, 2});
        Assertions.assertFalse(m1.equals(m3));
        Assertions.assertFalse(m2.equals(m3));
    }

    @Test
    public void testIdentity() {
        RealityMatrix m1 = new RealityMatrix(new double[] {1});
        Assertions.assertEquals(m1, RealityMatrix.identity(1));

        RealityMatrix m2 = new RealityMatrix(new double[] {1, 0}, new double[] {0, 1});
        Assertions.assertEquals(m2, RealityMatrix.identity(2));
    }

    @Test
    public void simpleVectorMatrix() {
        RealityVector v1 = new RealityVector(1, 0);

        RealityMatrix m1 = new RealityMatrix(new double[] {0, 1}, new double[] {-1, 0});
        Assertions.assertEquals(new RealityVector(0, 1), m1.apply(v1));

        RealityMatrix m2 = new RealityMatrix(new double[] {1, 1}, new double[] {-1, 0});
        Assertions.assertEquals(new RealityVector(1, 1), m2.apply(v1));

        RealityVector v2 = new RealityVector(1);
        Assertions.assertThrows(RuntimeException.class, () -> m1.apply(v2));
    }

    @Test
    public void simpleMatrixMatrix() {
        RealityMatrix m1 = new RealityMatrix(new double[] {0, 1}, new double[] {-1, 0});

        RealityMatrix m2 = new RealityMatrix(new double[] {1, 1}, new double[] {-1, -1});
        Assertions.assertEquals(new RealityMatrix(new double[] {-1, 1}, new double[] {1, -1}), m1.compose(m2));

        RealityMatrix m3 = new RealityMatrix(new double[] {-1, 0}, new double[] {0, 1});
        Assertions.assertEquals(new RealityMatrix(new double[] {0, -1}, new double[] {-1, 0}), m1.compose(m3));

        RealityMatrix m4 = RealityMatrix.identity(1);
        Assertions.assertThrows(RuntimeException.class, () -> m1.compose(m4));
    }
}
