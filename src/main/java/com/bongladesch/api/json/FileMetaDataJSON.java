package com.bongladesch.api.json;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record FileMetaDataJSON(String name, String mimeType) {

}
