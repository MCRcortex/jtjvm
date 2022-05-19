package natives;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ResourceLocator {
    public List<Path> searchPaths = new LinkedList<>();

    public void add(Path path) {
        searchPaths.add(path);
    }

    public Path findClazz(String clazz) {
        String clzFile = clazz.replaceAll("\\.","/")+".class";
        for (Path p : searchPaths) {
            if (Files.exists(p.resolve(clzFile))) {
                return p.resolve(clzFile);
            }
        }
        return null;
    }

    public static ResourceLocator createBootstrapResources() throws IOException {
        ResourceLocator loader = new ResourceLocator();
        try {
            Path p = Paths.get(URI.create("jrt:/")).resolve("/modules");
            Files.list(p).forEach(loader::add);
        } catch(FileSystemNotFoundException ex) {
            System.out.println("Could not read runtime.");
        }
        return loader;
    }
}
