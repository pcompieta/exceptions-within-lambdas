package com.pcompieta.javautils;

import java.util.function.*;

public final class LambdaExceptionUtil {

    @FunctionalInterface
    public interface Consumer_WithExceptions<T, E extends Exception> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface BiConsumer_WithExceptions<T, K, E extends Exception> {
        void accept(T t, K k) throws E;
    }

    @FunctionalInterface
    public interface Function_WithExceptions<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface BiFunction_WithExceptions<T, K, R, E extends Exception> {
        R apply(T t, K k) throws E;
    }

    @FunctionalInterface
    public interface Supplier_WithExceptions<T, E extends Exception> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface Predicate_WithExceptions<T, E extends Exception> {
        boolean test(T t) throws E;
    }

    @FunctionalInterface
    public interface BiPredicate_WithExceptions<T, K, E extends Exception> {
        boolean test(T t, K k) throws E;
    }

    /**
     * .forEach(rethrowConsumer(name -> System.out.println(Class.forName(name))));
     */
    public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer) throws E {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception exception) {
                throwActualException(exception);
            }
        };
    }

    public static <T, K, E extends Exception> BiConsumer<T, K> rethrowBiConsumer(BiConsumer_WithExceptions<T, K, E> consumer) throws E {
        return (t, k) -> {
            try {
                consumer.accept(t, k);
            } catch (Exception exception) {
                throwActualException(exception);
            }
        };
    }

    /**
     * .map(rethrowFunction(name -> Class.forName(name))) or .map(rethrowFunction(Class::forName))
     */
    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function) throws E  {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception exception) {
                return throwActualException(exception);
            }
        };
    }


    public static <T, K, R, E extends Exception> BiFunction<T, K, R> rethrowBiFunction(BiFunction_WithExceptions<T, K, R, E> function) throws E  {
        return (t, k) -> {
            try {
                return function.apply(t, k);
            } catch (Exception exception) {
                return throwActualException(exception);
            }
        };
    }

    /**
     * rethrowSupplier(() -> new StringJoiner(new String(new byte[]{77, 97, 114, 107}, "UTF-8"))),
     */
    public static <T, E extends Exception> Supplier<T> rethrowSupplier(Supplier_WithExceptions<T, E> function) throws E {
        return () -> {
            try {
                return function.get();
            } catch (Exception exception) {
                return throwActualException(exception);
            }
        };
    }

    /**
     * .filter(rethrowPredicate(t -> t.isActive()))
     */
    public static <T, E extends Exception> Predicate<T> rethrowPredicate(Predicate_WithExceptions<T, E> predicate) throws E {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Exception exception) {
                return throwActualException(exception);
            }
        };
    }

    public static <T, K, E extends Exception> BiPredicate<T, K> rethrowBiPredicate(BiPredicate_WithExceptions<T, K, E> predicate) throws E {
        return (t, k) -> {
            try {
                return predicate.test(t, k);
            } catch (Exception exception) {
                return throwActualException(exception);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T, E extends Exception> T throwActualException(Exception exception) throws E {
        throw (E) exception;
    }

}