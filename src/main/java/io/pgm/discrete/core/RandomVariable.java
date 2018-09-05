package io.pgm.discrete.core;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RandomVariable<T extends Comparable<T>, E> implements
        Comparable<RandomVariable<T, E>>,
        Serializable {

    private static final long serialVersionUID = -4515022937413013557L;

    private final T label;
    private final Collection<? extends E> eventSpace;

    public static <T extends Comparable<T>> Builder<T> label(final T label) {
        return new Builder<>(label);
    }

    public static final class Builder<T extends Comparable<T>> {

        private final T label;

        private Builder(final T varLabel) {
            label = varLabel;
        }

        public <E> RandomVarBuilder<T, E> event(final E event) {
            return new RandomVarBuilder<>(label, event);
        }

        public <E> RandomVarBuilder<T, E> events(final Collection<? extends E> events) {
            return new RandomVarBuilder<>(label, events);
        }

        @SafeVarargs
        public final <E> RandomVarBuilder<T, E> events(final E e0, final E ... rest) {
            List<E> events = new ArrayList<>();
            events.add(e0);
            if (rest.length > 0) {
                events.addAll(Arrays.asList(rest));
            }
            return new RandomVarBuilder<>(label, events);
        }

        public <E> RandomVarBuilder<T, E> events(final E[] events) {
            return new RandomVarBuilder<>(label, Arrays.asList(events));
        }
    }

    public static final class RandomVarBuilder<T extends Comparable<T>, E> {

        private final T label;
        private final List<E> events = new ArrayList<>();

        private RandomVarBuilder(final T varLabel, final E varEvent) {
            label = varLabel;
            events.add(varEvent);
        }

        private RandomVarBuilder(final T varLabel, final Collection<? extends E> varEvents) {
            label = varLabel;
            events.addAll(varEvents);
        }

        public RandomVarBuilder<T, E> event(final E event) {
            events.add(event);
            return this;
        }

        public RandomVarBuilder<T, E> events(final Collection<? extends E> eventsColl) {
            events.addAll(eventsColl);
            return this;
        }

        public RandomVarBuilder<T, E> events(final E[] eventsArr) {
            events.addAll(Arrays.asList(eventsArr));
            return this;
        }

        public RandomVariable<T, E> build() {
            return new RandomVariable<>(this);
        }
    }

    private RandomVariable(final RandomVarBuilder<T, E> builder) {
        this.label = builder.label;
        if (builder.events.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.eventSpace = Collections.unmodifiableCollection(builder.events);
    }

    public T label() {
        return label;
    }

    public Collection<? extends E> eventSpace() {
        return eventSpace;
    }

    public int cardinality() {
        return eventSpace.size();
    }

    public MultiVarAssignment set(final E event) {
        if (!eventSpace.contains(event)) {
            throw new IllegalArgumentException("Event is not in the event space");
        }

        VarAssignment<T, E> varAssignment = new VarAssignment<>(this, event);
        return new MultiVarAssignment(Collections.singletonList(varAssignment));
    }

    VarAssignment<T, E> setIndex(final int eventIndex) {
        if (eventIndex < 0 || eventIndex >= eventSpace.size()) {
            throw new IllegalArgumentException("Invalid index");
        }

        int i = 0;
        for (E e : eventSpace) {
            if (i == eventIndex) {
                return new VarAssignment<>(this, e);
            }
            i++;
        }

        throw new AssertionError("Unreachable statement");
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        RandomVariable<?, ?> that = (RandomVariable<?, ?>) obj;

        boolean equals = Objects.equals(label, that.label);

        if (equals && !Objects.equals(eventSpace, that.eventSpace)) {
            throw new IllegalStateException("Two equal random variables with different event spaces");
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return "RandomVar(label=" + label + ", card=" + cardinality() + ")";
    }

    @Override
    public int compareTo(final RandomVariable<T, E> randomVariable) {
        return label.compareTo(randomVariable.label);
    }
}
