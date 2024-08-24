package uwu.narumi.deobfuscator.api.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import uwu.narumi.deobfuscator.api.helper.ClassHelper;
import uwu.narumi.deobfuscator.api.helper.FileHelper;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Library {

  private static final Logger LOGGER = LogManager.getLogger(Library.class);

  private final Map<String, byte[]> files = new ConcurrentHashMap<>();
  private final Map<String, byte[]> classFiles = new ConcurrentHashMap<>();
  private final Path path;

  public Library(Path path, int classWriterFlags) {
    this.path = path;
    FileHelper.loadFilesFromZip(
        path,
        (name, bytes) -> {
          if (!ClassHelper.isClass(name, bytes)) {
            files.putIfAbsent(name, bytes);
            return;
          }

          try {
            classFiles.putIfAbsent(
                ClassHelper.loadClass(
                        name,
                        bytes,
                        ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG,
                        classWriterFlags)
                    .name(),
                bytes);
          } catch (Exception e) {
            LOGGER.error("Could not load {} class from {} library", name, path, e);
          }
        });

    LOGGER.info("Loaded {} classes from {}", classFiles.size(), path.getFileName());
  }

  public Map<String, byte[]> getFiles() {
    return files;
  }

  public Map<String, byte[]> getClassFiles() {
    return classFiles;
  }

  public Path getPath() {
    return path;
  }
}
