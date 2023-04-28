package com.bongladesch.api.json;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ErrorJSON(String message) {

}
