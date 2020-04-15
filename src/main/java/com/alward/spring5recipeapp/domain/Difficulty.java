package com.alward.spring5recipeapp.domain;

public enum Difficulty {
    EASY("Easy"), MODERATE("Moderate"), KIND_OF_HARD("Kind of hard"), HARD("Hard");

    private final String displayName;

    Difficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
