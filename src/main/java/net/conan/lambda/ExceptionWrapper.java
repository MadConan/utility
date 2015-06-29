package net.conan.lambda;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>Checked exceptions don't play well with Lambda statements.  <code>ExceptionWrapper</code> allows
 * lambda code to be chained together without the need to in-line try/catch blocks.</p>
 * <p>It does this by utilizing an intermediary type that mimics the <code>java.util.function.*</code> type
 *  but with an added <code>throws Exception</code> clause.  This forces the compiler to
 * create the intermediary instead of the core type, allowing the lambda to be wrapped in a
 * try/catch.</p>
 * <p>
 *     Here's an example.  The following code finds all "txt" files in a directory and then merges
 *     the content into a single "txt" file.
 *     <pre>
 *    final PrintWriter writer = ExceptionWrapper.wrapSupplier(
         () -> new PrintWriter(new FileWriter(dir + File.separatorChar + mergedName))).get();

      ExceptionWrapper.wrapSupplier(() ->
      Files.list(Paths.get(dir))).get() // Get a Stream
               .filter(p -> p.toString().endsWith("txt"))               // filter them
               .map(ExceptionWrapper.wrapFunction(Files::readAllLines)) // map each file to a list of its lines
               .flatMap(List::stream)                                   // map each list to a stream of each list
               .forEach(writer::println);                               //  for each stream item, write item to writer

      writer.close();
 *     </pre>
 * </p>
 *
 * @author Conan Dombroski
 */
public class ExceptionWrapper {

    /**
     * Change {@link Function Function&lt;T,R&gt;} to be one that throws Exception.
     *
     * @param <T> Input type
     * @param <R> Return type
     */
    public interface FunctionWrapper<T,R>{
        R apply(T t) throws Exception;
    }

    /**
     * Change {@link Consumer Consumer&lt;T&gt;} to be one that throws Exception.
     *
     * @param <T> Input type
     */
    public interface ConsumerWrapper<T>{
        void accept(T t) throws Exception;
    }

    /**
     * Change {@link Supplier Supplier&lt;T&gt;} to be one that throws Exception.
     * @param <T> Output type from Supplier.get()
     */
    public interface SupplierWrapper<T> {
        T get() throws Exception;
    }

    /**
     * Take the compiler generated {@link ExceptionWrapper.SupplierWrapper} and
     * return the corresponding Supplier.
     *
     * @param supplierWrapper Compiler interpreted lambda type using SupplierWrapper
     * @param <T> Output type from Supplier.get()
     * @return {@link Supplier}
     */
    public static <T> Supplier<T> wrapSupplier(SupplierWrapper<T> supplierWrapper){
        return () -> {
            try{
                return supplierWrapper.get();
            }catch (Exception e){
                throw new IllegalStateException(e);
            }
        };
    }

    /**
     * Take the compiler generated {@link ExceptionWrapper.FunctionWrapper} and
     * return the corresponding Function.
     *
     * @param function Compiler generated FunctionWrapper
     * @param <T> Input type to function
     * @param <R> Return type from function
     * @return Function
     */
    public static <T,R> Function<T,R> wrapFunction(FunctionWrapper<T,R> function){
        return t -> {
            try{
                return function.apply(t);
            }catch (Exception e){
                throw new IllegalStateException(e);
            }
        };
    }

    /**
     * Take the compiler generated {@link ExceptionWrapper.ConsumerWrapper} and
     * return the corresponding Consumer.
     * @param consumer Compiler generated ConsumerWrapper
     * @param <T> Input type to Consumer
     * @return Consumer
     */
    public static <T> Consumer<T> wrapConsumer(ConsumerWrapper<T> consumer){
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
