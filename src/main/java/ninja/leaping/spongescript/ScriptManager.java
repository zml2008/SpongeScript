package ninja.leaping.spongescript;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import ninja.leaping.spongescript.languages.JSR223Language;
import ninja.leaping.spongescript.languages.NashornLanguage;
import ninja.leaping.spongescript.languages.ScriptLanguage;
import ninja.leaping.spongescript.resources.CacheableResource;
import ninja.leaping.spongescript.resources.DiscoveryRoot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import javax.script.CompiledScript;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

/**
 * Manages loading, unloading of scripts and so on
 */
public class ScriptManager {
    private final Set<ScriptLanguage> languages = new HashSet<>();
    private final Map<String, ScriptLanguage> extensionLanguageMapping = new ConcurrentHashMap<>();
    private final List<DiscoveryRoot> discoveryRoots;
    private final ConcurrentMap<String, ScriptInstance> scripts = new ConcurrentHashMap<>();
    private final Set<Supplier<Binding>> bindingFactories = new HashSet<>();

    public ScriptManager(List<DiscoveryRoot> discoveryRoots) {
        this.discoveryRoots = discoveryRoots;
        registerLanguage(new NashornLanguage());
        for (ScriptEngineFactory factory : JSR223Language.MANAGER.getEngineFactories()) {
            registerLanguage(new JSR223Language(factory));
        }
    }

    // -- Compiling resources to scripts
    public void registerLanguage(ScriptLanguage language) {
        if (this.languages.add(language)) {
            language.getHandledExtensions().forEach(ext -> extensionLanguageMapping.put(ext.toLowerCase(), language));
        }
    }

    public void addBindingFactory(Supplier<Binding> factory) {
        bindingFactories.add(factory);
    }

    private Optional<CompiledScript> loadScript(CacheableResource resource) throws ScriptException {
        final String resourceName = resource.getName();
        int dotIndex = resourceName.lastIndexOf(".");
        if (dotIndex == -1) {
            return Optional.absent();
        }
        String extension = resourceName.substring(dotIndex, resourceName.length());
        ScriptLanguage language = extensionLanguageMapping.get(extension);
        if (language == null) {
            return Optional.absent();
        }
        return language.forResource(resource);
    }

    private static class LoadError {
        private final CacheableResource resource;
        private final ScriptException exception;

        private LoadError(CacheableResource resource, ScriptException exception) {
            this.resource = resource;
            this.exception = exception;
        }

        public CacheableResource getResource() {
            return resource;
        }

        public ScriptException getException() {
            return exception;
        }
    }

    public void loadAll() {
        final Set<LoadError> loadErrors = new HashSet<>();
        discoveryRoots.parallelStream().flatMap(root -> StreamSupport.stream(root.spliterator(), true)).map(res -> {
            try {
                return loadScript(res);
            } catch (ScriptException e) {
                loadErrors.add(new LoadError(res, e));
                return Optional.<CompiledScript>absent();
            }
        }).filter(Optional::isPresent).map(Optional::get);

        Map<String, ScriptInstance> scripts = new HashMap<>();

        for (ScriptInstance value : scripts.values()) {
            try {
                value.enable();
            } catch (ScriptException e) {
                loadErrors.add(new LoadError(value.getScript(), e));
            }
        }
    }

    public void unloadAll() {
        for (ScriptInstance value : scripts.values()) {
            value.close();
        }
    }

    public Optional<ScriptInstance> getScript(String name) {
        return Optional.fromNullable(scripts.get(checkNotNull(name, "name").toLowerCase()));
    }

    public CompletableFuture<ScriptInstance> refreshOrReloadScript(String name) {
        return null;
    }
}
