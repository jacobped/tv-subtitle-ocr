package dk.kb.tvsubtitleocr.videoProcessor;

import dk.kb.tvsubtitleocr.videoProcessor.common.PropertiesFactory;
import dk.kb.tvsubtitleocr.videoProcessor.common.RuntimeProperties;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class is not for actual unit testing, but for development with experimentation and debugging.
 * So instead of spawning a billion main args all over the place. You can experiment with this as your starting point.
 */
public class MainTest {
    final static Logger log = LoggerFactory.getLogger(MainTest.class);
    protected final String uuidListFileName = "uuidList.txt";

    @Test
    @Disabled("For manual debugging only.")
    public void runVideoProcessor() throws IOException {

        log.info("Starting.");
        log.info("Setting up program.");
        RuntimeProperties properties = PropertiesFactory.getProperties();
        VideoProcessor videoProcessor = new VideoProcessor(properties);

        log.info("Running program.");
        File video = new File("/home/jacob/andet/DR Video Streams/drRecords/dr1-1.mp4");
        Path workDir = Paths.get(properties.getProperty(RuntimeProperties.ResourceName.sharedWorkDir));
        Path srtPath = Paths.get(workDir.toString(), "/subtitles.srt");

        try {
            Files.deleteIfExists(srtPath);
        } catch (IOException e) {
            throw new RuntimeException("Something happened when deleting the SRT file. FilePath: " + srtPath, e);
        }

        videoProcessor.processVideo(video, srtPath);

        log.info("The End.");
    }

    @SuppressWarnings("Duplicates")
    @Test
    @Disabled("For manual debugging only.")
    void createWorkDir() throws IOException {
        final String configFileName = "config.properties";
        Properties prop = new Properties();
        try(InputStream propFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName)) {
            prop.load(new InputStreamReader(propFile));
        }
        String workDirString = prop.getProperty(RuntimeProperties.ResourceName.sharedWorkDir.toString());
        Path workDirPath = Paths.get(workDirString);

        Files.createDirectories(workDirPath);
    }
}
