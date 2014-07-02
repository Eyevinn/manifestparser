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

public class ManifestMediaSegment {
    private String mediaURI;
    private float mediaDuration;
    private class ByteRange {
        private int offset;
        private int length;
        public ByteRange(int offset, int length) { this.offset = offset; this.length = length; }
        public int getOffset() { return this.offset; }
        public int getLength() { return this.length; }
        public String toString() { return "["+offset+":"+(offset+length)+"]"; }
    }
    private ByteRange range;

    public ManifestMediaSegment(String mediaURI) {
        this.mediaURI = mediaURI;
    }

    public ManifestMediaSegment(String mediaURI, float mediaDuration) {
        this.mediaURI = mediaURI;
        this.mediaDuration = mediaDuration;
    }

    public void setDuration(float mediaDuration) { this.mediaDuration = mediaDuration; }
    public float getDuration() { return this.mediaDuration; }

    public void setByteRange(int offset, int length) { this.range = new ByteRange(offset, length); }
    public boolean hasByteRange() {
        if (this.range != null) {
            return true;
        }
        return false;
    }
    public int getMediaSegmentStart() { return range.getOffset(); }
    public int getMediaSegmentEnd() { return range.getOffset() + range.getLength(); }

    public String toString() {
        String sRange = "";
        if (range != null) {
            sRange = range.toString();
        }
        return mediaURI + " (" + mediaDuration + ") " + sRange;
    }
}
