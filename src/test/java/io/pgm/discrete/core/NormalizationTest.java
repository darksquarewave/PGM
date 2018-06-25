package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension", "checkstyle:methodname"})
public final class NormalizationTest {

    @Test
    public void testNormalization_case1() {
        RandomVariable<Integer, Integer> a = RandomVariable.id(1)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variables(a)
            .values(4d, 1d)
            .build();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(a)
            .values(0.8d, 0.2d)
            .build();

        Assert.assertEquals(s2.collect(Collectors.toList()), s1.norm().collect(Collectors.toList()));
    }

    @Test
    public void testNormalization_case2() {
        RandomVariable<Integer, Integer> a = RandomVariable.id(1)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> b = RandomVariable.id(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> c = RandomVariable.id(3)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variables(a, b, c)
            .values(1d, 1d, 1d, 1d, 1d, 1d, 2d, 2d)
            .build();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(a, b, c)
            .values(0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.1d, 0.2d, 0.2d)
            .build();

        Assert.assertEquals(s2.collect(Collectors.toList()), s1.norm().collect(Collectors.toList()));
    }

    @Test
    public void testLogNormalization_case1() {
        RandomVariable<Integer, Integer> a = RandomVariable.id(1)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variable(a)
            .values(-0.22314d, -0.91629d)
            .build();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(a)
            .values(-0.40546416829569604, -1.098614168295696)
            .build();

        Assert.assertEquals(s2.collect(Collectors.toList()), s1.lognorm().collect(Collectors.toList()));
    }
}
