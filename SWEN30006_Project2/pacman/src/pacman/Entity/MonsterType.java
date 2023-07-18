package pacman.Entity;

/**
 * Type of Monster. Only really exists to maintain functionality of GameCallBack class
 */
public enum MonsterType {
    Troll,
    TX5;
    public String getImageName() {
        switch (this) {
            case Troll -> {
                return "pacman/sprites/m_troll.gif";
            }
            case TX5 -> {
                return "pacman/sprites/m_tx5.gif";
            }
            default -> {
                assert false;
            }
        }
        return null;
    }
}
