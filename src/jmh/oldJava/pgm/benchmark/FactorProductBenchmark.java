package pgm.benchmark;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings({"checkstyle:magicnumber", "checkstyle:designforextension"})
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MINUTES)
@Warmup(iterations = 5, time = 5)
@Measurement(iterations = 10, time = 10)
@State(Scope.Benchmark)
@Fork(1)
@Threads(8)
public class FactorProductBenchmark {

    private static final List<Person> PERSONS = new ArrayList<>();

    private static final class Person {

        private final String name;
        private final int age;

        private Person(final String n, final int a) {
            name = n;
            age = a;
        }

        public int getAge() {
            return age;
        }
    }

    static {
        for (int i = 0; i < 100000000; i++) {
            PERSONS.add(new Person("random", 26));
        }
    }

    @Benchmark
    public void sequentialCollectors() {
        PERSONS.stream().collect(Collectors.summingInt(p -> p.getAge()));
    }

    @Benchmark
    public void parallelCollectors() {
        PERSONS.parallelStream().collect(Collectors.summingInt(p -> p.getAge()));
    }

    @Benchmark
    public void sequentialMapSum() {
        PERSONS.stream().mapToInt(p -> p.getAge()).sum();
    }

    @Benchmark
    public void parallelMapSum() {
        PERSONS.parallelStream().mapToInt(p -> p.getAge()).sum();
    }

    @Benchmark
    public int simpleSum() {
        int sum = 0;
        for (Person person : PERSONS) {
            sum += person.getAge();
        }
        return sum;
    }
}

