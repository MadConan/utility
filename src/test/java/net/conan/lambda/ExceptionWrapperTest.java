package net.conan.lambda;

import org.junit.Test;

/**
 * @author Conan Dombroski ()
 */
public class ExceptionWrapperTest {

    @Test(expected = IllegalStateException.class)
    public void testWrapSupplier() throws Exception {
        ExceptionWrapper.wrapSupplier(() -> {throw new Exception("TEST");}).get();
    }

    @Test(expected = IllegalStateException.class)
    public void testWrapFunction() throws Exception {
        ExceptionWrapper.wrapFunction(o -> {throw new Exception("Test");}).apply(new Object());
    }

    @Test(expected = IllegalStateException.class)
    public void testWrapConsumer() throws Exception {
        ExceptionWrapper.wrapConsumer(o -> {throw new Exception("Test");}).accept(new Object());
    }
}