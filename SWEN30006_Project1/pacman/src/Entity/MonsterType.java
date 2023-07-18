package src.Entity;

/**
 * Type of Monster. Only really exists to maintain functionality of GameCallBack class
 */
public enum MonsterType {
    Troll,
    TX5,
    Alien,
    Orion,
    Wizard;

    public String getImageName() {
        switch (this) {
            case Troll -> {
                return "sprites/m_troll.gif";
            }
            case TX5 -> {
                return "sprites/m_tx5.gif";
            }
            case Alien -> {
                return "sprites/m_alien.gif";
            }
            case Orion -> {
                return "sprites/m_orion.gif";
            }
            case Wizard -> {
                return "sprites/m_wizard.gif";
            }
            default -> {
                assert false;
            }
        }
        return null;
    }
}
