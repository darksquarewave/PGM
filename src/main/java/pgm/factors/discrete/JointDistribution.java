package pgm.factors.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.core.discrete.Assignment;
import pgm.core.discrete.AbstractAssignmentSet;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public final class JointDistribution extends AbstractAssignmentSet {

    public static final class Builder extends AbstractAssignmentSet.AbstractBuilder<JointDistribution> {

        @Override
        public JointDistribution build() {
            return new JointDistribution(assignments());
        }
    }

    private JointDistribution(final Collection<? extends Assignment> assignmentsColl) {
        super(assignmentsColl);
        validate(assignments());
    }

    private static void validate(final Set<? extends Assignment> assignments) {
        double sum = assignments.stream().mapToDouble(Assignment::value).sum();
        if (Double.compare(sum, 1d) != 0) {
            throw new IllegalStateException("Joint distribution must sum to 1");
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

        JointDistribution jointDistribution = (JointDistribution) obj;

        return Objects.equals(randomVariables(), jointDistribution.randomVariables())
                && Objects.equals(assignments(), jointDistribution.assignments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariables(), assignments());
    }
}
