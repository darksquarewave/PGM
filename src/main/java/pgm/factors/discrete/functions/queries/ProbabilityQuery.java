package pgm.factors.discrete.functions.queries;

import pgm.core.discrete.Assignment;
import pgm.core.discrete.AssignmentOperations;
import pgm.core.discrete.VariableAssignment;
import pgm.core.discrete.VariableGroupAssignment;
import pgm.factors.discrete.functions.transformers.EvidenceTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ProbabilityQuery implements Function<Stream<? extends Assignment>, Double> {

    private final VariableAssignment target;
    private final VariableGroupAssignment evidences;

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Builder() {
        }

        public EvidenceBuilder assignment(final VariableAssignment target) {
            return new EvidenceBuilder(target);
        }
    }

    public static final class EvidenceBuilder {

        private final VariableAssignment targetAssignment;
        private final List<VariableAssignment> evidenceAssignments;

        EvidenceBuilder(final VariableAssignment assignment) {
            targetAssignment = assignment;
            evidenceAssignments = new ArrayList<>();
        }

        public EvidenceBuilder evidences(final VariableGroupAssignment evidences) {
            this.evidenceAssignments.addAll(evidences.assignments());
            return this;
        }

        public EvidenceBuilder evidence(final VariableAssignment evidence) {
            this.evidenceAssignments.add(evidence);
            return this;
        }

        public ProbabilityQuery build() {
            return new ProbabilityQuery(this);
        }
    }

    private ProbabilityQuery(final EvidenceBuilder builder) {
        target = builder.targetAssignment;
        evidences = VariableGroupAssignment.of(builder.evidenceAssignments);
    }

    @Override
    public Double apply(final Stream<? extends Assignment> assignments) {
        return assignments.map(new EvidenceTransformer(evidences))
                .collect(AssignmentOperations.renormalize())
                .filter(a -> a.varAssignments().contains(target))
                .mapToDouble(Assignment::value)
                .sum();
    }
}
