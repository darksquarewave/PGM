package pgm.factor.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public final class RandomVariable<T extends Comparable<T>> implements Comparable<RandomVariable<T>> {

    private final T id;

    private final int cardinality;

    public RandomVariable(final T varId, final int varCard) {
        id = varId;
        cardinality = varCard;
    }

    public T id() {
        return id;
    }

    public int cardinality() {
        return cardinality;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RandomVariable<?> that = (RandomVariable<?>) obj;

        boolean equals = Objects.equals(id, that.id);
        if (equals && !Objects.equals(cardinality, that.cardinality)) {
            throw new IllegalStateException(
                    "Two equal random variables with different dimensions");
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(final RandomVariable<T> o) {
        return id.compareTo(o.id);
    }

}

