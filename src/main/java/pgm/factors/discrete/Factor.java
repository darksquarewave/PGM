package pgm.factors.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import pgm.core.discrete.Assignment;
import pgm.core.discrete.AssignmentOperations;
import pgm.core.discrete.FactorCollectors;
import pgm.core.discrete.ImmutableAssignmentSet;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableAssignment;
import pgm.core.discrete.VariableGroupAssignment;
import pgm.factors.discrete.functions.transformers.EvidenceTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Factor extends ImmutableAssignmentSet {

    public static Factor build(final Collection<Assignment> assignments) {
        return new Factor(assignments);
    }

    private Factor(final Collection<? extends Assignment> assignments) {
        super(assignments);
    }

    public CPD toCPD() {
        return stream().collect(FactorCollectors.toCPD());
    }

    public JointDistribution toJointDistribution() {
        return stream().collect(FactorCollectors.toJoint());
    }

    public Factor product(final Factor f) {
        return product(Arrays.asList(this, f));
    }

    public static Factor product(final Factor f0, final Factor ... rest) {
        ArrayList<Factor> factors = new ArrayList<>();
        factors.add(f0);
        if (rest.length > 0) {
            factors.addAll(Arrays.asList(rest));
        }
        return product(factors);
    }

    public static Factor product(final Collection<? extends Factor> factors) {
        if (factors.size() < 2) {
            throw new IllegalArgumentException("Invalid count of factors");
        }

        return factors.stream()
                .flatMap(factor -> factor.assignments().stream())
                .collect(AssignmentOperations.product())
                .collect(FactorCollectors.toFactor());
    }

    public Factor marginalize(final RandomVariable var0, final RandomVariable ... rest) {
        ArrayList<RandomVariable> vars = new ArrayList<>();
        vars.add(var0);

        if (rest.length > 0) {
            vars.addAll(Arrays.asList(rest));
        }

        return marginalize(vars);
    }

    public Factor marginalize(final Collection<? extends RandomVariable> margVars) {
        return this.assignments().stream()
                .collect(AssignmentOperations.marginalize(margVars))
                .collect(FactorCollectors.toFactor());
    }

    public Factor observeEvidence(final VariableAssignment singleEvidence) {
        return stream().map(new EvidenceTransformer(singleEvidence.toGroupAssignment()))
                .collect(FactorCollectors.toFactor());
    }

    public Factor observeEvidence(final VariableGroupAssignment groupEvidence) {
        return stream().map(new EvidenceTransformer(groupEvidence))
                .collect(FactorCollectors.toFactor());
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Factor factor = (Factor) obj;

        return Objects.equals(randomVariables(), factor.randomVariables())
                && Objects.equals(assignments(), factor.assignments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomVariables(), assignments());
    }

    @Override
    public String toString() {
        String vars = randomVariables().stream()
                .map(variable -> variable.id().toString())
                .collect(Collectors.joining(", "));
        return "Factor(variables=" + vars + ")";
    }
}
