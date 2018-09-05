package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension", "checkstyle:methodname"})
public final class NormalizationTest {

    @Test
    public void testNormalization_case1() {
        RandomVariable<Integer, Integer> a = RandomVariable.label(1)
            .events(1, 2)
            .build();

        AssignmentStream s = ProbTable.builder()
            .variables(a)
            .values(4d, 1d)
            .build()
            .stream();

        ProbTable result = ProbTable.builder()
            .variables(a)
            .values(0.8d, 0.2d)
            .build();

        Assert.assertEquals(result, s.norm().collect(AssignmentCollectors.toProbTable()));
    }

    @Test
    public void testNormalization_case2() {
        RandomVariable<Integer, Integer> a = RandomVariable.label(1)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> b = RandomVariable.label(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> c = RandomVariable.label(3)
            .events(1, 2)
            .build();

        AssignmentStream s = ProbTable.builder()
            .variables(a, b, c)
            .values(1d, 1d, 1d, 1d, 1d, 1d, 2d, 2d)
            .build()
            .stream();

        ProbTable result = ProbTable.builder()
            .variables(a, b, c)
            .values(0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.2d, 0.2d)
            .build();

        Assert.assertEquals(result, s.norm().collect(AssignmentCollectors.toProbTable()));
    }

    @Test
    public void testLogNormalization_case1() {
        RandomVariable<Integer, Integer> a = RandomVariable.label(1)
            .events(1, 2)
            .build();

        AssignmentStream s = ProbTable.builder()
            .variable(a)
            .values(-0.22314d, -0.91629d)
            .build()
            .stream();

        ProbTable result = ProbTable.builder()
            .variables(a)
            .values(-0.40546416829569604, -1.098614168295696)
            .build();

        Assert.assertEquals(result, s.lognorm().collect(AssignmentCollectors.toProbTable()));
    }
}
