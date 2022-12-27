package com.vicom.backend.repository;

import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

@Repository
public class ImageFileRepository {
    final String RESOURCE_DIR = Objects.requireNonNull(ImageFileRepository.class.getResource("/"))
            .getPath().substring(1) + "static/";
    public String saveImage(byte[] image, String imageName) throws IOException {
        Path newFile = Paths.get(RESOURCE_DIR + "image/" + new Date().getTime() + "-" + imageName);
        Files.createDirectories(newFile.getParent());
        Files.write(newFile, image);
        return "image/" + newFile.getFileName().toString();
    }

}
