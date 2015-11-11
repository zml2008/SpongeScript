package ninja.leaping.spongescript.resources;

/**
 * Level of verification that will be performed on a resource
 */
public enum VerificationLevel {
    /**
     * No verification of remote sources will be performed
     */
    NONE,
    /**
     * Require any external resources, if signed, to maintain the same signature
     */
    LOOSE,

    /**
     * Only allow resources with one of the approved signatures or a fixed sha256 hash
     */
    STRICT;
}
