package pgm.factor;

import org.junit.Assert;
import org.junit.Test;

public class DiscreteFactorTest {

    @Test
    public void testConstruction() {
        DiscreteFactor factor = new DiscreteFactor.Builder()
            .var(1, 2, 4)
            .card(2, 2, 2)
            .val(0, 0, 0, 0, 0, 0, 0, 0)
            .build();

        Assert.assertEquals(3, factor.variables().size());
    }

    @Test
    public void testGetAssignment() {
        DiscreteFactor factor = new DiscreteFactor.Builder()
            .var(1, 2, 3)
            .card(2, 3, 2)
            .val(0.2, 0.8, 0.1, 0.9, 0.4, 0.6, 0.3, 0.7, 0.9, 0.1, 0.2, 0.8)
            .build();

        final double delta = 1e-9;
        Assert.assertEquals(0.2, factor.assignment(1, 1, 1), delta);
        Assert.assertEquals(0.1, factor.assignment(1, 2, 1), delta);
        Assert.assertEquals(0.4, factor.assignment(1, 3, 1), delta);
        Assert.assertEquals(0.8, factor.assignment(2, 1, 1), delta);
        Assert.assertEquals(0.9, factor.assignment(2, 2, 1), delta);
        Assert.assertEquals(0.6, factor.assignment(2, 3, 1), delta);
        Assert.assertEquals(0.3, factor.assignment(1, 1, 2), delta);
        Assert.assertEquals(0.9, factor.assignment(1, 2, 2), delta);
        Assert.assertEquals(0.2, factor.assignment(1, 3, 2), delta);
        Assert.assertEquals(0.7, factor.assignment(2, 1, 2), delta);
        Assert.assertEquals(0.1, factor.assignment(2, 2, 2), delta);
        Assert.assertEquals(0.8, factor.assignment(2, 3, 2), delta);
    }

    @Test
    public void testProductTwoFactors1() {
        DiscreteFactor a = new DiscreteFactor.Builder()
            .var(1)
            .card(2)
            .val(0.11, 0.89)
            .build();

        DiscreteFactor b = new DiscreteFactor.Builder()
            .var(2, 1)
            .card(2, 2)
            .val(0.59, 0.41, 0.22, 0.78)
            .build();

        DiscreteFactor c = new DiscreteFactor.Builder()
            .var(1, 2)
            .card(2, 2)
            .val(0.0649, 0.1958, 0.0451, 0.6942)
            .build();

        Assert.assertEquals(a.product(b), c);
    }

    @Test
    public void testProductTwoFactors2() {
        DiscreteFactor x = new DiscreteFactor.Builder()
            .var(2, 1)
            .card(2, 3)
            .val(0.5, 0.8, 0.1, 0, 0.3, 0.9)
            .build();

        DiscreteFactor y = new DiscreteFactor.Builder()
            .var(3, 2)
            .card(2, 2)
            .val(0.5, 0.7, 0.1, 0.2)
            .build();

        DiscreteFactor z = new DiscreteFactor.Builder()
            .var(1, 2, 3)
            .card(3, 2, 2)
            .val(0.25, 0.05, 0.15, 0.08, 0, 0.09, 0.35, 0.07, 0.21, 0.16, 0, 0.18)
            .build();

        Assert.assertEquals(z, x.product(y));
    }

