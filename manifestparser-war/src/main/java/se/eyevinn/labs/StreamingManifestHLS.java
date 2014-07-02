/**
 * Copyright (c) 2014 Eyevinn
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
    private class MediaSegment {
        public Float duration;
        public String title;
        public String medialink;
    }
    private List<MediaSegment> playlist = new ArrayList<>();

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

    private void initiateTags() {
        validTags.put("#EXTM3U", "EXTM3U");
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

    private MediaSegment parseEXTINF(String line) {
        MediaSegment segment = new MediaSegment();
        Pattern p = Pattern.compile("(\\d+),(\\S+)");
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
            MediaSegment segment = null;
            while ((line = reader.readLine()) != null) {
                String tag = tagFromLine(line);
                if (tag != null) {
                    if (!expectMediaSegment) {
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
                    } else {
                        segment.medialink = line;
                        playlist.add(segment);
                        expectMediaSegment = false;
                        segment = null;
                    }
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
