package se.eyevinn.labs;

/**
 * Created by deejaybee on 6/28/14.
 */
public class StreamingManifestHLS implements StreamingManifest {
    public String getManifestType() {
        return StreamingManifestType.HLS;
    }
}
