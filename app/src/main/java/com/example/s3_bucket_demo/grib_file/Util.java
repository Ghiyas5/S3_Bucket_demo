package com.example.s3_bucket_demo.grib_file;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.logging.Logger;

import ucar.grib.NoValidGribException;
import ucar.grib.grib1.Grib1Input;
import ucar.grib.grib2.Grib2Input;
import ucar.unidata.io.RandomAccessFile;

public class Util {
  
    private static final Logger logger = Logger.getLogger(String.valueOf(Util.class));

    public static boolean isLocalFile(String path) {
        return new File(path).exists();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static File downloadFileIfNecessary(String filePath) throws Throwable {
        if (isLocalFile(filePath))
            return new File(filePath);
        else
            return downloadFileFromURL(filePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static File downloadFileFromURL(String uri) throws Throwable {
        return downloadFileFromURL(URI.create(uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static File downloadFileFromURL(URI uri) throws Throwable {
        String filename = Paths.get(uri.getPath()).getFileName().toString();
        String tmp = System.getProperty("java.io.tmpdir");
        File destination = new File(tmp, filename);
        downloadFileFromURL(uri.toURL(), destination);
        return destination;
    }

    public static void downloadFileFromURL(URL url, File destination) throws Throwable {
        logger.info("Downloading " + url.toExternalForm() + " in " + destination.getAbsolutePath());
        try (
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

    }

    public static ucar.unidata.io.RandomAccessFile toRandomAccessFile(File file) throws IOException {
        return toRandomAccessFile(file, "r");
    }

    public static ucar.unidata.io.RandomAccessFile toRandomAccessFile(File file, String mode) throws IOException {
        return new ucar.unidata.io.RandomAccessFile(file.getAbsolutePath(), mode);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void showGrib1(File gridFile) throws NoValidGribException, IOException {

        RandomAccessFile randomFile = Util.toRandomAccessFile(gridFile);

        //Create grib2Input file
        Grib1Input grib2Input = new Grib1Input(randomFile);

        grib2Input.scan(false, false);

        grib2Input.getProducts().stream().forEach((r) -> {
            System.out.println("Product.Header: " + r.getHeader());
        });

        System.out.println("Records.size:" + grib2Input.getRecords().size());

        grib2Input.getRecords().stream().forEach((r) -> {
            System.out.println("Record:" + r);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void showGrib2(File gridFile) throws IOException {

        Grib2Dumper grib2Dumper = new Grib2Dumper(gridFile);
        Grib2Input grib2Input = grib2Dumper.getInput();
        grib2Input.getProducts().stream().forEach((p) -> {
            grib2Dumper.printProduct(p, System.out);
        });

        grib2Input.getRecords().stream().forEach((r) -> {
            try {
                grib2Dumper.printRecord(r, System.out, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
