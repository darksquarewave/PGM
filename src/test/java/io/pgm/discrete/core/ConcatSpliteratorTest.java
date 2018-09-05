package io.pgm.discrete.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConcatSpliteratorTest {

    @Test
    @SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
    public void testConcatSpliterator() {
        Spliterator<Integer> spliterator = new UtilitySpliterators.ConcatSpliterator<>(
            IntStream.range(1, 11).spliterator(),
            IntStream.range(11, 21).spliterator(),
            IntStream.range(21, 31).spliterator(),
            IntStream.range(31, 41).spliterator(),
            IntStream.range(41, 51).spliterator(),
            IntStream.range(51, 61).spliterator());

        Stream<Integer> concatenated = StreamSupport.stream(spliterator, true);
        Assert.assertEquals(60, concatenated.collect(Collectors.toList()).size());
    }
}
