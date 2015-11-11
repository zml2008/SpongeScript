package ninja.leaping.spongescript;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.spongescript.resources.VerificationLevel;

import java.util.List;

/**
 * Configuration for SpongeScript
 */
@ConfigSerializable
public class SpongeScriptConfig {
    @Setting(comment = "Paths to search in to discover scripts")
    private List<String> discoveryPaths = Lists.newArrayList("bundles/*/__main__.*", "scripts/*");
    @Setting(comment = "Level to verify script integrity at")
    private VerificationLevel verificationLevel = VerificationLevel.LOOSE;

    public List<String> getScriptDiscoveryPaths() {
        return discoveryPaths;
    }

    public VerificationLevel getVerificationLevel() {
        return this.verificationLevel;
    }

}