    @Test
    public void productThreeFactors() {
        DiscreteFactor x = new DiscreteFactor.Builder()
            .var(2, 1)
            .card(2, 3)
            .val(0.5, 0.8, 0.1, 0, 0.3, 0.9)
            .build();

        DiscreteFactor y = new DiscreteFactor.Builder()
            .var(4, 3)
            .card(2, 2)
            .val(0.5, 0.7, 0.1, 0.2)
            .build();

        DiscreteFactor z = new DiscreteFactor.Builder()
            .var(5, 6, 7)
            .card(3, 2, 2)
            .val(0.25, 0.05, 0.15, 0.08, 0, 0.09, 0.35, 0.07, 0.21, 0.16, 0, 0.18)
            .build();

        DiscreteFactor result = new DiscreteFactor.Builder()
            .var(1, 2, 3, 4, 5, 6, 7)
            .card(3, 2, 2, 2, 3, 2, 2)
            .val(0.06250, 0.01250, 0.03750, 0.10000, 0.00000, 0.11250, 0.01250, 0.00250, 0.00750, 0.02000,
                 0.00000, 0.02250, 0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750, 0.02500, 0.00500,
                 0.01500, 0.04000, 0.00000, 0.04500, 0.01250, 0.00250, 0.00750, 0.02000, 0.00000, 0.02250,
                 0.00250, 0.00050, 0.00150, 0.00400, 0.00000, 0.00450, 0.01750, 0.00350, 0.01050, 0.02800,
                 0.00000, 0.03150, 0.00500, 0.00100, 0.00300, 0.00800, 0.00000, 0.00900, 0.03750, 0.00750,
                 0.02250, 0.06000, 0.00000, 0.06750, 0.00750, 0.00150, 0.00450, 0.01200, 0.00000, 0.01350,
                 0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450, 0.01500, 0.00300, 0.00900, 0.02400,
                 0.00000, 0.02700, 0.02000, 0.00400, 0.01200, 0.03200, 0.00000, 0.03600, 0.00400, 0.00080,
                 0.00240, 0.00640, 0.00000, 0.00720, 0.02800, 0.00560, 0.01680, 0.04480, 0.00000, 0.05040,
                 0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440, 0.00000, 0.00000, 0.00000, 0.00000,
                 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                 0.02250, 0.00450, 0.01350, 0.03600, 0.00000, 0.04050, 0.00450, 0.00090, 0.00270, 0.00720,
                 0.00000, 0.00810, 0.03150, 0.00630, 0.01890, 0.05040, 0.00000, 0.05670, 0.00900, 0.00180,
                 0.00540, 0.01440, 0.00000, 0.01620, 0.08750, 0.01750, 0.05250, 0.14000, 0.00000, 0.15750,
                 0.01750, 0.00350, 0.01050, 0.02800, 0.00000, 0.03150, 0.12250, 0.02450, 0.07350, 0.19600,
                 0.00000, 0.22050, 0.03500, 0.00700, 0.02100, 0.05600, 0.00000, 0.06300, 0.01750, 0.00350,
                 0.01050, 0.02800, 0.00000, 0.03150, 0.00350, 0.00070, 0.00210, 0.00560, 0.00000, 0.00630,
                 0.02450, 0.00490, 0.01470, 0.03920, 0.00000, 0.04410, 0.00700, 0.00140, 0.00420, 0.01120,
                 0.00000, 0.01260, 0.05250, 0.01050, 0.03150, 0.08400, 0.00000, 0.09450, 0.01050, 0.00210,
                 0.00630, 0.01680, 0.00000, 0.01890, 0.07350, 0.01470, 0.04410, 0.11760, 0.00000, 0.13230,
                 0.02100, 0.00420, 0.01260, 0.03360, 0.00000, 0.03780, 0.04000, 0.00800, 0.02400, 0.06400,
                 0.00000, 0.07200, 0.00800, 0.00160, 0.00480, 0.01280, 0.00000, 0.01440, 0.05600, 0.01120,
                 0.03360, 0.08960, 0.00000, 0.10080, 0.01600, 0.00320, 0.00960, 0.02560, 0.00000, 0.02880,
                 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000, 0.00000,
                 0.00000, 0.00000, 0.00000, 0.00000, 0.04500, 0.00900, 0.02700, 0.07200, 0.00000, 0.08100,
                 0.00900, 0.00180, 0.00540, 0.01440, 0.00000, 0.01620, 0.06300, 0.01260, 0.03780, 0.10080,
                 0.00000, 0.11340, 0.01800, 0.00360, 0.01080, 0.02880, 0.00000, 0.03240)
            .build();

        Assert.assertEquals(result, x.product(y, z));
    }

    @Test
    public void testProductEmptyFactorsCase1() {
        DiscreteFactor a = new DiscreteFactor.Builder().build();
        DiscreteFactor b = new DiscreteFactor.Builder()
            .var(1)
            .card(2)
            .val(0.5, 0.5)
            .build();

        Assert.assertEquals(b, a.product(b));
    }

    @Test
    public void testProductEmptyFactorsCase2() {
        DiscreteFactor a = new DiscreteFactor.Builder().build();
        DiscreteFactor b = new DiscreteFactor.Builder().build();

        Assert.assertEquals(a, a.product(b));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductInvalidCardinalities() {
        DiscreteFactor a = new DiscreteFactor.Builder()
            .var(1)
            .card(3)
            .val(0.5, 0.2, 0.3)
            .build();

        DiscreteFactor b = new DiscreteFactor.Builder()
            .var(1)
            .card(2)
            .val(0.5, 0.5)
            .build();

        Assert.assertEquals(a, a.product(b));
    }

    @Test
    public void testMarginalizationCase1() {
        DiscreteFactor a = new DiscreteFactor.Builder()
            .var(2, 1)
            .card(2, 2)
            .val(0.59, 0.41, 0.22, 0.78)
            .build();

        DiscreteFactor z = new DiscreteFactor.Builder()
            .var(1)
            .card(2)
            .val(1, 1)
            .build();

        Assert.assertEquals(z, a.marginalize(2));
    }

    @Test
    public void testMarginalizationCase2() {
        DiscreteFactor a = new DiscreteFactor.Builder()
            .var(1, 3)
            .card(3, 2)
            .val(0.33, 0.05, 0.24, 0.51, 0.07, 0.39)
            .build();

        DiscreteFactor z = new DiscreteFactor.Builder()
            .var(3, 2, 1)
            .card(2, 2, 3)
            .val(0.25, 0.35, 0.08, 0.16, 0.05, 0.07, 0, 0, 0.15, 0.21, 0.09, 0.18)
            .build();

        Assert.assertEquals(a, z.marginalize(2));
    }
}
