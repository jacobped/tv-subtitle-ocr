package dk.kb.tvsubtitleocr.videoProcessor.ocrprocessing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface IOCRProcessor{
    List<String> ocrImage(BufferedImage image) throws IOException;
}
