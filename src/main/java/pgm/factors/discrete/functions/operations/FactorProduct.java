package pgm.factors.discrete.functions.operations;

import pgm.core.discrete.Assignment;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableGroupAssignments;
import pgm.factors.discrete.Factor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

public final class FactorProduct implements Function<Collection<? extends Factor>, Stream<? extends Assignment>> {

    @Override
    public Stream<? extends Assignment> apply(final Collection<? extends Factor> factors) {
        List<Factor> unionFactors = new ArrayList<>(factors);

        Set<RandomVariable> unionVars = new TreeSet<>();
        for (Factor operand : factors) {
            unionVars.addAll(operand.randomVariables());
        }

        return VariableGroupAssignments.of(unionVars).stream()
            .map(varGroupAssignment -> {
                double value = unionFactors.stream()
                    .map(Factor::assignments)
                    .flatMap(Collection::stream)
                    .filter(assignment -> varGroupAssignment.contains(assignment.varAssignments()))
                    .mapToDouble(Assignment::value)
                    .reduce(1d, (a, b) -> a * b);

                return varGroupAssignment.toAssignment(value);
            });
    }
}
