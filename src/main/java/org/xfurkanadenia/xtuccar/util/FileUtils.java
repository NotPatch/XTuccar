package org.xfurkanadenia.xtuccar.util;

import org.xfurkanadenia.xtuccar.XTuccar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    public static List<String> listResources(String path) {
        List<String> files = new ArrayList<>();
        try (ZipInputStream zip = new ZipInputStream(
                XTuccar.getInstance().getClass().getProtectionDomain().getCodeSource().getLocation().openStream())) {
            ZipEntry e;
            while ((e = zip.getNextEntry()) != null)
                if (e.getName().startsWith(path + "/") && !e.isDirectory())
                    files.add(e.getName().substring(path.length() + 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}
