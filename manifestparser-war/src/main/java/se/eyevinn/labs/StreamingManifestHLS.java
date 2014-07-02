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
    private class HLSMediaSegment {
        public Float duration;
        public String title;
        public String mediauri;
        public int byteRangeLength = 0;
        public int byteRangeOffset = 0;
    }
    private List<HLSMediaSegment> playlistHLS = new ArrayList<>();
    private String manifestVersion;
    private int targetDuration = 0;
    private boolean isMasterPlaylist = true;

    public StreamingManifestHLS(String manifestFileName) throws StreamingManifestException {
        try {
            inputStream = new FileInputStream(manifestFileName);
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

    public int getManifestDuration() {
        return targetDuration;
    }

    public List<ManifestMediaSegment> getMediaSegments() throws StreamingManifestException {
        List<ManifestMediaSegment> segments = new ArrayList<>();
        for (HLSMediaSegment hlsMediaSegment : playlistHLS) {
            ManifestMediaSegment segment = new ManifestMediaSegment(hlsMediaSegment.mediauri);
            if (hlsMediaSegment.duration != null) {
                segment.setDuration(hlsMediaSegment.duration);
            }
            if (hlsMediaSegment.byteRangeLength != 0) {
                segment.setByteRange(hlsMediaSegment.byteRangeOffset, hlsMediaSegment.byteRangeLength);
            }

            segments.add(segment);
        }
        return segments;
    }

    private String tagFromLine(String line) {
        if (line.equals("#EXTM3U")) {
            return "EXTM3U";
        }
        Pattern p = Pattern.compile("^(#.*?):(.*)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            return m.group(1).substring(1);
        }
        return null;
    }

    private void parseEXTINF(HLSMediaSegment segment, String line) {
        Pattern p = Pattern.compile("#EXTINF:(.*),(.*)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            segment.duration = new Float(m.group(1));
            segment.title = m.group(2);
        }
    }

    private void parseEXTByteRange(HLSMediaSegment segment, String line) {
        Pattern p = Pattern.compile("#EXT-X-BYTERANGE:(\\d+)(@\\d+)?");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            segment.byteRangeLength = new Integer(m.group(1));
            if (m.group(2) != null) {
                segment.byteRangeOffset = new Integer(m.group(2).substring(1));
            }
        }

    }

    private void parseTargetDuration(String line) {
        Pattern p = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            targetDuration = new Integer(m.group(1));
        }
    }

    private void parseVersion(String line) {
        Pattern p = Pattern.compile("#EXT-X-VERSION:(\\d+)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            manifestVersion = m.group(1);
        }
    }

    private void parseUTF8(BufferedReader reader) throws StreamingManifestException {
        try {
            String line = null;
            int linenumber = 0;
            HLSMediaSegment segment = new HLSMediaSegment();
            while ((line = reader.readLine()) != null) {
                String tag = tagFromLine(line);
                if (tag != null) {
                    switch (tag) {
                        case "EXTM3U":
                            if (linenumber < 1) {
                                validFile = true;
                            }
                            break;
                        case "EXTINF":
                            parseEXTINF(segment, line);
                            break;
                        case "EXT-X-BYTERANGE":
                            manifestVersion = "4";
                            isMasterPlaylist = false;
                            parseEXTByteRange(segment, line);
                            break;
                        case "EXT-X-TARGETDURATION":
                            parseTargetDuration(line);
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-MEDIA-SEQUENCE":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-PROGRAM-DATE-TIME":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-PLAYLIST-TYPE":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-ENDLIST":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-MEDIA":
                            break;
                        case "EXT-X-STREAM-INF":
                            break;
                        case "EXT-X-DISCONTINUITY":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-DISCONTINUITY-SEQUENCE":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-I-FRAMES-ONLY":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-MAP":
                            isMasterPlaylist = false;
                            break;
                        case "EXT-X-I-FRAME-STREAM-INF":
                            break;
                        case "EXT-X-INDEPENDENT-SEGMENTS":
                            break;
                        case "EXT-X-START":
                            break;
                        case "EXT-X-KEY":
                            break;
                        case "EXT-X-VERSION":
                            parseVersion(line);
                            break;
                        default:
                            throw new StreamingManifestException("Unknown tag found in HLS manifest: " + tag);
                    }
                } else if (!line.isEmpty() && !line.startsWith("#")) {
                    segment.mediauri = line;
                    playlistHLS.add(segment);
                    segment = new HLSMediaSegment();
                }
                linenumber++;
            }
            if (!validFile) {
                throw new StreamingManifestException("No #EXTM3U tag found");
            }
            if (!isMasterPlaylist && targetDuration == 0) {
                throw new StreamingManifestException("No #EXT-X-TARGETDURATION and not a master playlist");
            }
        } catch (IOException e) {
            throw new StreamingManifestException("Error reading HLS manifest: " + e.getMessage());
        }
    }
}
