package ninja.leaping.spongescript.languages;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import ninja.leaping.spongescript.resources.CacheableResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import javax.script.CompiledScript;
import javax.script.ScriptException;

/**
 * Represents a specific language that plugins can be written in
 */
public interface ScriptLanguage {
    List<String> BLACKLISTED_CLASSES = Lists.newArrayList("java.lang.Thread");

    Optional<CompiledScript> forResource(CacheableResource path) throws ScriptException;

    /**
     * Get the extensions of files handled by this scripting language
     *
     * @return The extensions handled
     */
    Set<String> getHandledExtensions();
}
