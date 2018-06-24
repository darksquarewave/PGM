package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
public class AssignmentUtilsTest {

    @Test
    public void testSubAssignmentIndexes() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder().id(1).events(1, 2, 3).build();
        RandomVariable<Integer, Integer> b = RandomVariable.builder().id(2).events(1, 2, 3).build();
        RandomVariable<Integer, Integer> c = RandomVariable.builder().id(3).events(1, 2, 3).build();

        List<Integer> indexes = AssignmentUtils.subAssignmentIndexes(
            a.set(1).and(b.set(1)),
            Arrays.asList(a, b, c));

        Assert.assertEquals(Arrays.asList(0, 9, 18), indexes);
    }
}
