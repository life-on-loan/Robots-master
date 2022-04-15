package gui;

/**
 * Типы окон
 */
public enum FrameNames {
    LOG_WINDOW("log"),
    GAME_WINDOW("game");

    private final String name;

    FrameNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Метод приводящий строку к типу FrameName
     * @param frameNameToGet - имя окна типа String
     * @return имя окна типа FrameName
     */
    public static FrameNames getTypeByFrameName(String frameNameToGet) {
        for (FrameNames frameName : values()) {
            if (frameName.name.equals(frameNameToGet)) {
                return frameName;
            }
        }
        throw new RuntimeException(String.format("Не найдено окно с именем: %s", frameNameToGet));
    }
}
