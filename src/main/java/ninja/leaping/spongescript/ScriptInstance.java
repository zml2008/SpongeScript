package ninja.leaping.spongescript;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * Represents an instance of a script that needs to be present. This script instance can only occur once
 */
public class ScriptInstance implements AutoCloseable {
    private enum State {
        INITIALIZED, ENABLING, ENABLED, DISABLING, ERRORED, DISABLED;
    }
    private final CompiledScript script;
    private final AtomicReference<State> state = new AtomicReference<>(State.INITIALIZED);
    private final Set<Binding> bindings;
    private final Map<String, Object> exports = new ConcurrentHashMap<>();



    public ScriptInstance(CompiledScript script, Set<Binding> bindings) {
        this.script = script;
        this.bindings = new HashSet<>(bindings);
        this.bindings.add(new Binding() {
            @Override
            public void close() throws Exception {
            }

            @Override
            public void accept(Bindings bindings) {
                bindings.put("exports", exports);
            }
        });
    }

    public void enable() throws ScriptException {
        if (state.compareAndSet(State.INITIALIZED, State.ENABLING)) {
            Bindings bindings = script.getEngine().createBindings();
            for (Binding binding : this.bindings) {
                binding.accept(bindings);
            }

            try {
                script.eval(bindings);
            } catch (ScriptException e) {
                try {
                    close();
                } catch (Exception e1) {// oh noes, we'll figure this out later I guess?
                }

                state.set(State.ERRORED);
                throw e;
            }
            state.set(State.ENABLED);
        }
    }

    public Map<String, Object> getExports() {
        return this.exports;
    }

    @Override
    public void close() throws Exception {
        if (state.compareAndSet(State.ENABLED, State.DISABLING)) {
            for (Binding bind : bindings) {
                bind.close();
            }
            state.set(State.DISABLED);
        }
    }
}
