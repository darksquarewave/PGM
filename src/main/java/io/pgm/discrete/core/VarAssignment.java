package io.pgm.discrete.core;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Objects;

public final class VarAssignment<T extends Comparable<T>, E> implements Serializable,
        Comparable<VarAssignment<T, E>> {

    private static final long serialVersionUID = -7957857059339996975L;

    private final RandomVariable<T, E> randomVariable;
    private final E event;

    VarAssignment(final RandomVariable<T, E> assignmentVar, final E assignmentEvent) {
        randomVariable = assignmentVar;
        event = assignmentEvent;
    }

    public RandomVariable<T, E> randomVariable() {
        return randomVariable;
    }

    public E event() {
        return event;
    }

    int index() {
        int i = 0;

        for (E e : randomVariable.eventSpace()) {
            if (event != null && event.equals(e)) {
                return i;
            }
            i++;
        }

        throw new AssertionError("Target event is not in the event space");
    }

    @Override
    public int compareTo(final VarAssignment<T, E> variableAssignment) {
        return randomVariable.compareTo(variableAssignment.randomVariable);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        VarAssignment<?, ?> that = (VarAssignment<?, ?>) obj;

        return Objects.equals(randomVariable, that.randomVariable)
                && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariable, event);
    }

    @Override
    public String toString() {
        return "VariableAssignment(" + randomVariable.id() + "=" + event + ")";
    }
}

