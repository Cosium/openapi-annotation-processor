package com.cosium.openapi.annotation_processor.standard;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 13/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class PathUtils {

    /**
     * @param path The path to scan
     * @return The direct subdiretories of the provided path
     */
    public static List<Path> subDirectories(Path path) {
        List<Path> directories = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path subPath : directoryStream) {
                if (!Files.isDirectory(subPath)) {
                    continue;
                }
                directories.add(subPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return directories;
    }

    /**
     * @param path The patch to scan
     * @return All direct and indirect sub files
     */
    public static List<Path> subFiles(Path path) {
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path subPath : directoryStream) {
                if (Files.isDirectory(subPath)) {
                    files.addAll(subFiles(subPath));
                } else {
                    files.add(subPath);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return files;
    }

}
