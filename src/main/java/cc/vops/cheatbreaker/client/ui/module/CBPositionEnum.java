package cc.vops.cheatbreaker.client.ui.module;

import lombok.Getter;

@Getter
public enum CBPositionEnum {
    BOTTOM("BOTTOM"),
    TOP("TOP"),
    CENTER("CENTER"),
    LEFT("LEFT"),
    RIGHT("RIGHT");

    private final String identifier;

    CBPositionEnum(String identifier) {
        this.identifier = identifier;
    }
}
