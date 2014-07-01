/**
 * Copyright (c) 2014 Eyevinn
 */
package se.eyevinn.labs;

import java.io.*;

public class StreamingManifestHLS implements StreamingManifest {
    private File manifestFile = null;
    private boolean foundTag = false;

    // TODO handle URN
    public StreamingManifestHLS(String manifestFileName) throws StreamingManifestException {
        manifestFile = new File(manifestFileName);
        if (!manifestFile.exists()) {
            throw new StreamingManifestException("HLS file " + manifestFileName + " not found");
        }
    }

    public void parse() throws StreamingManifestException {
        if (manifestFile != null) {
            parseLocalFile();
        }
    }

    public String getManifestType() {
        return StreamingManifestType.HLS;
    }

    private void parseLocalFile() throws StreamingManifestException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(manifestFile));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("#EXTM3U")) {
                    foundTag = true;
                }
            }
            if (!foundTag) {
                throw new StreamingManifestException("Not a valid HLS manifest. No #EXTM3U tag found");
            }
        } catch (FileNotFoundException e) {
            throw new StreamingManifestException("HLS manifest " + manifestFile.getName() + " not found");
        } catch (IOException e) {
            throw new StreamingManifestException("Error reading HLS manifest: " + e.getMessage());
        }
    }
}
