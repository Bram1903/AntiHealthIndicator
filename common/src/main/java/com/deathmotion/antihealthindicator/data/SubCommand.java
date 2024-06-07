package com.deathmotion.antihealthindicator.data;

import lombok.Getter;

@Getter
public class SubCommand {
    private final String name;
    private final String description;

    protected SubCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
