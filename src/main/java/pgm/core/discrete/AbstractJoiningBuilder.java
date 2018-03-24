package pgm.core.discrete;

import java.util.ArrayList;
import java.util.Collection;

abstract class AbstractJoiningBuilder<T, B extends AbstractJoiningBuilder<T, B>> implements JoiningOperation<T, B> {

    private final Collection<T> items;

    AbstractJoiningBuilder(final Collection<? extends T> coll) {
        items = new ArrayList<>();
        items.addAll(coll);
    }

    void add(final T t) {
        items.add(t);
    }

    Collection<T> items() {
        return items;
    }

    void addAll(final Collection<? extends T> coll) {
        items.addAll(coll);
    }
}
