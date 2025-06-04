package com.eventhub.AuthMicroService.tools;

public enum PathTool {
    INIT_AUTH("/auth"),
    REGISTRATION("%s/sign-up".formatted(PathTool.INIT_AUTH)),
    LOGIN("%s/sign-in".formatted(PathTool.INIT_AUTH)),
    REFRESH("%s/refresh".formatted(PathTool.INIT_AUTH)),
    MAIN("%s/main".formatted(PathTool.INIT_AUTH));

    private final String path;
    PathTool(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path;
    }
}
