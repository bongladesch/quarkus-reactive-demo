package com.bongladesch.service;

import java.io.InputStream;

public record FileDataDTO(String name, String mimeType, InputStream fileStream) {

}
