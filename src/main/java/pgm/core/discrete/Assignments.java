package pgm.core.discrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class Assignments {

    private Assignments() {
    }

    public static Collection<? extends Assignment> of(final Collection<? extends Double> values,
                                                      final RandomVariable var0,
                                                      final RandomVariable ... rest) {
        List<RandomVariable> vars = new ArrayList<>();
        vars.add(var0);
        if (rest.length > 0) {
            vars.addAll(Arrays.asList(rest));
        }

        return construct(values, vars);
    }

    private static Collection<? extends Assignment> construct(final Collection<? extends Double> values,
                                                              final Collection<? extends RandomVariable> vars) {

        if (vars.isEmpty()) {
            throw new IllegalArgumentException("Random variables cannot be empty");
        }

        int sumCardinality = vars.stream()
            .map(RandomVariable::cardinality)
            .reduce(1, (a, b) -> a * b);

        if (sumCardinality != values.size()) {
            throw new IllegalArgumentException("Dimensionality mismatch between values Operations variables");
        }

        Iterator<? extends Double> valIterator = values.iterator();

        return VariableGroupAssignments.of(vars).stream()
            .map(groupAssignment -> new Assignment(groupAssignment, valIterator.next()))
            .collect(Collectors.toList());
    }
}
