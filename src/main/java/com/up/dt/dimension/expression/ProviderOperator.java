package com.up.dt.dimension.expression;

public class ProviderOperator<T> implements Operator.NullaryOperator<T> {

    private T value;

    public ProviderOperator(T value) {
        this.value = value;
    }

    @Override
    public T operate() {
        return value;
    }
}
