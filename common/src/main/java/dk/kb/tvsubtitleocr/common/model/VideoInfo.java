package dk.kb.tvsubtitleocr.common.model;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class VideoInfo implements Serializable {
    private UUID uuid;
    private String title;
    private ZonedDateTime startTime;

    public VideoInfo(){}

    public VideoInfo(UUID uuid, String title, ZonedDateTime startTime) {
        this.uuid = uuid;
        this.title = title;
        this.startTime = startTime;
    }

    public VideoInfo(String uuid, String title, String startTime) {

        String uuidSubString = uuid.substring(uuid.lastIndexOf(":") + 1);
        this.uuid = UUID.fromString(uuidSubString);
        this.title = title;
        this.startTime = parseDateTime(startTime);
    }

    protected ZonedDateTime parseDateTime(String dateTime) {
        final String format = "yyyy-MM-dd'T'HH:mm:ssZ";

        ZonedDateTime result = ZonedDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
        return result;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public String toString() {
        return "Info: {" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
