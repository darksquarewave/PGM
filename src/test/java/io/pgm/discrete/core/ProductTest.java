package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
public class ProductTest {

    @Test
    public void testSequentialProductCase1() {
        RandomVariable<String, Integer> var1 = RandomVariable.builder()
            .id("a")
            .events(1, 2)
            .build();

        RandomVariable<String, Integer> var2 = RandomVariable.builder()
            .id("b")
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variable(var1)
            .values(0.11d, 0.89d)
            .build()
            .sequential();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(var2, var1)
            .values(0.59d, 0.41d)
            .values(0.22d, 0.78d)
            .build()
            .sequential();

        AssignmentStream s3 = AssignmentStream.builder()
            .variables(var1, var2)
            .values(0.0649d, 0.1958d)
            .values(0.0451d, 0.6942d)
            .build();

        Assert.assertEquals(s3.collect(Collectors.toSet()),
            s1.concat(s2).product().collect(Collectors.toSet()));
    }

    @Test
    public void testSequentialProductCase2() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder()
            .id(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> b = RandomVariable.builder()
            .id(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> c = RandomVariable.builder()
            .id(3)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variables(b, a)
            .values(0.5d, 0.8d)
            .values(0.1d, 0d)
            .values(0.3d, 0.9d)
            .build()
            .sequential();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(c, b)
            .values(0.5d, 0.7d)
            .values(0.1d, 0.2d)
            .build()
            .sequential();

        AssignmentStream s3 = AssignmentStream.builder()
            .variables(a, b, c)
            .values(0.25d, 0.05d, 0.15d)
            .values(0.08d, 0d, 0.09d)
            .values(0.35d, 0.07d, 0.21d)
            .values(0.16d, 0d, 0.18d)
            .build();

        Assert.assertEquals(s3.collect(Collectors.toList()),
            s1.concat(s2).product().collect(Collectors.toList()));
    }

    @Test
    public void testSequentialProductCase3() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder()
            .id(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> b = RandomVariable.builder()
            .id(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> c = RandomVariable.builder()
            .id(3)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> d = RandomVariable.builder()
            .id(4)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> e = RandomVariable.builder()
            .id(5)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> f = RandomVariable.builder()
            .id(6)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> g = RandomVariable.builder()
            .id(7)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variables(b, a)
            .values(0.5d, 0.8d, 0.1d, 0d, 0.3d, 0.9d)
            .build();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(d, c)
            .values(0.5d, 0.7d, 0.1d, 0.2d)
            .build();

        AssignmentStream s3 = AssignmentStream.builder()
            .variables(e, f, g)
            .values(0.25d, 0.05d, 0.15d, 0.08d, 0d, 0.09d, 0.35d, 0.07d, 0.21d, 0.16d, 0d, 0.18d)
            .build();

        AssignmentStream result = AssignmentStream.builder()
            .variables(a, b, c, d, e, f, g)
            .values(0.06250, 0.01250, 0.03750, 0.10000, 0.00000, 0.11250)
            .values(0.01250, 0.00250, 0.00750, 0.02000, 0.00000, 0.02250)
            .values(0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750)
            .values(0.02500, 0.00500, 0.01500, 0.04000, 0.00000, 0.04500)
            .values(0.01250, 0.00250, 0.00750, 0.02000, 0.00000, 0.02250)
            .values(0.00250, 0.00050, 0.00150, 0.00400, 0.00000, 0.00450)
            .values(0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150)
            .values(0.00500, 0.00100, 0.00300, 0.00800, 0.00000, 0.00900)
            .values(0.03750, 0.00750, 0.02250, 0.06000, 0.00000, 0.06750)
            .values(0.00750, 0.00150, 0.00450, 0.01200, 0.00000, 0.01350)
            .values(0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450)
            .values(0.01500, 0.00300, 0.00900, 0.02400, 0.00000, 0.02700)
            .values(0.02000, 0.00400, 0.01200, 0.03200, 0.00000, 0.03600)
            .values(0.00400, 0.00080, 0.00240, 0.00640, 0.00000, 0.00720)
            .values(0.02800, 0.00560, 0.01680, 0.04480, 0.00000, 0.05040)
            .values(0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.02250, 0.00450, 0.01350, 0.03600, 0.00000, 0.04050)
            .values(0.00450, 0.00090, 0.00270, 0.00720, 0.00000, 0.00810)
            .values(0.03150, 0.00630, 0.01890, 0.05040, 0.00000, 0.05670)
            .values(0.00900, 0.00180, 0.00540, 0.01440, 0.00000, 0.01620)
            .values(0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750)
            .values(0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150)
            .values(0.12250, 0.02450, 0.07350, 0.19600, 0.00000, 0.22050)
            .values(0.03500, 0.00700, 0.02100, 0.05600, 0.00000, 0.06300)
            .values(0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150)
            .values(0.00350, 0.00070, 0.00210, 0.00560, 0.00000, 0.00630)
            .values(0.02450, 0.00490, 0.01470, 0.03920, 0.00000, 0.04410)
            .values(0.00700, 0.00140, 0.00420, 0.01120, 0.00000, 0.01260)
            .values(0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450)
            .values(0.01050, 0.00210, 0.00630, 0.01680, 0.00000, 0.01890)
            .values(0.07350, 0.01470, 0.04410, 0.11760, 0.00000, 0.13230)
            .values(0.02100, 0.00420, 0.01260, 0.03360, 0.00000, 0.03780)
            .values(0.04000, 0.00800, 0.02400, 0.06400, 0.00000, 0.07200)
            .values(0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440)
            .values(0.05600, 0.01120, 0.03360, 0.08960, 0.00000, 0.10080)
            .values(0.01600, 0.00320, 0.00960, 0.02560, 0.00000, 0.02880)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.04500, 0.00900, 0.02700, 0.07200, 0.00000, 0.08100)
            .values(0.00900, 0.00180, 0.00540, 0.01440, 0.00000, 0.01620)
            .values(0.06300, 0.01260, 0.03780, 0.10080, 0.00000, 0.11340)
            .values(0.01800, 0.00360, 0.01080, 0.02880, 0.00000, 0.03240)
            .build();

        Assert.assertEquals(result.collect(Collectors.toSet()),
            s1.concat(s2, s3).product().collect(Collectors.toSet()));
    }

    @Test
    public void testParallelProductCase1() {
        RandomVariable<String, Integer> var1 = RandomVariable.builder()
            .id("a")
            .events(1, 2)
            .build();

        RandomVariable<String, Integer> var2 = RandomVariable.builder()
            .id("b")
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variable(var1)
            .values(0.11d, 0.89d)
            .build()
            .parallel();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(var2, var1)
            .values(0.59d, 0.41d)
            .values(0.22d, 0.78d)
            .build()
            .parallel();

        AssignmentStream s3 = AssignmentStream.builder()
            .variables(var1, var2)
            .values(0.0649d, 0.1958d)
            .values(0.0451d, 0.6942d)
            .build();

        Assert.assertEquals(s3.collect(Collectors.toSet()),
            s1.concat(s2).product().collect(Collectors.toSet()));
    }

    @Test
    public void testParallelProductCase2() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder()
            .id(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> b = RandomVariable.builder()
            .id(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> c = RandomVariable.builder()
            .id(3)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variables(b, a)
            .values(0.5d, 0.8d)
            .values(0.1d, 0d)
            .values(0.3d, 0.9d)
            .build()
            .parallel();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(c, b)
            .values(0.5d, 0.7d)
            .values(0.1d, 0.2d)
            .build()
            .parallel();

        AssignmentStream s3 = AssignmentStream.builder()
            .variables(a, b, c)
            .values(0.25d, 0.05d, 0.15d)
            .values(0.08d, 0d, 0.09d)
            .values(0.35d, 0.07d, 0.21d)
            .values(0.16d, 0d, 0.18d)
            .build();

        Assert.assertEquals(s3.collect(Collectors.toList()),
            s1.concat(s2).product().collect(Collectors.toList()));
    }

    @Test
    public void testParallelProductCase3() {
        RandomVariable<Integer, Integer> a = RandomVariable.builder()
            .id(1)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> b = RandomVariable.builder()
            .id(2)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> c = RandomVariable.builder()
            .id(3)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> d = RandomVariable.builder()
            .id(4)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> e = RandomVariable.builder()
            .id(5)
            .events(1, 2, 3)
            .build();

        RandomVariable<Integer, Integer> f = RandomVariable.builder()
            .id(6)
            .events(1, 2)
            .build();

        RandomVariable<Integer, Integer> g = RandomVariable.builder()
            .id(7)
            .events(1, 2)
            .build();

        AssignmentStream s1 = AssignmentStream.builder()
            .variables(b, a)
            .values(0.5d, 0.8d, 0.1d, 0d, 0.3d, 0.9d)
            .build();

        AssignmentStream s2 = AssignmentStream.builder()
            .variables(d, c)
            .values(0.5d, 0.7d, 0.1d, 0.2d)
            .build();

        AssignmentStream s3 = AssignmentStream.builder()
            .variables(e, f, g)
            .values(0.25d, 0.05d, 0.15d, 0.08d, 0d, 0.09d, 0.35d, 0.07d, 0.21d, 0.16d, 0d, 0.18d)
            .build();

        AssignmentStream result = AssignmentStream.builder()
            .variables(a, b, c, d, e, f, g)
            .values(0.06250, 0.01250, 0.03750, 0.10000, 0.00000, 0.11250)
            .values(0.01250, 0.00250, 0.00750, 0.02000, 0.00000, 0.02250)
            .values(0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750)
            .values(0.02500, 0.00500, 0.01500, 0.04000, 0.00000, 0.04500)
            .values(0.01250, 0.00250, 0.00750, 0.02000, 0.00000, 0.02250)
            .values(0.00250, 0.00050, 0.00150, 0.00400, 0.00000, 0.00450)
            .values(0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150)
            .values(0.00500, 0.00100, 0.00300, 0.00800, 0.00000, 0.00900)
            .values(0.03750, 0.00750, 0.02250, 0.06000, 0.00000, 0.06750)
            .values(0.00750, 0.00150, 0.00450, 0.01200, 0.00000, 0.01350)
            .values(0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450)
            .values(0.01500, 0.00300, 0.00900, 0.02400, 0.00000, 0.02700)
            .values(0.02000, 0.00400, 0.01200, 0.03200, 0.00000, 0.03600)
            .values(0.00400, 0.00080, 0.00240, 0.00640, 0.00000, 0.00720)
            .values(0.02800, 0.00560, 0.01680, 0.04480, 0.00000, 0.05040)
            .values(0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.02250, 0.00450, 0.01350, 0.03600, 0.00000, 0.04050)
            .values(0.00450, 0.00090, 0.00270, 0.00720, 0.00000, 0.00810)
            .values(0.03150, 0.00630, 0.01890, 0.05040, 0.00000, 0.05670)
            .values(0.00900, 0.00180, 0.00540, 0.01440, 0.00000, 0.01620)
            .values(0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750)
            .values(0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150)
            .values(0.12250, 0.02450, 0.07350, 0.19600, 0.00000, 0.22050)
            .values(0.03500, 0.00700, 0.02100, 0.05600, 0.00000, 0.06300)
            .values(0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150)
            .values(0.00350, 0.00070, 0.00210, 0.00560, 0.00000, 0.00630)
            .values(0.02450, 0.00490, 0.01470, 0.03920, 0.00000, 0.04410)
            .values(0.00700, 0.00140, 0.00420, 0.01120, 0.00000, 0.01260)
            .values(0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450)
            .values(0.01050, 0.00210, 0.00630, 0.01680, 0.00000, 0.01890)
            .values(0.07350, 0.01470, 0.04410, 0.11760, 0.00000, 0.13230)
            .values(0.02100, 0.00420, 0.01260, 0.03360, 0.00000, 0.03780)
            .values(0.04000, 0.00800, 0.02400, 0.06400, 0.00000, 0.07200)
            .values(0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440)
            .values(0.05600, 0.01120, 0.03360, 0.08960, 0.00000, 0.10080)
            .values(0.01600, 0.00320, 0.00960, 0.02560, 0.00000, 0.02880)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000)
            .values(0.04500, 0.00900, 0.02700, 0.07200, 0.00000, 0.08100)
            .values(0.00900, 0.00180, 0.00540, 0.01440, 0.00000, 0.01620)
            .values(0.06300, 0.01260, 0.03780, 0.10080, 0.00000, 0.11340)
            .values(0.01800, 0.00360, 0.01080, 0.02880, 0.00000, 0.03240)
            .build()
            .parallel();

        Assert.assertEquals(result.collect(Collectors.toSet()),
            s1.concat(s2, s3).product().collect(Collectors.toSet()));
    }
}
