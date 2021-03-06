package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension", "checkstyle:methodname"})
public final class ObserveEvidenceTest {

    @Test
    public void testEvidence_case1() {
        RandomVariable<Integer, Integer> var1 = RandomVariable.label(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> var2 = RandomVariable.label(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var3 = RandomVariable.label(3)
            .events(1, 2)
            .build();

        AssignmentStream s = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d, 0d, 0d, 0.15d, 0.21d, 0.09d, 0.18d)
            .build()
            .stream();

        ProbTable result = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0d, 0.08d, 0d, 0.05d, 0d, 0d, 0d, 0.15d, 0d, 0.09d, 0d)
                .build();

        Assert.assertEquals(result, s.evidence(var3.set(1)).collect(AssignmentCollectors.toProbTable()));
    }

    @Test
    public void testEvidence_case2() {
        RandomVariable<Integer, Integer> var1 = RandomVariable.label(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> var2 = RandomVariable.label(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var3 = RandomVariable.label(3)
            .events(1, 2)
            .build();

        AssignmentStream s1 = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d, 0d, 0d, 0.15d, 0.21d, 0.09d, 0.18d)
            .build()
            .stream();

        ProbTable table = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d)
            .build();

        Assert.assertEquals(table, s1.evidence(var3.set(1).and(var1.set(1)).and(var2.set(1)))
            .collect(AssignmentCollectors.toProbTable()));
    }

    @Test
    public void testEvidence_case3() {
        RandomVariable<Integer, Integer> var1 = RandomVariable.label(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> var2 = RandomVariable.label(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var3 = RandomVariable.label(3)
            .events(1, 2)
            .build();

        AssignmentStream s = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d, 0d, 0d, 0.15d, 0.21d, 0.09d, 0.18d)
            .build()
            .stream();

        ProbTable table = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0d, 0.08d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d)
            .build();

        Assert.assertEquals(table, s.evidence(var3.set(1).and(var1.set(1)))
            .collect(AssignmentCollectors.toProbTable()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEvidence_invalidEvidence() {
        RandomVariable<Integer, Integer> var1 = RandomVariable.label(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> var2 = RandomVariable.label(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var3 = RandomVariable.label(3)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var4 = RandomVariable.label(4)
            .events(1, 2)
            .build();

        AssignmentStream s1 = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0.35d, 0.08d, 0.16d, 0.05d, 0.07d, 0d, 0d, 0.15d, 0.21d, 0.09d, 0.18d)
            .build()
            .stream();

        s1.evidence(var4.set(1)).collect(Collectors.toList());
    }
}
