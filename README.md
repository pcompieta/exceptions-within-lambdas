# LambdaExceptionUtil

A collection of wrapper methods to **allow throw/catch of explicit exceptions
within lambda expressions**, whereas it would not normally be allowed.

As an example, the below code using a `Consumer` would not even compile:

```java
     void myLambdaExperiment() {
         Stream.of("hello", null, "unreachable")
             .forEach(s -> checkValue(s)); // <-- WOULD NOT COMPILE
     }

     void checkValue(String value) throws MyTestException {
         if(value==null) {
             throw new MyTestException();
         }
     }
```

Instead, using this LambdaExceptionUtil's wrapper methods *would both compile and
ask the developer to declare the thrown exception* in the caller method.
E.g.

```java
    void myLambdaExperiment() throws MyTestException { // <-- CORRECTLY RETHROWS
         Stream.of("hello", null, "unreachable")
             .forEach(rethrowConsumer(s -> checkValue(s))); // <-- DOES COMPILE!
     }
```

Wrapper methods are provided for the following basic Functional Interfaces:
* Consumer
* BiConsumer  
* Function    
* BiFunction  
* Supplier    
* Predicate   
* BiPredicate 
