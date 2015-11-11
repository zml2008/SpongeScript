package ninja.leaping.spongescript;

import java.util.function.Consumer;

import javax.script.Bindings;

/**
 * Represents a class that will populate a specific script's bindings with useful context details
 */
public interface Binding extends AutoCloseable, Consumer<Bindings> {


}
