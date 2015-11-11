package ninja.leaping.spongescript.resources;

import java.io.IOException;
import java.io.Reader;

/**
 * A common interface for resources that may originally be of a remote origin but have been cached.
 */
public interface CacheableResource {
    Reader getReader() throws IOException;
    String getName();
}
