package pgm.factors.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.core.discrete.ImmutableAssignmentSet;
import pgm.core.discrete.Assignment;
import pgm.core.discrete.RandomVariable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class CPD extends ImmutableAssignmentSet {

    private final RandomVariable randomVariable;
    private final List<? extends RandomVariable> conditioningVariables;

    public static CPD build(final Collection<Assignment> assignments) {
        return new CPD(assignments);
    }

    private CPD(final Collection<? extends Assignment> assignments) {
        super(assignments);
        List<RandomVariable> vars = varList(assignments);

        randomVariable = vars.get(0);
        validate(randomVariable, assignments);

        if (vars.size() > 1) {
            conditioningVariables = Collections.unmodifiableList(vars.subList(1, vars.size()));
        } else {
            conditioningVariables = Collections.emptyList();
        }
    }

    private static void validate(final RandomVariable randomVariable,
                                 final Collection<? extends Assignment> assignments) {

        int cardinality = randomVariable.cardinality();

        int i = 0;
        double sum = 0;

        for (Assignment assignment : assignments) {
            if (i > 0 && i % cardinality == 0) {
                if (Double.compare(sum, 1d) != 0) {
                    throw new IllegalStateException("Probability distribution entries must sum to 1");
                } else {
                    sum = 0;
                }
            }

            sum += assignment.value();
            i++;
        }

        if (Double.compare(sum, 1.0d) != 0) {
            throw new IllegalArgumentException("CPD must sum to 1");
        }
    }

    public RandomVariable randomVariable() {
        return randomVariable;
    }

    public Collection<? extends RandomVariable> conditioningVariables() {
        return conditioningVariables;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        CPD cpd = (CPD) obj;

        return Objects.equals(randomVariables(), cpd.randomVariables())
                && Objects.equals(assignments(), cpd.assignments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariables(), assignments());
    }
}
