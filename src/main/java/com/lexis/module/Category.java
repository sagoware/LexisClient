package com.lexis.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    PLAYER("Player");

    private final String name;
    Category(String name) { this.name = name; }
    public String getName() { return name; }
}