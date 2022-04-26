package gui;

import java.util.stream.Stream;

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
        return Stream.of(values())
                .filter(n -> n.name.equals(frameNameToGet))
                .findFirst().get();
    }
}
