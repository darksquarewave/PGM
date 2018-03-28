package pgm.factors.discrete.functions.operations;

import pgm.core.discrete.Assignment;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableGroupAssignments;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Product implements Function<Collection<? extends Assignment>, Stream<? extends Assignment>> {

    @Override
    public Stream<? extends Assignment> apply(final Collection<? extends Assignment> assignments) {
        Set<RandomVariable> unionVars = new TreeSet<>();
        for (Assignment assignment : assignments) {
            unionVars.addAll(assignment.varAssignments().randomVariables());
        }

        return VariableGroupAssignments.of(unionVars).stream()
            .map(varGroupAssignment -> {
                double value = assignments.stream()
                    .filter(assignment -> varGroupAssignment.contains(assignment.varAssignments()))
                    .mapToDouble(Assignment::value)
                    .reduce(1d, (a, b) -> a * b);

                return varGroupAssignment.toAssignment(value);
            });
    }
}
