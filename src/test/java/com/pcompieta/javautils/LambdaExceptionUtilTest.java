package com.pcompieta.javautils;

import org.junit.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pcompieta.javautils.LambdaExceptionUtil.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class and usage examples.
 *
 * @author Paolo Compieta
 */
public class LambdaExceptionUtilTest {

    @Test(expected = MyTestException.class)
    public void testConsumer() throws MyTestException {
        Stream.of("", null).forEach(rethrowConsumer(this::checkValue));
    }

    @Test(expected = MyTestException.class)
    public void testBiConsumer() throws MyTestException {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "");
        map.put(2, null);

        map.forEach(rethrowBiConsumer((v1, v2) -> checkValue(v1.toString(), v2)));
    }

    private void checkValue(String value) throws MyTestException {
        if(value==null) {
            throw new MyTestException();
        }
    }

    private void checkValue(String value, String value2) throws MyTestException {
        if(value==null || value2==null) {
            throw new MyTestException();
        }
    }

    private class MyTestException extends Exception { }

    private class MyMap extends HashMap<String, String> {
        {
            this.put("a", "2");
            this.put("b", "a");
            this.put("c", "1");
        }

        public <R> List<R> map(BiFunction<? super String, ? super String, ? extends R> action) {
            return this.entrySet().stream().map(e -> action.apply(e.getKey(), e.getValue())).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        public List<String> filter(BiPredicate<String,String> predicate) {
            return this.entrySet().stream().filter(e -> predicate.test(e.getKey(), e.getValue())).map(Entry::getKey).collect(Collectors.toList());
        }
    }

    @Test
    public void testConsumerRaisingExceptionInTheMiddle() {
        final MyLongAccumulator accumulator = new MyLongAccumulator();
        try {
            Stream.of(2L, 3L, 4L, null, 5L).forEach(rethrowConsumer(accumulator::add));
            fail();
        } catch (MyTestException e) {
            assertEquals(9L, accumulator.acc);
        }

    }

    @Test
    public void testBiConsumerRaisingExceptionInTheMiddle() {

        final MyLongAccumulator accumulator2 = new MyLongAccumulator();
        try {
            Map<Long, Long> map = new HashMap<>();
            map.put(1L, 1L);
            map.put(2L, 1L);
            map.put(3L, 1L);
            map.put(4L, null);
            map.put(5L, 1L);

            map.forEach(rethrowBiConsumer(accumulator2::add2));
            fail();
        } catch (MyTestException e) {
            assertEquals(9L, accumulator2.acc);
        }
    }

    private class MyLongAccumulator {
        private long acc = 0;
        public void add(Long value) throws MyTestException {
            if(value==null) {
                throw new MyTestException();
            }
            acc += value;
        }
        public void add2(Long value, Long value2) throws MyTestException {
            if(value==null || value2==null) {
                throw new MyTestException();
            }
            acc += value;
            acc += value2;
        }
    }

    @Test
    public void testFunction() throws MyTestException {
        List<Integer> sizes = Stream.of("ciao", "hello").<Integer>map(rethrowFunction(this::transform)).collect(toList());
        assertEquals(2, sizes.size());
        assertEquals(4, sizes.get(0).intValue());
        assertEquals(5, sizes.get(1).intValue());
    }

    @Test
    public void testBiFunction() throws MyTestException {
        final MyMap myMap = new MyMap();
        final List<Integer> list = myMap.<Integer>map(rethrowBiFunction((k, v) -> transform(k) + transform(v)));
        for (int i : list) {
            assertEquals(2, i);
        }
    }

    private Integer transform(String value) throws MyTestException {
        if(value==null) {
            throw new MyTestException();
        }
        return value.length();
    }

    @Test(expected = MyTestException.class)
    public void testFunctionRaisingException() throws MyTestException {
        Stream.of("ciao", null, "hello")
                .<Integer>map(rethrowFunction(this::transform))
                .collect(toList());
    }

    @Test(expected = MyTestException.class)
    public void testBiFunctionRaisingException() throws MyTestException {
        final MyMap myMap = new MyMap();
        myMap.put("c", null);
        myMap.<Integer>map(rethrowBiFunction((k, v) -> transform(k) + transform(v)));
    }

    @Test
    public void testPredicate() throws MyTestException {
        List<String> nonEmptyStrings = Stream.of("ciao", "")
                .filter(rethrowPredicate(this::notEmpty))
                .collect(toList());
        assertEquals(1, nonEmptyStrings.size());
        assertEquals("ciao", nonEmptyStrings.get(0));
    }

    @Test
    public void testBiPredicate() throws MyTestException {
        final List<String> allKeys = new MyMap().filter(rethrowBiPredicate((k, v) -> notEmpty(k)));

        assertEquals(new MyMap().size(), allKeys.size());
        int i = 0;
        for (String s : new MyMap().keySet()) {
            assertEquals(s, allKeys.get(i));
            i++;
        }
    }

    private boolean notEmpty(String value) throws MyTestException {
        if(value==null) {
            throw new MyTestException();
        }
        return !value.isEmpty();
    }

    @Test
         public void testPredicateRaisingException() throws MyTestException {
        try {
            Stream.of("ciao", null)
                    .filter(rethrowPredicate(this::notEmpty))
                    .collect(toList());
            fail();
        } catch (MyTestException e) {
            //OK
        }
    }

    @Test
    public void testBiPredicateRaisingException() throws MyTestException {
        try {
            final MyMap myMap = new MyMap();
            myMap.put("c", null);
            myMap.filter(rethrowBiPredicate((k, v) -> notEmpty(v)));
            fail();
        } catch (MyTestException e) {
            //OK
        }
    }

    @Test
    public void testSupplier() throws MyTestException {
        assertEquals(1, rethrowSupplier(this::supply).get().intValue());
    }

    private int count = 0;
    private Integer supply() throws MyTestException {
        count++;
        if(count==2){
            throw new MyTestException();
        }
        return count;
    }

    @Test
    public void testSupplierWithException() throws MyTestException {
        Supplier<Integer> stringSupplier = rethrowSupplier(this::supply);
        assertEquals(1, stringSupplier.get().intValue());
        try {
            stringSupplier.get();
            fail();
        } catch (Exception e) {
            //OK
        }
        assertEquals(3, stringSupplier.get().intValue());
    }

}