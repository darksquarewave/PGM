package pgm.factors.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.core.discrete.AbstractAssignmentSet;
import pgm.core.discrete.Assignment;
import pgm.core.discrete.RandomVariable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public final class CPD extends AbstractAssignmentSet {

    public static final class Builder extends AbstractAssignmentSet.AbstractBuilder<CPD> {

        @Override
        public CPD build() {
            return new CPD(assignments());
        }
    }

    private CPD(final Collection<? extends Assignment> assignmentsColl) {
        super(assignmentsColl);
        validate(randomVariables().iterator().next(), assignments());
    }

    private static void validate(final RandomVariable randomVariable,
                                 final Set<? extends Assignment> assignments) {

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
