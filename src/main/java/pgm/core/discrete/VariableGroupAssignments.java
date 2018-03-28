package pgm.core.discrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class VariableGroupAssignments {

    private VariableGroupAssignments() {
    }

    public static Collection<VariableGroupAssignment> of(final RandomVariable var0,
                                                         final RandomVariable ... others) {
        List<RandomVariable> vars = new ArrayList<>();
        vars.add(var0);
        if (others.length > 0) {
            vars.addAll(Arrays.asList(others));
        }

        return construct(vars);
    }

    public static Collection<VariableGroupAssignment> of(final Collection<? extends RandomVariable> vars) {
        return construct(vars);
    }

    private static Collection<VariableGroupAssignment> construct(final Collection<? extends RandomVariable> vars) {
        if (vars.isEmpty()) {
            throw new IllegalArgumentException("Empty random variables");
        }

        List<VariableGroupAssignment> assignmentGroups = new ArrayList<>();
        List<RandomVariable> varList = new ArrayList<>(vars);

        int sumCardinality = varList.stream().map(RandomVariable::cardinality).reduce(1, (a, b) -> a * b);

        for (int i = 0; i < sumCardinality; i++) {
            int cumulativeProduct = 1;

            Set<VariableAssignment> varAssignments = new LinkedHashSet<>();
            varAssignments.add(varList.get(0).setIndex(i % varList.get(0).cardinality()));

            for (int j = 1; j < varList.size(); j++) {
                RandomVariable variable = varList.get(j);
                cumulativeProduct *= varList.get(j - 1).cardinality();
                varAssignments.add(variable.setIndex((i / cumulativeProduct) % variable.cardinality()));
            }

            assignmentGroups.add(VariableGroupAssignment.of(varAssignments));
        }

        return assignmentGroups;
    }
}
