package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        File file = new File(blob.name);
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();


        IOUtils.copy(blob.inputStream, new FileOutputStream(file));
        /*try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(new byte[blob.inputStream.available()]);
        }*/
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File coverFile = new File(name);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            try {
                coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
            } catch (URISyntaxException e) {
                throw new IOException("Failed to read file");
            }
        }
        byte[] imageBytes = readAllBytes(coverFilePath);
        String contentType = new Tika().detect(coverFilePath);
        InputStream is = new ByteArrayInputStream(imageBytes);
        return Optional.of(new Blob(name, is, contentType));
    }

    @Override
    public void deleteAll() {
        // ...
    }
}
