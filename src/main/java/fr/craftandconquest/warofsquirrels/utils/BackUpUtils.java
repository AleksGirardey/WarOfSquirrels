package fr.craftandconquest.warofsquirrels.utils;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Objects;

public class BackUpUtils {
    private static void CopyDirectory(File source, File destination) {
        if (source == null || destination == null) return;

        if (!destination.exists())
            if (!destination.mkdir())
                WarOfSquirrels.LOGGER.error("Couldn't create BackUp file : " + destination.getName());

        for (String child : Objects.requireNonNull(source.list())) {
            Copy(new File(source, child), new File(destination, child));
        }
    }

    private static void CopyFile(File source, File destination) throws IOException {
        try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destination)) {
            byte[] buf = new byte[1024];
            int length;

            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        }
    }

    @SneakyThrows
    private static void Copy(File source, File destination) {
        if (source.isDirectory())
            CopyDirectory(source, destination);
        else
            CopyFile(source, destination);
    }
}
