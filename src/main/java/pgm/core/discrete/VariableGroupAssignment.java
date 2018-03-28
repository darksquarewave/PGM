package pgm.core.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class VariableGroupAssignment implements Comparable<VariableGroupAssignment>, Serializable {

    private static final long serialVersionUID = -7058579283378002854L;

    private final Map<? extends RandomVariable, ? extends VariableAssignment> varToAssignmentMap;
    private final int index;

    private static final VariableGroupAssignment EMPTY_GROUP_ASSIGNMENT = new VariableGroupAssignment();

    public static VariableGroupAssignment emptyAssignment() {
        return EMPTY_GROUP_ASSIGNMENT;
    }

    private VariableGroupAssignment() {
        varToAssignmentMap = Collections.emptyMap();
        index = 0;
    }

    private VariableGroupAssignment(final Map<? extends RandomVariable,
                                              ? extends VariableAssignment> varToAssignments) {
        varToAssignmentMap = Collections.unmodifiableMap(varToAssignments);
        index = calculateIndex(varToAssignments.values());
    }

    private VariableGroupAssignment(final Collection<? extends VariableAssignment> assignments) {
        if (assignments.isEmpty()) {
            throw new IllegalArgumentException("Var assignments cannot be empty");
        }

        Map<RandomVariable, VariableAssignment> map = new LinkedHashMap<>();
        for (VariableAssignment assignment : assignments) {
            map.put(assignment.randomVariable(), assignment);
        }

        varToAssignmentMap = Collections.unmodifiableMap(map);
        index = calculateIndex(assignments);
    }

    public static VariableGroupAssignment of(final Collection<? extends VariableAssignment> evidenceAssignments) {
        if (evidenceAssignments.isEmpty()) {
            return VariableGroupAssignment.emptyAssignment();
        } else {
            return new VariableGroupAssignment(evidenceAssignments);
        }
    }

    private static int calculateIndex(final Collection<? extends VariableAssignment> assignments) {
        List<VariableAssignment> assignmentList = new ArrayList<>(assignments);

        int cumulativeProduct = 1;
        int index = assignmentList.get(0).index();

        for (int i = 1; i < assignmentList.size(); i++) {
            VariableAssignment assignment = assignmentList.get(i);
            VariableAssignment prev = assignmentList.get(i - 1);
            cumulativeProduct *= prev.randomVariable().cardinality();
            index += cumulativeProduct * assignment.index();
        }

        return index;
    }

    public Collection<? extends VariableAssignment> assignments() {
        return varToAssignmentMap.values();
    }

    public Set<? extends RandomVariable> randomVariables() {
        return varToAssignmentMap.keySet();
    }

    public VariableGroupAssignment randomVariables(final Set<? extends RandomVariable> randomVariables) {
        Map<RandomVariable, VariableAssignment> map = new TreeMap<>(varToAssignmentMap);
        map.keySet().retainAll(randomVariables);

        return new VariableGroupAssignment(map);
    }

    public boolean contains(final VariableGroupAssignment varGroupAssignment) {
        Map<RandomVariable, VariableAssignment> unionMap = new HashMap<>(varToAssignmentMap);
        unionMap.putAll(varGroupAssignment.varToAssignmentMap);

        return unionMap.equals(varToAssignmentMap);
    }

    public boolean contains(final VariableAssignment variableAssignment) {
        RandomVariable randomVar = variableAssignment.randomVariable();
        return varToAssignmentMap.containsKey(randomVar)
            && varToAssignmentMap.get(randomVar).equals(variableAssignment);
    }

    public boolean intersects(final VariableGroupAssignment group) {
        Map<RandomVariable, VariableAssignment> mapA = new LinkedHashMap<>(group.varToAssignmentMap);
        mapA.keySet().retainAll(varToAssignmentMap.keySet());

        Map<RandomVariable, VariableAssignment> mapB = new LinkedHashMap<>(varToAssignmentMap);
        mapB.keySet().retainAll(group.varToAssignmentMap.keySet());

        return mapA.equals(mapB);
    }

    public Assignment toAssignment(final double value) {
        return new Assignment(this, value);
    }

    @Override
    public int compareTo(final @NotNull VariableGroupAssignment assignment) {
        return Integer.compare(index, assignment.index);
    }

    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        VariableGroupAssignment that = (VariableGroupAssignment) obj;

        return Objects.equals(varToAssignmentMap, that.varToAssignmentMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varToAssignmentMap);
    }

    @Override
    public String toString() {
        String result = varToAssignmentMap.values().stream()
                .map(assignment -> assignment.randomVariable().id() + "=" + assignment.event())
                .collect(Collectors.joining(", "));

        return "VariableGroupAssignment(" + result + ")";
    }

}
