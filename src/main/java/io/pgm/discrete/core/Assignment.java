package io.pgm.discrete.core;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public final class Assignment implements Comparable<Assignment>, Serializable {

    private static final long serialVersionUID = 7753872171086728807L;
    private static final double DELTA = 1e-9;

    private final MultiVarAssignment varAssignment;
    private final double value;

    Assignment(final MultiVarAssignment multiAssignment, final double val) {
        varAssignment = multiAssignment;
        value = val;
    }

    public MultiVarAssignment varAssignments() {
        return varAssignment;
    }

    public Assignment varAssignments(final MultiVarAssignment groupAssignment) {
        return new Assignment(groupAssignment, value);
    }

    public double value() {
        return value;
    }

    public Assignment value(final double val) {
        return new Assignment(varAssignment, val);
    }

    public Assignment divide(final double divideVal) {
        return new Assignment(varAssignment, value / divideVal);
    }

    public Assignment multiply(final double multiplyVal) {
        return new Assignment(varAssignment, value * multiplyVal);
    }

    public Assignment plus(final double plusVal) {
        return new Assignment(varAssignment, value + plusVal);
    }

    public Assignment minus(final double minusVal) {
        return new Assignment(varAssignment, value - minusVal);
    }

    public Collection<? extends RandomVariable> randomVariables() {
        return varAssignments().randomVariables();
    }

    @Override
    public String toString() {
        return varAssignment.toString() + "=" + value;
    }

    @Override
    public int compareTo(final Assignment assignment) {
        return varAssignment.compareTo(assignment.varAssignment);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Assignment that = (Assignment) obj;

        return Math.abs(value - that.value) < DELTA
            && Objects.equals(varAssignment, that.varAssignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varAssignment);
    }
}

