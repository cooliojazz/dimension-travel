package com.up.dt.dimension.expression;

public interface Operator<T> {

    public T operate(Object... params);

    public int parameterCount();

    public static interface NullaryOperator<T> extends Operator<T> {

        public T operate();

        @Override
        public default T operate(Object... params) {
            return operate();
        }

        public default int parameterCount() {
            return 0;
        }
    }

    public static interface UnaryOperator<T, U> extends Operator<T> {

        public T operate(U u);

        @Override
        public default T operate(Object... params) {
            return operate((U)params[0]);
        }

        public default int parameterCount() {
            return 1;
        }
    }

    public static interface BinaryOperator<T, U, V> extends Operator<T> {

        public T operate(U u, V v);

        @Override
        public default T operate(Object... params) {
            return operate((U)params[0], (V)params[1]);
        }

        public default int parameterCount() {
            return 2;
        }
    }
}
