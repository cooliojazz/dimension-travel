package com.up.dt.dimension.expression;

public class Expression<T> implements Operator.NullaryOperator<T> {

    private Operator<T> op;
    private Operator.NullaryOperator<?>[] params;

    public Expression(Operator<T> op, Operator.NullaryOperator<?>... params) {
        if (op.parameterCount() != params.length) throw new RuntimeException("Parameter counts don't match.");
        this.op = op;
        this.params = params;
    }

    @Override
    public T operate() {
        Object[] vals = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            vals[i] = params[i].operate();
        }
        return op.operate(vals);
    }

    @Override
    public int parameterCount() {
        return params.length;
    }

    public static class UnaryExpression<T, U> extends Expression<T> {

        public UnaryExpression(UnaryOperator<T, U> op, Operator.NullaryOperator<U> a) {
            super(op, a);
        }

    }

    public static class BinaryExpression<T, U, V> extends Expression<T> {

        public BinaryExpression(BinaryOperator<T, U, V> op, Operator.NullaryOperator<U> a, Operator.NullaryOperator<V> b) {
            super(op, a, b);
        }
    }
}
