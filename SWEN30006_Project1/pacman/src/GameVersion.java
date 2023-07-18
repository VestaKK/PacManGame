package src;

/**
 * Describes the version of the game being played;
 */
public enum GameVersion {
    SIMPLE,
    MULTIVERSE;
    public String toString() {
        return this.name().toLowerCase();
    }
}
