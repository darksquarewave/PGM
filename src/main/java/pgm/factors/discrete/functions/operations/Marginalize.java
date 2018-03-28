package pgm.factors.discrete.functions.operations;

import pgm.core.discrete.Assignment;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableGroupAssignment;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Marginalize implements
        Function<Collection<? extends Assignment>, Stream<? extends Assignment>> {

    private final Collection<? extends RandomVariable> marginalizationVars;

    public Marginalize(final Collection<? extends RandomVariable> margVars) {
        marginalizationVars = margVars;
    }

    @Override
    public Stream<? extends Assignment> apply(final Collection<? extends Assignment> assignments) {
        Map<VariableGroupAssignment, Double> map = assignments.stream().map(assignment -> {
            VariableGroupAssignment varAssignments = assignment.varAssignments();

            Set<RandomVariable> variables = new LinkedHashSet<>(varAssignments.randomVariables());
            variables.removeAll(marginalizationVars);

            if (variables.isEmpty()) {
                throw new IllegalArgumentException("Cannot produce empty factor");
            }

            VariableGroupAssignment margAssignments = varAssignments.randomVariables(variables);

            return assignment.varAssignments(margAssignments);

        }).collect(Collectors.groupingBy(Assignment::varAssignments, Collectors.summingDouble(Assignment::value)));

        return map.entrySet()
                .stream()
                .map(entry -> {
                    VariableGroupAssignment groupAssignment = entry.getKey();
                    double value = entry.getValue();

                    return groupAssignment.toAssignment(value);
                });
    }
}
