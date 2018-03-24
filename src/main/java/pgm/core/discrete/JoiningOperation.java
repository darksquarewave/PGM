package pgm.core.discrete;

public interface JoiningOperation<T, B extends AbstractJoiningBuilder<T, B>> {

    B and(T t);
}
