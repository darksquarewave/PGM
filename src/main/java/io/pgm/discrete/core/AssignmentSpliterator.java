package io.pgm.discrete.core;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

final class AssignmentSpliterator implements Spliterator<Assignment> {

    private final MultiVarAssignmentSpliterator source;
    private final List<? extends Double> values;
    private @Nullable MultiVarAssignment current;

    AssignmentSpliterator(final Collection<? extends RandomVariable> vars, final List<? extends Double> vals) {
        this(new MultiVarAssignmentSpliterator(vars), vals);
    }

    private AssignmentSpliterator(final MultiVarAssignmentSpliterator split, final List<? extends Double> vals) {
        source = split;
        values = vals;
    }

    private void setVarAssignment(final MultiVarAssignment ga) {
        current = ga;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super Assignment> action) {
        if (!source.tryAdvance(this::setVarAssignment)) {
            return false;
        }
        if (source.index() > values.size()) {
            throw new IllegalArgumentException();
        }
        if (current == null) {
            throw new NullPointerException();
        }
        action.accept(current.toAssignment(values.get(source.index() - 1)));
        return true;
    }

    @SuppressWarnings({"return.type.incompatible"})
    @Override
    public Spliterator<Assignment> trySplit() {
        MultiVarAssignmentSpliterator prefix = source.trySplit();
        if (prefix == null) {
            return null;
        }

        return new AssignmentSpliterator(prefix, values);
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public int characteristics() {
        return source.characteristics();
    }
}

