package io.pgm.discrete.core;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MINUTES)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 10, time = 10)
@State(Scope.Benchmark)
@Fork(1)
@Threads(8)
public class FactorProductBenchmark {

    private static final RandomVariable[] VARS;

    static {
        VARS = new RandomVariable[10];
        for (int i = 0; i < VARS.length; i++) {
            VARS[i] = RandomVariable.id(i).events(1, 2, 3).build();
        }

    }

    private AssignmentStream getStream() {
        Random random = new Random();
        AssignmentStream source = AssignmentStream.builder().variable(VARS[0])
                .value(random.nextDouble())
                .value(random.nextDouble())
                .value(random.nextDouble())
                .build();


        AssignmentStream[] streams = new AssignmentStream[VARS.length];
        for (int i = 1; i < VARS.length; i++) {
            streams[i] = AssignmentStream.builder().variable(VARS[i])
                    .value(random.nextDouble())
                    .value(random.nextDouble())
                    .value(random.nextDouble())
                    .build();
        }

        return source.concat(streams);

    }
    @Benchmark
    public void sequentialProduct() {
        long t1 = System.nanoTime();
        getStream().product();
        long t2 = System.nanoTime();
        System.out.println("seq product: " + (t2 - t1) / 1e6);
    }

    @Benchmark
    public void sequentialSum() {
        long t1 = System.nanoTime();
        getStream().sum();
        long t2 = System.nanoTime();
        System.out.println("seq sum: " + (t2 - t1)/1e6);
    }

    @Benchmark
    public void parallelProduct() {
        long t1 = System.nanoTime();
        final int parallelism = 10;
        ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);

        try {
            forkJoinPool.submit(() ->
                    getStream().parallel().product()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (forkJoinPool != null) {
                forkJoinPool.shutdown(); //always remember to shutdown the pool
            }
        }

        long t2 = System.nanoTime();
        System.out.println("par product: " + (t2 - t1) / 1e6);
    }

    @Benchmark
    public void parallelSum() {
        long t1 = System.nanoTime();
        final int parallelism = 10;
        ForkJoinPool forkJoinPool = new ForkJoinPool(parallelism);

        try {
            forkJoinPool.submit(() ->
                    getStream().parallel().sum()
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (forkJoinPool != null) {
                forkJoinPool.shutdown(); //always remember to shutdown the pool
            }
        }

        long t2 = System.nanoTime();
        System.out.println("par sum: " + (t2 - t1) / 1e6);
    }
}
