package init.app.exception;

import java.util.function.Consumer;


/**
 * Created by ivansavic on 07/11/17.
 */
public class LambdaCustomExceptionHandler {
    /*
        Custom Consumer Functional Interface which can throw an exception
     */
    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    /*
        Handling Unchecked Exceptions
     */
    public static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                throw new LambdaCustomException(ex.getMessage());
            }
        };
    }

    /*
        Handling Checked Exceptions
    */
    public static <T, E extends Exception> Consumer<T> handlingConsumerWrapper(ThrowingConsumer<T, E> throwingConsumer, Class<E> exceptionClass) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                try {
                    E exCast = exceptionClass.cast(ex);
                    throw new LambdaCustomException(exCast.getMessage());
                } catch (ClassCastException ccEx) {
                    throw new LambdaCustomException(ex.getMessage());
                }
            }
        };
    }
}
