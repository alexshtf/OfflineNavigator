package com.alexshtf.offlinenavigator;

public class MapInfo {
    private final String name;
    private final String imageUri;

    public MapInfo(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }
}
