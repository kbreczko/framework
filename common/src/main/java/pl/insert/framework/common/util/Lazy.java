package pl.insert.framework.common.util;

import java.util.Optional;
import java.util.function.Supplier;

public final class Lazy<T> {
    private volatile Optional<T> value = Optional.empty();
    private final Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T getOrCompute() {
        return value.orElseGet(this::maybeCompute);
    }

    private synchronized T maybeCompute() {
        value = Optional.of(value.orElse(supplier.get()));
        return value.get();
    }
}