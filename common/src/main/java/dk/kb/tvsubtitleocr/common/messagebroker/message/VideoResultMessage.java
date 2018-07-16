package dk.kb.tvsubtitleocr.common.messagebroker.message;

import dk.kb.tvsubtitleocr.common.model.VideoInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob on 2018-03-24 (YYYY-MM-DD).
 */
public class VideoResultMessage implements Serializable {
    private VideoInfo videoInfo;
    private List<String> srtContent;
    private List<Exception> errors = new ArrayList<>();

    public VideoResultMessage() {}

    public VideoResultMessage(VideoInfo videoInfo, List<String> srtContent) {
        this.videoInfo = videoInfo;
        this.srtContent = srtContent;
    }

    public boolean anyErrors() {
        return errors != null && errors.size() > 0;
    }

    public void addError(Exception e) {
        errors.add(e);
    }

    public List<String> getSrtContent() {
        return srtContent;
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public void setSrtContent(List<String> srtContent) {
        this.srtContent = srtContent;
    }

    public void setErrors(List<Exception> errors) {
        this.errors = errors;
    }
}
