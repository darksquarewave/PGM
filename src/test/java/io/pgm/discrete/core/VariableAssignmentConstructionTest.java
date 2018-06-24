package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

public class VariableAssignmentConstructionTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
            .id("A")
            .event(1)
            .event(2)
            .event(3)
            .build();

        VarAssignment aAssignment = a.set(2);
        Assert.assertEquals(2, aAssignment.event());
        Assert.assertEquals(a, aAssignment.randomVariable());
    }
}
