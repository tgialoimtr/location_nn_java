package com.trungp.ocrreport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class FileFolderUtils {

    public static Map<String, String> getFileNames(Map<String, String> fileNames, Path dir) {
        return getFileNames(fileNames, dir, null);
    }

    public static Map<String, String> getFileNames(Map<String, String> fileNames, Path dir, String ext) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getFileNames(fileNames, path, ext);
                } else {

                    if (ext != null) {
                        if (path.getFileName().toString().toLowerCase().endsWith(ext.toLowerCase())) {
                            fileNames.put(path.getFileName().toString(), path.toAbsolutePath().toString());
                        }
                    } else {
                        fileNames.put(path.getFileName().toString(), path.toAbsolutePath().toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }

    public static void copyFile(String srcFile, String destFile) {

        try {
            FileInputStream srcStream = new FileInputStream(srcFile);
            FileOutputStream destStream = new FileOutputStream(destFile);
            FileChannel src = srcStream.getChannel();
            FileChannel dest = destStream.getChannel();
            dest.transferFrom(src, 0, src.size());

            try {
                srcStream.close();
            } catch (Throwable e) {
                System.out.println("e: " + e);
            }
            try {
                destStream.close();
            } catch (Throwable e) {
                System.out.println("e: " + e);
            }
        } catch (Throwable e) {
            System.out.println("Copy file error: " + e);
        }
    }

}
