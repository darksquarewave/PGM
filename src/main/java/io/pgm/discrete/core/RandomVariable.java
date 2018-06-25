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

    private final T id;
    private final Collection<? extends E> eventSpace;

    public static IdBuilder builder() {
        return new IdBuilder();
    }

    public static final class IdBuilder {

        private IdBuilder() {
        }

        public <T extends Comparable<T>> EventSpaceBuilder<T> id(final T id) {
            return new EventSpaceBuilder<>(id);
        }
    }

    public static final class EventSpaceBuilder<T extends Comparable<T>> {

        private final T id;

        private EventSpaceBuilder(final T varId) {
            this.id = varId;
        }

        public <E> RandomVarBuilder<T, E> event(final E event) {
            return new RandomVarBuilder<>(id, event);
        }

        public <E> RandomVarBuilder<T, E> events(final Collection<? extends E> events) {
            return new RandomVarBuilder<>(id, events);
        }

        @SafeVarargs
        public final <E> RandomVarBuilder<T, E> events(final E e0, final E ... rest) {
            List<E> events = new ArrayList<>();
            events.add(e0);
            if (rest.length > 0) {
                events.addAll(Arrays.asList(rest));
            }
            return new RandomVarBuilder<>(id, events);
        }

        public <E> RandomVarBuilder<T, E> events(final E[] events) {
            return new RandomVarBuilder<>(id, Arrays.asList(events));
        }
    }

    public static final class RandomVarBuilder<T extends Comparable<T>, E> {

        private final T id;
        private final List<E> events = new ArrayList<>();

        private RandomVarBuilder(final T varId, final E varEvent) {
            id = varId;
            events.add(varEvent);
        }

        private RandomVarBuilder(final T varId, final Collection<? extends E> varEvents) {
            id = varId;
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
        this.id = builder.id;
        this.eventSpace = Collections.unmodifiableCollection(builder.events);
    }

    public T id() {
        return id;
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

        boolean equals = Objects.equals(id, that.id);

        if (equals && !Objects.equals(eventSpace, that.eventSpace)) {
            throw new IllegalStateException("Two equal random variables with different event spaces");
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RandomVar(id=" + id + ", card=" + cardinality() + ")";
    }

    @Override
    public int compareTo(final RandomVariable<T, E> randomVariable) {
        return id.compareTo(randomVariable.id);
    }

}

