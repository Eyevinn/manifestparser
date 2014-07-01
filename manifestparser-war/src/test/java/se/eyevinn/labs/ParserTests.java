/**
 * Copyright (c) 2014 Eyevinn
 */
package se.eyevinn.labs;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParserTests {
    private List<String> hlsTestFiles = new ArrayList<>();

    public ParserTests() {
        hlsTestFiles.add("src/test/resources/bipbop_4x3_variant.m3u8");
    }

    @Test
    public void parseHLSFile() {
        System.out.println("Testing to parse HLS files");
        for(String hlsTestFile : hlsTestFiles) {
            try {
                StreamingManifest manifest = StreamingManifestFactory.createInstance(hlsTestFile);
                Assert.assertEquals(manifest.getManifestType(), StreamingManifestType.HLS);
                manifest.parse();
            } catch (StreamingManifestException e) {
                fail("Failed parsing test file: " + hlsTestFile + ": " + e.getMessage());
            }
        }
    }
}
