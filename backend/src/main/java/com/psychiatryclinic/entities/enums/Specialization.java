package com.psychiatryclinic.entities.enums;

public enum Specialization {
    PSIKIYATRIST("Psikiyatrist"),
    PSIKOLOG("Psikolog"),
    UZMAN_PSIKOLOG("Uzman Psikolog"),
    KLINIK_PSIKOLOG("Klinik Psikolog");

    private final String displayName;

    Specialization(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 