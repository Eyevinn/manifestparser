/**
 * Copyright (c) 2014 jonas.birme@eyevinn.se
 *
 * This file is part of Manifest Parser.
 *
 * Manifest Parser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Manifest Parser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Manifest Parser.  If not, see <http://www.gnu.org/licenses/>.
 *
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
        hlsTestFiles.add("src/test/resources/simple.m3u8");
        hlsTestFiles.add("src/test/resources/wowza.m3u8");
        hlsTestFiles.add("src/test/resources/encrypted.m3u8");
        hlsTestFiles.add("src/test/resources/master.m3u8");
        hlsTestFiles.add("src/test/resources/byterange.m3u8");
    }

    @Test
    public void parseHLSFile() {
        System.out.println("Testing to parse HLS files: ");
        for(String hlsTestFile : hlsTestFiles) {
            try {
                StreamingManifest manifest = StreamingManifestFactory.createInstance(hlsTestFile);
                Assert.assertEquals(manifest.getManifestType(), StreamingManifestType.HLS);
                manifest.parse();
                int i = 1;
                System.out.println(" * " + hlsTestFile + ":");
                if (manifest.getManifestDuration() > 0) {
                    System.out.println("   Duration: " + manifest.getManifestDuration() + "s");
                }
                for(ManifestMediaSegment seg : manifest.getMediaSegments()) {
                    System.out.println("   - " + i + ": " + seg);
                    i++;
                }
            } catch (StreamingManifestException e) {
                fail("Failed parsing test file: " + hlsTestFile + ": " + e.getMessage());
            }
        }
        System.out.println("Managed to parse all HLS files");
    }
}
