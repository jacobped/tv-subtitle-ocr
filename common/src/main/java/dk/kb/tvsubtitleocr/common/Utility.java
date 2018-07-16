package dk.kb.tvsubtitleocr.common;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class Utility {
    /**
     * Checks if the input is null or a Path and returns the equivalent.
     * Ths way you avoid the InvalidPathException if path is null.
     * @param path the input path to check.
     * @return null if input is null. Else its Path object instance.
     */
    public static Path stringAsNullOrPath(String path) {
        Path result = null;
        if(path != null) result = Paths.get(path);
        return result;
    }

    /**
     * Checks if the input is null or a file and returns the equivalent.
     * Ths way you avoid the NullPointerException if path is null.
     * @param path the input path to check.
     * @return null if input is null. Else its File object instance.
     */
    public static File stringAsNullOrFile(String path) {
        File result = null;
        if(path != null) result = new File(path);
        return result;
    }

    public static File getFileFromVideoUUID(UUID video, File videoSourceDir, String videoFileExtension) throws IOException {

        CharSequence dirSequence = video.toString().subSequence(0, 4);
        String dir1 = String.valueOf(dirSequence.charAt(0));
        String dir2 = String.valueOf(dirSequence.charAt(1));
        String dir3 = String.valueOf(dirSequence.charAt(2));
        String dir4 = String.valueOf(dirSequence.charAt(3));
        Path videoPath = Paths.get(videoSourceDir.getAbsolutePath(), dir1, dir2, dir3, dir4,
                String.join(".", video.toString(), videoFileExtension)
        );

        if(Files.notExists(videoPath)) {
            throw new IOException("No video file at the expected location: " + videoPath.toString());
        }

        return videoPath.toFile();
    }
}
