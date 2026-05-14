package io.github.parkkevinsb.flower.sample.battle.domain;

public enum ItemRarity {
    COMMON("Common", "common", "#f4f1e8"),
    MAGIC("Magic", "magic", "#5aa7ff"),
    RARE("Rare", "rare", "#ffd34d"),
    UNIQUE("Unique", "unique", "#c95b1d");

    private final String displayName;
    private final String cssClass;
    private final String color;

    ItemRarity(String displayName, String cssClass, String color) {
        this.displayName = displayName;
        this.cssClass = cssClass;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getColor() {
        return color;
    }
}
