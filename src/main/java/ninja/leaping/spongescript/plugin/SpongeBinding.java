package ninja.leaping.spongescript.plugin;

import com.google.common.base.Optional;
import ninja.leaping.spongescript.Binding;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventHandler;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandMapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.script.Bindings;

/**
 * Add Sponge-specific methods to the binding
 */
public class SpongeBinding implements Binding {
    private final SpongeScriptPlugin plugin;

    private final Set<EventHandler<?>> registeredEvents = new HashSet<>();
    private final Set<CommandMapping> registeredCommands = new HashSet<>();
    private Map<String, Object> exports = new HashMap<>();

    public SpongeBinding(SpongeScriptPlugin plugin) {
        this.plugin = plugin;
    }

    public <T extends Event> void onEvent(final Class<T> eventClass, final Consumer<T> eventListener) {
        final EventHandler<T> wrappedHandler = eventListener::accept;
        plugin.getGame().getEventManager().register(plugin, eventClass, wrappedHandler);
        registeredEvents.add(wrappedHandler);
    }

    public void onCommand(CommandCallable callable, String... aliases) {
        Optional<CommandMapping> ret = plugin.getGame().getCommandDispatcher().register(plugin, callable, aliases);
        if (!ret.isPresent()) {
            throw new IllegalArgumentException("Invalid alias provided");
        }
        registeredCommands.add(ret.get());
    }

    public Map<String, Object> getExports() {
        return this.exports;
    }

    // TODO: Scheduler tasks

    @Override
    public void close() throws Exception {
        for (CommandMapping mapping : registeredCommands) {
            plugin.getGame().getCommandDispatcher().removeMapping(mapping);
        }

        for (EventHandler<?> handler : registeredEvents) {
            plugin.getGame().getEventManager().unregister(handler);
        }
    }

    @Override
    public void accept(Bindings bindings) {
        bindings.put("game", plugin.getGame());
        bindings.put("ctx", this);
    }
}
