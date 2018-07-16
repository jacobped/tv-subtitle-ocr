package dk.kb.tvsubtitleocr.common;

import java.io.File;

public class MachineLearningProperties {
    private File modelfile, labelfile;

    public MachineLearningProperties(File modelfile, File labelfile) {
        this.modelfile = modelfile;
        this.labelfile = labelfile;
    }

    public File getModelfile() {
        return modelfile;
    }

    public File getLabelfile() {
        return labelfile;
    }
}
