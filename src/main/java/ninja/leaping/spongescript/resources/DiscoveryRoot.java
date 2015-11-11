package ninja.leaping.spongescript.resources;

import com.google.common.base.Optional;
import ninja.leaping.spongescript.ScriptInstance;

/**
 * Created by zml on 8/15/15.
 */
public interface DiscoveryRoot extends Iterable<CacheableResource> {

    /**
     * Get the resource for the given name, updating if necessary
     *
     * @param name The name to get the resource by
     * @return The resource, if any is present
     */
     Optional<CacheableResource> getResource(String name);

}
