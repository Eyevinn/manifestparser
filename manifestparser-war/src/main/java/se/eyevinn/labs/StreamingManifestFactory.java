package se.eyevinn.labs;

/**
 * Created by deejaybee on 6/28/14.
 */
public class StreamingManifestFactory {
    public static StreamingManifest createInstance(String manifestFile) {
        String type = StreamingManifestType.fromFilename(manifestFile);
        if (type.equals(StreamingManifestType.HLS)) {
            return new StreamingManifestHLS();
        }
        return null;
    }
}
