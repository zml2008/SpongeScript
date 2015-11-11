package ninja.leaping.spongescript.resources;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A resource stored locally
 */
public class LocalResource implements CacheableResource {
    private final Path location;

    public LocalResource(Path location) {
        this.location = location;
    }

    @Override
    public Reader getReader() throws IOException {
        return Files.newBufferedReader(location, StandardCharsets.UTF_8);
    }

    @Override
    public String getName() {
        return location.getFileName().toString();
    }
}
