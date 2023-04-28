package com.bongladesch.service;

import java.io.InputStream;

public record FileStreamDataDTO(String name, String mimeType, InputStream fileStream) {

}
