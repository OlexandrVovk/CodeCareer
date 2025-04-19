package com.my.jobsearcher.store.enums;

import java.util.HashMap;
import java.util.Map;

public enum Language {
    PYTHON("Python"),
    C_PLUS_PLUS("C++"),
    RUBY("Ruby"),
    JAVA("Java"),
    KOTLIN("Kotlin"),
    PHP("PHP"),
    JAVASCRIPT("JavaScript"),
    SWIFT("Swift"),
    DOT_NET(".NET");

    private final String displayName;
    private static final Map<String, Language> NAME_MAP = new HashMap<>();
    private static final Map<String, Language> DISPLAY_NAME_MAP = new HashMap<>();

    static {
        for (Language language : values()) {
            NAME_MAP.put(language.name().toUpperCase(), language);
            DISPLAY_NAME_MAP.put(language.displayName.toUpperCase(), language);
        }
    }

    Language(String displayName) {
        this.displayName = displayName;
    }

    public static Language fromString(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Language string is null");
        }
        String key = name.trim().toUpperCase();
        Language language = NAME_MAP.get(key);
        if (language == null) {
            language = DISPLAY_NAME_MAP.get(key);
        }
        if (language == null) {
            throw new IllegalArgumentException("No enum constant for Language: " + name);
        }
        return language;
    }

    @Override
    public String toString() {
        return displayName;
    }
}