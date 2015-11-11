package ninja.leaping.spongescript.languages;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

/**
 * Created by zml on 8/15/15.
 */
public class NashornLanguage extends JSR223Language {

    public NashornLanguage() {
        super(MANAGER.getEngineByName("nashorn").getFactory());
    }

    @Override
    protected ScriptEngine getScriptEngine() {
        return ((NashornScriptEngineFactory) factory).getScriptEngine(clazz -> !BLACKLISTED_CLASSES.contains(clazz));
    }
}
