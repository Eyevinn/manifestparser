/**
 * Copyright (c) 2014 Eyevinn
 */
package se.eyevinn.labs;

public class StreamingManifestFactory {
    public static StreamingManifest createInstance(String manifestFile) throws StreamingManifestException {
        String type = StreamingManifestType.fromFilename(manifestFile);
        if (type.equals(StreamingManifestType.HLS)) {
            return new StreamingManifestHLS(manifestFile);
        }
        return null;
    }
}
