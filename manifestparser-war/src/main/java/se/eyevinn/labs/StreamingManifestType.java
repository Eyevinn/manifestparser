package se.eyevinn.labs;

/**
 * Created by deejaybee on 6/29/14.
 */
public class StreamingManifestType {
    public static String HLS = "HLS";
    public static String UNKNOWN = "UNKNOWN";

    public static String fromFilename(String filename) {
        if (filename.endsWith(".m3u8")) {
            return HLS;
        }
        return UNKNOWN;
    }
}
