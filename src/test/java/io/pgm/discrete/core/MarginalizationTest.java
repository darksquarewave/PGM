package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension", "checkstyle:methodname"})
public final class MarginalizationTest {

    @Test
    public void testMarginalization_case1() {
        RandomVariable<Integer, Integer> var1 = RandomVariable.label(1)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var2 = RandomVariable.label(2)
            .events(1, 2)
            .build();

        AssignmentStream s1 = ProbTable.builder()
            .variables(var2, var1)
            .values(0.59d, 0.41d)
            .values(0.22d, 0.78d)
            .build()
            .stream();

        ProbTable result = ProbTable.builder()
            .variables(var1)
            .values(1d, 1d)
            .build();

        Assert.assertEquals(result, s1.marginalize(var2).sum().collect(AssignmentCollectors.toProbTable()));
    }

    @Test
    public void testMarginalization_case2() {
        RandomVariable<Integer, Integer> var1 = RandomVariable.label(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> var2 = RandomVariable.label(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> var3 = RandomVariable.label(3)
            .events(1, 2)
            .build();

        ProbTable result = ProbTable.builder()
            .variables(var1, var3)
            .values(0.33d, 0.05d, 0.24d, 0.51d, 0.07d, 0.39d)
            .build();

        AssignmentStream s = ProbTable.builder()
            .variables(var3, var2, var1)
            .values(0.25d, 0.35d, 0.08d)
            .values(0.16d, 0.05d, 0.07d)
            .values(0.00d, 0.00d, 0.15d)
            .values(0.21d, 0.09d, 0.18d)
            .build()
            .stream();

        Assert.assertEquals(result, s.marginalize(var2).sum().collect(AssignmentCollectors.toProbTable()));
    }
}
