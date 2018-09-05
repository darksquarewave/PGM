package io.pgm.discrete.core;

import com.google.common.collect.Iterables;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MultiVarAssignment implements Comparable<MultiVarAssignment>, Serializable {

    private static final long serialVersionUID = -2373257928740712783L;

    private final Map<RandomVariable, VarAssignment> assignments;
    private final int index;

    private MultiVarAssignment(final Builder builder) {
        this(builder.varAssignments);
    }

    MultiVarAssignment(final Iterable<? extends VarAssignment> varAssignments) {
        Iterator<? extends VarAssignment> iterator = varAssignments.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException();
        }

        Map<RandomVariable, VarAssignment> map = new LinkedHashMap<>();

        while (iterator.hasNext()) {
            VarAssignment varAssignment = iterator.next();
            map.put(varAssignment.randomVariable(), varAssignment);
        }

        assignments = Collections.unmodifiableMap(map);
        index = AssignmentUtils.assignmentIndex(map.values());
    }

    private MultiVarAssignment(final Map<RandomVariable, VarAssignment> map) {
        assignments = Collections.unmodifiableMap(map);
        index = AssignmentUtils.assignmentIndex(map.values());
    }

    public Assignment toAssignment(final double val) {
        return new Assignment(this, val);
    }

    public Collection<? extends RandomVariable> randomVariables() {
        return assignments.keySet();
    }

    public MultiVarAssignment retainAll(final Collection<? extends RandomVariable> randomVars) {
        Map<RandomVariable, VarAssignment> copy = new LinkedHashMap<>(assignments);
        copy.keySet().retainAll(randomVars);

        return new MultiVarAssignment(copy);
    }

    public MultiVarAssignment excludeAll(final Collection<? extends RandomVariable> randomVars) {
        Map<RandomVariable, VarAssignment> excluded = new LinkedHashMap<>(assignments);
        for (RandomVariable randomVar : randomVars) {
            excluded.remove(randomVar);
        }

        return new MultiVarAssignment(excluded);
    }

    public Optional<VarAssignment> get(final RandomVariable randomVariable) {
        return Optional.ofNullable(assignments.get(randomVariable));
    }

    public MultiVarAssignment and(final MultiVarAssignment varAssignment) {
        return new MultiVarAssignment(Iterables.concat(assignments.values(), varAssignment.assignments()));
    }

    public Collection<? extends VarAssignment> assignments() {
        return assignments.values();
    }

    public int index() {
        return index;
    }

    @Override
    public int compareTo(final MultiVarAssignment assignment) {
        return Integer.compare(index, assignment.index);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MultiVarAssignment that = (MultiVarAssignment) obj;

        return Objects.equals(assignments, that.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignments);
    }

    @Override
    public String toString() {
        String result = assignments.values().stream()
                .map(assignment -> assignment.randomVariable().label() + "=" + assignment.event())
                .collect(Collectors.joining(", "));

        return "MultiVarAssignment(" + result + ")";
    }

    public static Builder builder() {
        return new Builder();
    }


    public boolean contains(final MultiVarAssignment subAssignment) {
        return assignments.entrySet().containsAll(subAssignment.assignments.entrySet());
    }

    public static final class Builder {

        private final List<VarAssignment> varAssignments = new ArrayList<>();

        public Builder add(final VarAssignment value) {
            varAssignments.add(value);
            return this;
        }

        public Builder add(final Collection<? extends VarAssignment> value) {
            varAssignments.addAll(value);
            return this;
        }

        public MultiVarAssignment build() {
            return new MultiVarAssignment(this);
        }
    }
}
