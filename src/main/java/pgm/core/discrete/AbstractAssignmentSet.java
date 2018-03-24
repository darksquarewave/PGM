package pgm.core.discrete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractAssignmentSet {

    private final Set<? extends Assignment> assignments;
    private final Set<? extends RandomVariable> randomVariables;

    public abstract static class AbstractBuilder<E> {

        private final Collection<Assignment> assignments;

        public AbstractBuilder() {
            assignments = new ArrayList<>();
        }

        public final AbstractBuilder<E> add(final Assignment element) {
            assignments.add(element);
            return this;
        }

        public final AbstractBuilder<E> combine(final AbstractBuilder<E> builder) {
            assignments.addAll(builder.assignments);
            return this;
        }

        public final Collection<Assignment> assignments() {
            return assignments;
        }

        public abstract E build();
    }

    protected AbstractAssignmentSet(final Collection<? extends Assignment> assignmentsColl) {
        assignments = Collections.unmodifiableSet(new TreeSet<>(assignmentsColl));
        validate(assignments);

        this.randomVariables = Collections.unmodifiableSet(getRandomVariables(assignments));
    }

    private static void validate(final Set<? extends Assignment> assignments) {
        if (assignments.isEmpty()) {
            throw new IllegalArgumentException("Assignments cannot be empty");
        }

        Set<? extends RandomVariable> randomVariables = getRandomVariables(assignments);

        boolean hasSameVariables = assignments.stream()
                .map(assignment -> assignment.varAssignments().randomVariables())
                .allMatch(randomVariables::equals);

        if (!hasSameVariables) {
            throw new IllegalArgumentException("Random variables mismatch, cannot construct factor");
        }

        Collection<VariableGroupAssignment> groupAssignments = VariableGroupAssignments.of(randomVariables);

        boolean hasCorrectAssignments = assignments.stream()
                .map(Assignment::varAssignments)
                .collect(Collectors.toList())
                .equals(groupAssignments);

        if (!hasCorrectAssignments) {
            throw new IllegalArgumentException("Invalid assignment indexes, cannot construct factor");
        }
    }

    private static Set<? extends RandomVariable> getRandomVariables(final Set<? extends Assignment> assignments) {
        Assignment first = assignments.iterator().next();
        return first.varAssignments().randomVariables();
    }

    public final int cardinality() {
        return randomVariables.stream().map(RandomVariable::cardinality).reduce(1, (a, b) -> a * b);
    }

    public final Stream<? extends Assignment> stream() {
        return StreamSupport.stream(Spliterators.spliterator(assignments, 0), false);
    }

    public final Iterator<? extends Assignment> iterator() {
        return assignments.iterator();
    }

    public final double get(final VariableGroupAssignment varAssignment) {
        Optional<? extends Assignment> opt = stream()
                .filter(assignment -> assignment.varAssignments().equals(varAssignment))
                .findFirst();

        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Cannot find assignment");
        }

        return opt.get().value();
    }

    public final Set<? extends Assignment> assignments() {
        return assignments;
    }

    public final Set<? extends RandomVariable> randomVariables() {
        return randomVariables;
    }

    public final <R> R query(final Function<Stream<? extends Assignment>, R> query) {
        return query.apply(stream());
    }
}
