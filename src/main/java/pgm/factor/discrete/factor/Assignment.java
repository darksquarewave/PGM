package pgm.factor.discrete.factor;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public final class Assignment implements Comparable<Assignment> {

    private final AssignmentIndex index;
    private final double value;
    private static final double EPSILON = 1e-12;

    Assignment(final AssignmentIndex i, final double val) {
        this.index = i;
        this.value = val;
    }

    public AssignmentIndex index() {
        return index;
    }

    public Assignment index(final AssignmentIndex newIndex) {
        return new Assignment(newIndex, value);
    }

    public double value() {
        return value;
    }

    public Assignment value(final double newValue) {
        return new Assignment(index, newValue);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Assignment that = (Assignment) o;

        return Math.abs(that.value - value) < EPSILON
                && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, value);
    }

    @Override
    public int compareTo(final Assignment i) {
        return index.compareTo(i.index);
    }
}

