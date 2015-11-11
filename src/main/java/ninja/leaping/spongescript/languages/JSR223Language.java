package ninja.leaping.spongescript.languages;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import ninja.leaping.spongescript.resources.CacheableResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Generic scripting language compatible with any JSR223-compatible implementation
 */
public class JSR223Language implements ScriptLanguage {
    public static final ScriptEngineManager MANAGER = new ScriptEngineManager();
    protected final ScriptEngineFactory factory;

    public JSR223Language(ScriptEngineFactory factory) {
        this.factory = factory;
    }

    protected ScriptEngine getScriptEngine() {
        return factory.getScriptEngine();
    }

    @Override
    public Optional<CompiledScript> forResource(CacheableResource path) throws ScriptException {
        ScriptEngine engine = getScriptEngine();
        if (!(engine instanceof Compilable)) {
            throw new ScriptException("Engine " + engine + " cannot compile scripts!");
        }

        try {
            return Optional.of(((Compilable) engine).compile(path.getReader()));
        } catch (IOException e) {
            throw new ScriptException(e);
        }
    }

    @Override
    public Set<String> getHandledExtensions() {
        return ImmutableSet.copyOf(factory.getExtensions());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JSR223Language)) {
            return false;
        }
        JSR223Language that = (JSR223Language) o;
        return Objects.equals(factory, that.factory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factory);
    }
}
