package dk.kb.tvsubtitleocr.videoProcessor.frameextraction;
import java.io.File;
import java.io.IOException;

public interface IFrameExtractionProcessor {
    VideoInformation extractFrames(File input) throws IOException;
}
