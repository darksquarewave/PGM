package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
public class ConcatTest {

    @Test
    public void testConcat() {
        RandomVariable<String, Integer> var1 = RandomVariable.label("var1")
            .events(1, 2)
            .build();

        RandomVariable<String, Integer> var2 = RandomVariable.label("var2")
            .events(1, 2)
            .build();

        RandomVariable<String, Integer> var3 = RandomVariable.label("var3")
            .events(1, 2)
            .build();

        AssignmentStream s1 = ProbTable.builder()
            .variables(var1, var2)
            .values(0.5d, 0.5d)
            .values(0.6d, 0.4d)
            .build()
            .stream()
            .parallel();

        AssignmentStream s2 = ProbTable.builder()
            .variables(var2, var3)
            .values(0.5d, 0.5d)
            .values(0.6d, 0.4d)
            .build()
            .stream()
            .parallel();

        AssignmentStream s3 = ProbTable.builder()
            .variables(var1, var3)
            .values(0.5d, 0.5d)
            .values(0.6d, 0.4d)
            .build()
            .stream()
            .parallel();

        List<Assignment> list = s1.concat(s2, s3).collect(Collectors.toList());

        Assert.assertEquals(12, list.size());
    }
}
