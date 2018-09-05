package io.pgm.discrete.core;

import java.util.Collection;
import java.util.Spliterator;
import java.util.function.Consumer;

final class MultiVarAssignmentSpliterator implements Spliterator<MultiVarAssignment> {

    private final Collection<? extends RandomVariable> randomVars;
    private final int fence;
    private int index;

    MultiVarAssignmentSpliterator(final Collection<? extends RandomVariable> vars) {
        this(vars, 0, AssignmentUtils.cardinality(vars));
    }

    private MultiVarAssignmentSpliterator(final Collection<? extends RandomVariable> vars, final int i, final int f) {
        randomVars = vars;
        index = i;
        fence = f;
    }

    int index() {
        return index;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super MultiVarAssignment> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        if (index >= 0 && index < fence) {
            MultiVarAssignment.Builder builder = MultiVarAssignment.builder();
            int cp = 1;
            for (RandomVariable v : randomVars) {
                int eventIndex = (index / cp) % v.cardinality();
                cp *= v.cardinality();
                builder.add(v.setIndex(eventIndex));
            }
            index++;
            action.accept(builder.build());
            return true;
        }
        return false;
    }

    @Override
    @SuppressWarnings({"return.type.incompatible"})
    public MultiVarAssignmentSpliterator trySplit() {
        int lo = index;
        int mid = (lo + fence) >>> 1;
        if (lo < mid) {
            index = mid;
            return new MultiVarAssignmentSpliterator(randomVars, lo, mid);
        } else {
            return null;
        }
    }

    @Override
    public long estimateSize() {
        return (long) (fence - index);
    }

    @Override
    public int characteristics() {
        return SIZED | SUBSIZED | CONCURRENT | IMMUTABLE;
    }
}


