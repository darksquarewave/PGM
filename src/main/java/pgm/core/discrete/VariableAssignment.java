package pgm.core.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public final class VariableAssignment<T extends Comparable<T>, E> implements Serializable,
        JoiningOperation<VariableAssignment, VariableAssignment.JoiningBuilder> {

    private static final long serialVersionUID = -7957857059339996975L;

    private final RandomVariable<T, E> randomVariable;
    private final E event;

    VariableAssignment(final RandomVariable<T, E> assignmentVar, final E assignmentEvent) {
        randomVariable = assignmentVar;
        event = assignmentEvent;
    }

    @Override
    public JoiningBuilder and(final VariableAssignment variableAssignment) {
        return new JoiningBuilder(this, variableAssignment);
    }

    public static final class JoiningBuilder extends AbstractJoiningBuilder<VariableAssignment, JoiningBuilder> {

        public JoiningBuilder(final VariableAssignment ... assignments) {
            super(Arrays.asList(assignments));
        }

        @Override
        public JoiningBuilder and(final VariableAssignment variableAssignment) {
            add(variableAssignment);
            return this;
        }

        public VariableGroupAssignment toGroupAssignment() {
            return VariableGroupAssignment.of(items());
        }

        public Assignment toAssignment(final double value) {
            return toGroupAssignment().toAssignment(value);
        }
    }

    public VariableGroupAssignment toGroupAssignment() {
        return VariableGroupAssignment.of(Collections.singletonList(this));
    }

    public Assignment toAssignment(final double value) {
        return toGroupAssignment().toAssignment(value);
    }

    public RandomVariable<T, E> randomVariable() {
        return randomVariable;
    }

    public E event() {
        return event;
    }

    public int index() {
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
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        VariableAssignment<?, ?> that = (VariableAssignment<?, ?>) obj;

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
