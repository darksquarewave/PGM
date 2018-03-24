package pgm.core.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import pgm.factors.discrete.CPD;
import pgm.factors.discrete.Factor;
import pgm.factors.discrete.JointDistribution;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public final class Assignment implements Comparable<Assignment>,
        JoiningOperation<Assignment, Assignment.JoiningBuilder>, Serializable {

    private static final long serialVersionUID = 7753872171086728807L;

    private final VariableGroupAssignment varAssignment;
    private final double value;

    Assignment(final VariableGroupAssignment groupAssignment, final double val) {
        varAssignment = groupAssignment;
        value = val;
    }

    @Override
    public JoiningBuilder and(final Assignment assignment) {
        return new JoiningBuilder(this, assignment);
    }

    public static final class JoiningBuilder extends AbstractJoiningBuilder<Assignment, JoiningBuilder> {

        JoiningBuilder(final Assignment ... assignments) {
            super(Arrays.asList(assignments));
        }

        @Override
        public JoiningBuilder and(final Assignment assignment) {
            add(assignment);
            return this;
        }

        public Factor toFactor() {
            return this.items().stream().collect(FactorCollectors.toFactor());
        }

        public JointDistribution toJointDistribution() {
            return this.items().stream().collect(FactorCollectors.toJoint());
        }

        public CPD toCPD() {
            return this.items().stream().collect(FactorCollectors.toCPD());
        }
    }

    public VariableGroupAssignment varAssignments() {
        return varAssignment;
    }

    public Assignment varAssignments(final VariableGroupAssignment groupAssignment) {
        return new Assignment(groupAssignment, value);
    }

    public double value() {
        return value;
    }

    public Assignment value(final double val) {
        return new Assignment(varAssignment, val);
    }

    @Override
    public int compareTo(final @NotNull Assignment assignment) {
        return this.varAssignment.compareTo(assignment.varAssignment);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Assignment that = (Assignment) obj;

        return Double.compare(that.value, value) == 0
                && Objects.equals(varAssignment, that.varAssignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varAssignment);
    }
}
