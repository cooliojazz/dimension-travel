package com.up.dt.dimension.expression;

import com.up.dt.dimension.math.RealityMatrix;

public class CompositionOperator implements Operator.BinaryOperator<RealityMatrix, RealityMatrix, RealityMatrix> {

    public RealityMatrix operate(RealityMatrix a, RealityMatrix b) {
        return a.compose(b);
    }

}
