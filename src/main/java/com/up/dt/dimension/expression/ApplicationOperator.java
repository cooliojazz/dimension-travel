package com.up.dt.dimension.expression;

import com.up.dt.dimension.math.RealityMatrix;
import com.up.dt.dimension.math.RealityVector;

public class ApplicationOperator implements Operator.BinaryOperator<RealityVector, RealityMatrix, RealityVector> {

    public RealityVector operate(RealityMatrix a, RealityVector b) {
        return a.apply(b);
    }

}
