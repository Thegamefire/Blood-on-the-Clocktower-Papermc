package com.thegamefire.bloodOnTheClocktower.characters;

import net.kyori.adventure.text.format.NamedTextColor;

public enum BotcCharacter {

    VIRGIN("Virgin", "The 1st time you are nominated, if the nominator is a Townsfolk, they are executed immediately.", null, Type.TOWNSFOLK);

    public enum Type {
        TOWNSFOLK(NamedTextColor.BLUE),
        OUTSIDER(NamedTextColor.DARK_BLUE),
        MINION(NamedTextColor.RED),
        DEMON(NamedTextColor.DARK_RED);

        private NamedTextColor color;
        Type(NamedTextColor color) {
            this.color = color;
        }
        public NamedTextColor getColor() {
            return color;
        }
    }
    private final String name;
    private final String description;
    private final String footnote;
    private final Type type;

    BotcCharacter(String name, String description, String footnote, Type type) {
        this.name = name;
        this.description = description;
        this.footnote = footnote!=null?footnote:"";
        this.type = type;
    }

    public boolean isGood() {
        return type == Type.TOWNSFOLK || type == Type.OUTSIDER;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description+" | "+footnote;
    }

    public Type getType() {
        return type;
    }
}
