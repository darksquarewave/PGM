package pgm.factors.discrete.functions.transformers;

import pgm.core.discrete.Assignment;
import pgm.core.discrete.VariableGroupAssignment;

import java.util.Collections;
import java.util.function.Function;

public final class EvidenceTransformer implements Function<Assignment, Assignment> {

    private final VariableGroupAssignment evidences;

    public static EvidenceTransformer of(final VariableGroupAssignment evidences) {
        return new EvidenceTransformer(evidences);
    }

    public EvidenceTransformer(final VariableGroupAssignment evidencesColl) {
        evidences = evidencesColl;
    }

    @Override
    public Assignment apply(final Assignment assignment) {
        VariableGroupAssignment varAssignments = assignment.varAssignments();
        if (Collections.disjoint(varAssignments.randomVariables(), evidences.randomVariables())) {
            return assignment;
        }

        if (varAssignments.intersects(evidences)) {
            return assignment;
        } else {
            return assignment.value(0d);
        }
    }
}
