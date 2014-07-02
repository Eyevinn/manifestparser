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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamingManifestHLS implements StreamingManifest {
    private InputStream inputStream = null;
    private boolean validFile = false;
    private Map<String, String> validTags = new HashMap<>();
    private class HLSMediaSegment {
        public Float duration;
        public String title;
        public String mediauri;
    }
    private List<HLSMediaSegment> playlistHLS = new ArrayList<>();

    public StreamingManifestHLS(String manifestFileName) throws StreamingManifestException {
        try {
            inputStream = new FileInputStream(manifestFileName);
            initiateTags();
        } catch (FileNotFoundException e) {
            throw new StreamingManifestException("HLS file " + manifestFileName + " not found: " + e.getMessage());
        }
    }

    public void parse() throws StreamingManifestException {
        if (inputStream != null) {
            try {
                parseUTF8(new BufferedReader(new InputStreamReader(inputStream, "UTF-8")));
            } catch(UnsupportedEncodingException e) {
                throw new StreamingManifestException("Unsupported encoding: " + e.getMessage());
            }
        } else {
            throw new StreamingManifestException("Failed to read HLS file: Not initialized");
        }
    }

    public String getManifestType() {
        return StreamingManifestType.HLS;
    }

    public List<ManifestMediaSegment> getMediaSegments() throws StreamingManifestException {
        List<ManifestMediaSegment> segments = new ArrayList<>();
        for (HLSMediaSegment hlsMediaSegment : playlistHLS) {
            ManifestMediaSegment segment = new ManifestMediaSegment(hlsMediaSegment.mediauri, hlsMediaSegment.duration);
            segments.add(segment);
        }
        return segments;
    }

    private void initiateTags() {
        validTags.put("#EXTM3U", "EXTM3U");
        validTags.put("#EXTINF", "EXTINF");
    }

    private String tagFromLine(String line) {
        if (line.equals("#EXTM3U")) {
            return validTags.get(line);
        }
        Pattern p = Pattern.compile("^(#\\w+):(.*)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            String tag = m.group(1);
            return validTags.get(tag);
        }
        return null;
    }

    private HLSMediaSegment parseEXTINF(String line) {
        HLSMediaSegment segment = new HLSMediaSegment();
        Pattern p = Pattern.compile("#EXTINF:(.*),(.*)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            segment.duration = new Float(m.group(1));
            segment.title = m.group(2);
            return segment;
        }
        return null;
    }

    private void parseUTF8(BufferedReader reader) throws StreamingManifestException {
        try {
            String line = null;
            boolean expectMediaSegment = false;
            int linenumber = 0;
            HLSMediaSegment segment = null;
            while ((line = reader.readLine()) != null) {
                if (!expectMediaSegment) {
                    String tag = tagFromLine(line);
                    if (tag != null) {
                        switch (tag) {
                            case "EXTM3U":
                                if (linenumber < 1) {
                                    validFile = true;
                                }
                                break;
                            case "EXTINF":
                                expectMediaSegment = true;
                                segment = parseEXTINF(line);
                                break;
                        }
                    }
                } else {
                    segment.mediauri = line;
                    playlistHLS.add(segment);
                    expectMediaSegment = false;
                    segment = null;
                }
                linenumber++;

            }
            if (!validFile) {
                throw new StreamingManifestException("Not a valid HLS manifest. No #EXTM3U tag found");
            }
        } catch (IOException e) {
            throw new StreamingManifestException("Error reading HLS manifest: " + e.getMessage());
        }
    }
}
