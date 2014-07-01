package se.eyevinn.labs;

/**
 * Created by deejaybee on 6/28/14.
 */
public interface StreamingManifest {
    public void parse() throws StreamingManifestException;
    public String getManifestType();
}
