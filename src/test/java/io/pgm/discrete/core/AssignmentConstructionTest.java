package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
public class AssignmentConstructionTest {

    @Test
    public void testAssignmentConstruction() {
        AssignmentStream stream = ProbTable.builder()
            .variable(RandomVariable.label("a").events(1, 2).build())
            .variable(RandomVariable.label("b").events(1, 2).build())
            .values(0.5d, 0.5d)
            .values(0.6d, 0.4d)
            .build()
            .stream();

        List<Assignment> list = stream.collect(Collectors.toList());

        Assert.assertEquals(4, list.size());
    }

}



