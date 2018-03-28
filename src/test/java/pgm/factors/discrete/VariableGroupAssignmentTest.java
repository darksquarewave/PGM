package pgm.factors.discrete;

import org.junit.Assert;
import org.junit.Test;
import pgm.core.discrete.RandomVariable;
import pgm.core.discrete.VariableGroupAssignment;

public class VariableGroupAssignmentTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testCreation() {
        RandomVariable<String, Integer> a = RandomVariable.builder()
                .id("a")
                .event(1)
                .event(2)
                .build();

        RandomVariable<String, Integer> b = RandomVariable.builder()
                .id("b")
                .event(1)
                .event(2)
                .build();

        VariableGroupAssignment groupAssignment = a.set(2).and(b.set(1)).toGroupAssignment();
        Assert.assertEquals(2, groupAssignment.assignments().size());
    }
}
