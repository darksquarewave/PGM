package io.pgm.discrete.core;

import java.util.Spliterator;
import java.util.function.Consumer;

final class UtilitySpliterators {

   public static final class ConcatSpliterator<T, S extends Spliterator<T>> implements Spliterator<T> {

        private final S[] spliterators;
        private final int fence;
        private int index;
        private final boolean unsized;

        @SafeVarargs
        ConcatSpliterator(final S ... s) {
            spliterators = s;
            fence = s.length;

            long size = 0;
            for (S spliterator : spliterators) {
                size += spliterator.estimateSize();
            }

            unsized = size < 0;
        }

        @Override
        public Spliterator<T> trySplit() {
            if (index < fence - 1) {
                return spliterators[index++];
            } else {
                return spliterators[index].trySplit();
            }
        }

        @Override
        public boolean tryAdvance(final Consumer<? super T> consumer) {
            boolean hasNext = false;

            while (index < fence && !hasNext) {
                hasNext = spliterators[index].tryAdvance(consumer);
                if (!hasNext) {
                    index++;
                }
            }

            return hasNext;
        }

        @Override
        public long estimateSize() {
            if (index < fence - 1) {
                long size = 0;

                for (int i = index; i < fence; i++) {
                    size += spliterators[i].estimateSize();
                }

                if (size >= 0) {
                    return size;
                } else {
                    return Long.MAX_VALUE;
                }

            } else {
                return spliterators[index].estimateSize();
            }
        }

        @Override
        public int characteristics() {
            int characteristics = 0;

            for (int i = index; i < fence; i++) {
                characteristics &= spliterators[i].characteristics();
            }

            return characteristics & ~(Spliterator.DISTINCT | Spliterator.SORTED
                    | (unsized ? Spliterator.SIZED | Spliterator.SUBSIZED : 0));
        }
    }
}
