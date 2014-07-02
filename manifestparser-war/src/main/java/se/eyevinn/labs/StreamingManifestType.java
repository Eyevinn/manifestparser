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

public class StreamingManifestType {
    public static String HLS = "HLS";
    public static String UNKNOWN = "UNKNOWN";

    public static String fromFilename(String filename) {
        if (filename.endsWith(".m3u8")) {
            return HLS;
        }
        return UNKNOWN;
    }
}
