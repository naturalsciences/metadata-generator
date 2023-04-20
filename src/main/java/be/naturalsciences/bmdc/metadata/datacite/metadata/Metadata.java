// 
// Decompiled by Procyon v0.5.36
// 

package be.naturalsciences.bmdc.metadata.datacite.metadata;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;

public class Metadata
{
    public static String getMetadataFromFile(final File metadataFile) throws IOException {
        final byte[] fileBytes = readFile(metadataFile);
        return new String(fileBytes);
    }
    
    private static byte[] readFile(final File inputFile) throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final BufferedInputStream bin = new BufferedInputStream(new FileInputStream(inputFile));
        final byte[] buffer = new byte[10240];
        int length;
        while ((length = bin.read(buffer)) != -1) {
            byteStream.write(buffer, 0, length);
        }
        bin.close();
        return byteStream.toByteArray();
    }
}
