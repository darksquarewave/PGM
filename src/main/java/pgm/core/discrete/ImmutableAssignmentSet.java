package pgm.core.discrete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterators;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ImmutableAssignmentSet {

    private final Set<? extends Assignment> assignments;
    private final List<? extends RandomVariable> randomVariables;

    protected ImmutableAssignmentSet(final Collection<? extends Assignment> input) {
        assignments = Collections.unmodifiableSet(new TreeSet<>(input));
        randomVariables = Collections.unmodifiableList(varList(assignments));
        validate(assignments, randomVariables);
    }

    private static void validate(final Set<? extends Assignment> assignments,
                                 final List<? extends RandomVariable> randomVariables) {

        if (assignments.isEmpty()) {
            throw new IllegalArgumentException("Assignments cannot be empty");
        }

        Set<RandomVariable> varSet = new HashSet<>(randomVariables);
        if (varSet.size() != randomVariables.size()) {
            throw new IllegalArgumentException("Duplicates in random variables detected");
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

    protected static List<RandomVariable> varList(final Collection<? extends Assignment> assignments) {
        Assignment assignment = assignments.iterator().next();
        VariableGroupAssignment groupAssignments = assignment.varAssignments();

        return new ArrayList<>(groupAssignments.randomVariables());
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

    public final List<? extends RandomVariable> randomVariables() {
        return randomVariables;
    }

    public final <R> R query(final Function<Stream<? extends Assignment>, R> query) {
        return query.apply(stream());
    }
}
