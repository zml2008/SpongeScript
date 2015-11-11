package ninja.leaping.spongescript.plugin;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.spongescript.ScriptManager;
import ninja.leaping.spongescript.SpongeScriptConfig;
import ninja.leaping.spongescript.resources.DiscoveryRoot;
import ninja.leaping.spongescript.resources.GlobDiscoveryRoot;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.util.PEBKACException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * A simple sponge plugin
 */
@Plugin(id = PomData.ARTIFACT_ID, name = PomData.NAME, version = PomData.VERSION)
public class SpongeScriptPlugin {

    // These are all injected on plugin load for users to work from
    @Inject private Logger logger;
    // Give us a configuration to work from
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    @Inject @ConfigDir(sharedRoot = false) private File configDir;
    @Inject private GuiceObjectMapperFactory guiceObjectMapperFactory;
    @Inject private Game game;

    private SpongeScriptConfig config;
    private final AtomicReference<ScriptManager> manager = new AtomicReference<>();

    @Subscribe
    public void onPreInit(PreInitializationEvent event) {
        try {
            reload();
        } catch (IOException e) {
            throw new PEBKACException(e);
        }
    }

    private void reload() throws IOException {
        ConfigurationNode node = this.configLoader.load(ConfigurationOptions.defaults().setObjectMapperFactory(guiceObjectMapperFactory));
        try {
            SpongeScriptConfig config = node.getValue(TypeToken.of(SpongeScriptConfig.class), new SpongeScriptConfig());
            this.configLoader.save(node);
            this.config = config;
        } catch (ObjectMappingException e) {
            throw new IOException(e);
        }

        final Path configDirPath = configDir.toPath();
        List<DiscoveryRoot> roots = config.getScriptDiscoveryPaths().stream()
                .map(glob -> new GlobDiscoveryRoot(configDirPath, glob)).collect(Collectors.toList());
        final ScriptManager manager = new ScriptManager(roots);
        manager.addBindingFactory(() -> new SpongeBinding(this));
        manager.loadAll();
        ScriptManager oldManager = this.manager.getAndSet(manager);
        if (oldManager != null) {
            oldManager.unloadAll();
        }

    }

    @Subscribe
    public void disable(ServerStoppingEvent event) {
        ScriptManager manager = this.manager.getAndSet(null);
        if (manager != null) {
            manager.unloadAll();
        }
    }

    public Game getGame() {
        return game;
    }
}
