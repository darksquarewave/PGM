package pgm.factor.discrete;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class AssignmentIndex implements Comparable<AssignmentIndex> {

    private final Map<? extends RandomVariable, Integer> varToAssignmentMap;

    public AssignmentIndex(final Set<? extends RandomVariable> vars,
                           final Collection<? extends Integer> indexes) {

        if (vars.size() == 0) {
            throw new IllegalArgumentException("Random variables cannot be empty");
        }

        if (indexes.size() == 0) {
            throw new IllegalArgumentException("Assignment indexes cannot be empty");
        }

        if (vars.size() != indexes.size()) {
            throw new IllegalArgumentException("Dimensionality mismatch between variables and indexes");
        }

        Map<RandomVariable, Integer> map = new LinkedHashMap<>();
        Iterator<? extends RandomVariable> varIterator = vars.iterator();
        Iterator<? extends Integer> indexIterator = indexes.iterator();

        while (varIterator.hasNext()) {
            RandomVariable randomVariable = varIterator.next();
            int index = indexIterator.next();
            map.put(randomVariable, index);
        }

        varToAssignmentMap = Collections.unmodifiableMap(map);
    }

    public boolean contains(final AssignmentIndex index) {
        Map<RandomVariable, Integer> unionMap = new HashMap<>(varToAssignmentMap);
        unionMap.putAll(index.varToAssignmentMap);

        return unionMap.equals(varToAssignmentMap);
    }

    public boolean contains(final Set<? extends RandomVariable> variables) {
        return !Collections.disjoint(varToAssignmentMap.keySet(), variables);
    }

    public boolean intersects(final AssignmentIndex index) {
        Map<RandomVariable, Integer> mapA = new LinkedHashMap<>(index.varToAssignmentMap);
        mapA.keySet().retainAll(varToAssignmentMap.keySet());

        Map<RandomVariable, Integer> mapB = new LinkedHashMap<>(varToAssignmentMap);
        mapB.keySet().retainAll(index.varToAssignmentMap.keySet());

        return mapA.equals(mapB);
    }

    public boolean isEmpty() {
        return varToAssignmentMap.isEmpty();
    }

    public AssignmentIndex remove(final Set<? extends RandomVariable> vars) {
        Map<RandomVariable, Integer> map = new LinkedHashMap<>(varToAssignmentMap);
        map.keySet().removeAll(vars);

        return new AssignmentIndex(map.keySet(), map.values());
    }

    public AssignmentIndex sort() {
        Map<? extends RandomVariable, Integer> map = new TreeMap<>(varToAssignmentMap);
        return new AssignmentIndex(map.keySet(), map.values());
    }

    public Set<? extends RandomVariable> randomVariables() {
        return new LinkedHashSet<>(varToAssignmentMap.keySet());
    }

    public List<Integer> indexes() {
        return new ArrayList<>(varToAssignmentMap.values());
    }

    public List<Integer> cardinalities() {
        return varToAssignmentMap.keySet().stream()
                .map(RandomVariable::cardinality)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AssignmentIndex that = (AssignmentIndex) obj;

        return Objects.equals(varToAssignmentMap, that.varToAssignmentMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varToAssignmentMap);
    }

    @Override
    public int compareTo(final AssignmentIndex o) {
        return Integer.compare(AssignmentIndexes.toIndex(this), AssignmentIndexes.toIndex(o));
    }
}
