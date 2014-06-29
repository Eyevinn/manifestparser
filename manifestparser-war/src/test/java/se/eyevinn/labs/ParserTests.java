package se.eyevinn.labs;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deejaybee on 6/28/14.
 */
public class ParserTests {
    private List<String> hlsTestFiles = new ArrayList<>();

    public ParserTests() {
        hlsTestFiles.add("resources/bipbop_4x3_variant.m3u8");
    }

    @Test
    public void parseHLSFile() {
        for(String hlsTestFile : hlsTestFiles) {
            StreamingManifest manifest = StreamingManifestFactory.createInstance(hlsTestFile);
            assertEquals(manifest.getManifestType(), StreamingManifestType.HLS);
        }
    }
}
