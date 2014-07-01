/**
 * Copyright (c) 2014 Eyevinn
 */
package se.eyevinn.labs;

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
