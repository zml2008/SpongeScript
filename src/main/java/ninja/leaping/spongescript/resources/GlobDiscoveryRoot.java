package ninja.leaping.spongescript.resources;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;

/**
 * Created by zml on 8/15/15.
 */
public class GlobDiscoveryRoot implements DiscoveryRoot {
    private final Path root;
    private final String matchAgainst;
    private final PathMatcher matcher;

    public GlobDiscoveryRoot(Path root, String matchAgainst) {
        this.root = root;
        this.matchAgainst = matchAgainst;
        this.matcher = root.getFileSystem().getPathMatcher("glob:" + matchAgainst);
    }


    @Override
    public Optional<CacheableResource> getResource(String name) {
        return Optional.absent(); // TODO
    }

    @Override
    public Iterator<CacheableResource> iterator() {
        try {
            return Iterators.transform(Files.newDirectoryStream(root, matchAgainst).iterator(), LocalResource::new);
        } catch (IOException e) {
            return Iterators.emptyIterator();
        }
    }
}
