package pgm.factors.discrete.functions.transformers;

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

public final class MarginalizationTransformer implements
        Function<Stream<? extends Assignment>, Stream<? extends Assignment>> {

    private final Collection<? extends RandomVariable> margVars;

    public static MarginalizationTransformer of(final Collection<? extends RandomVariable> marginalizationVars) {
        return new MarginalizationTransformer(marginalizationVars);
    }

    private MarginalizationTransformer(final Collection<? extends RandomVariable> marginalizationVars) {
        margVars = marginalizationVars;
    }

    @Override
    public Stream<? extends Assignment> apply(final Stream<? extends Assignment> assignments) {
        Map<VariableGroupAssignment, Double> map = assignments.map(assignment -> {
            VariableGroupAssignment varAssignments = assignment.varAssignments();

            Set<RandomVariable> variables = new LinkedHashSet<>(varAssignments.randomVariables());
            variables.removeAll(margVars);
            if (variables.isEmpty()) {
                throw new IllegalArgumentException("Cannot produce empty factor");
            }

            VariableGroupAssignment margAssignments = varAssignments.randomVariables(variables);

            return assignment.varAssignments(margAssignments);

        }).collect(Collectors.groupingBy(Assignment::varAssignments, Collectors.summingDouble(Assignment::value)));

        return map.entrySet().stream()
            .map(entry -> {
                VariableGroupAssignment groupAssignment = entry.getKey();
                double value = entry.getValue();

                return groupAssignment.toAssignment(value);
            });
    }
}
