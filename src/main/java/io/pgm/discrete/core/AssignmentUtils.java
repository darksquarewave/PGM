package io.pgm.discrete.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class AssignmentUtils {

    private AssignmentUtils() {
    }

    static int cardinality(final Collection<? extends RandomVariable> randomVars) {
        if (randomVars.isEmpty()) {
            return 0;
        } else {
            int result = 1;
            for (RandomVariable v : randomVars) {
                result *= v.cardinality();
            }
            return result;
        }
    }

    static int assignmentIndex(final MultiVarAssignment multiVarAssignment) {
        return assignmentIndex(multiVarAssignment.assignments());
    }

    static int assignmentIndex(final Collection<? extends VarAssignment> assignments) {
        int i = 0;
        int cp = 1;
        for (VarAssignment assignment : assignments) {
            i += assignment.index() * cp;
            cp *= assignment.randomVariable().cardinality();
        }
        return i;
    }

    static List<Integer> subAssignmentIndexes(final MultiVarAssignment subAssignment,
                                              final Collection<? extends RandomVariable> randomVars) {

        List<List<Integer>> input = new ArrayList<>();

        for (RandomVariable randomVar : randomVars) {
            Optional<VarAssignment> varAssignment = subAssignment.get(randomVar);

            if (varAssignment.isPresent()) {
                input.add(Collections.singletonList(varAssignment.get().index()));
            } else {
                List<Integer> eventIndexes = new ArrayList<>();
                for (int i = 0; i < randomVar.eventSpace().size(); i++) {
                    eventIndexes.add(i);
                }
                input.add(eventIndexes);
            }
        }

        int n = input.size();
        List<Integer> indices = new ArrayList<>(Collections.nCopies(n, 0));
        List<Integer> result = new ArrayList<>();

        while (true) {
            int i = 0;
            List<VarAssignment> varAssignments = new ArrayList<>();
            for (RandomVariable randomVar : randomVars) {
                varAssignments.add(randomVar.setIndex(input.get(i).get(indices.get(i))));
                i++;
            }

            result.add(assignmentIndex(varAssignments));

            int next = indices.size() - 1;
            while (next >= 0 && (indices.get(next) + 1 >= input.get(next).size())) {
                next--;
            }

            if (next < 0) {
                break;
            }

            indices.set(next, indices.get(next) + 1);

            for (i = next + 1; i < n; i++) {
                indices.set(i, 0);
            }
        }

        return result;
    }
}
