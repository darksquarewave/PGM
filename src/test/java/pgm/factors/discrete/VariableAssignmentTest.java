package pgm.factors.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableAssignment;

public class VariableAssignmentTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConstruction() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
                .id("A")
                .event(1)
                .event(2)
                .event(3)
                .build();

        VariableAssignment aAssignment = a.set(2);
        Assert.assertEquals(2, aAssignment.event());
        Assert.assertEquals(a, aAssignment.randomVariable());
    }

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testGroupAssignmentConstruction() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
                .id("A")
                .event(1)
                .event(2)
                .event(3)
                .build();

        RandomVariable<String, Integer> b = RandomVariable.builder()
                .id("B")
                .event(1)
                .event(2)
                .event(3)
                .build();

        Assert.assertEquals(2, a.set(2).and(b.set(3)).toGroupAssignment()
                .randomVariables()
                .size());
    }
}
