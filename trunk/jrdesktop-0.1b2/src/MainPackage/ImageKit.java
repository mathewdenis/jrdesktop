package MainPackage;


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;

/*
 * Developer Forums
 Java 2D - How to change image "file" size by changing resolution?
 * http://forum.java.sun.com/thread.jspa?threadID=330750&messageID=4381152
 */

public class ImageKit {

    static {ImageIO.setUseCache(false);}

    //Tested buffering. Reads 1000bytes/call for jpeg (200 for png, 100 for gif)
    public static BufferedImage read(InputStream in) throws IOException {
        BufferedImage image = ImageIO.read(in);
        if (image == null) {
            throw new IOException("Read fails");
        }
        return image;
    }

    public static BufferedImage read(byte[] bytes) {
        try {
            return read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //quality means jpeg output, if quality is < 0 = use default quality
    public static void write(BufferedImage image, float quality, OutputStream out) throws IOException {
        Iterator writers = ImageIO.getImageWritersBySuffix("jpeg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("No writers found");
        }
        ImageWriter writer = (ImageWriter) writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (quality >= 0) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
        }
        writer.write(null, new IIOImage(image, null, null), param);
        ios.close();
        writer.dispose();
    }

    public static byte[] toByteArray(BufferedImage image, float quality) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            write(image, quality, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage compress(BufferedImage image, float quality) {
        return read(toByteArray(image, quality));
    }

    /*public static void flush(BufferedImage image) {
        try {
            if (image != null) {
                image.flush();
            }
        } catch (NullPointerException e) {
            //bug in sun's code
        }

    }*/
}
