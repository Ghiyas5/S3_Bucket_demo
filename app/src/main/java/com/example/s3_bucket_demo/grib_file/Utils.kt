package com.example.s3_bucket_demo.grib_file

import android.os.Build
import androidx.annotation.RequiresApi
import ucar.grib.NoValidGribException
import ucar.grib.grib1.Grib1Input
import ucar.grib.grib1.Grib1Product
import ucar.grib.grib1.Grib1Record
import ucar.grib.grib2.Grib2Input
import ucar.grib.grib2.Grib2Product
import ucar.grib.grib2.Grib2Record
import ucar.unidata.io.RandomAccessFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.channels.Channels
import java.nio.file.Paths
import java.util.logging.Logger

object Utils {
    private val logger =
        Logger.getLogger(Utils::class.java.toString())

    fun isLocalFile(path: String?): Boolean {
        return File(path).exists()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Throwable::class)
    fun downloadFileIfNecessary(filePath: String?): File? {
        return if (isLocalFile(filePath)) File(filePath) else downloadFileFromURL(filePath)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Throwable::class)
    fun downloadFileFromURL(uri: String?): File? {
        return downloadFileFromURL(URI.create(uri))
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(Throwable::class)
    fun downloadFileFromURL(uri: URI): File? {
        val filename = Paths.get(uri.path).fileName.toString()
        val tmp = System.getProperty("java.io.tmpdir")
        val destination = File(tmp, filename)
        downloadFileFromURL(uri.toURL(), destination)
        return destination
    }

    @Throws(Throwable::class)
    fun downloadFileFromURL(url: URL, destination: File) {
        logger.info("Downloading " + url.toExternalForm() + " in " + destination.absolutePath)
        Channels.newChannel(url.openStream()).use { rbc ->
            FileOutputStream(destination).use { fos ->
                fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
            }
        }
    }

    @Throws(IOException::class)
    fun toRandomAccessFile(file: File): RandomAccessFile? {
        return toRandomAccessFile(file, "r")
    }

    @Throws(IOException::class)
    fun toRandomAccessFile(file: File, mode: String?): RandomAccessFile? {
        return RandomAccessFile(file.absolutePath, mode)
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(NoValidGribException::class, IOException::class)
    fun showGrib1(gridFile: File?) {
        val randomFile: RandomAccessFile? =Utils.toRandomAccessFile(gridFile!!)

        //Create grib2Input file
        val grib2Input = Grib1Input(randomFile)
        grib2Input.scan(false, false)
        grib2Input.products.stream().forEach { r: Grib1Product ->
            println(
                "Product.Header: " + r.header
            )
        }
        println("Records.size:" + grib2Input.records.size)
        grib2Input.records.stream().forEach { r: Grib1Record ->
            println(
                "Record:$r"
            )
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(IOException::class)
    fun showGrib2(gridFile: File?) {
        val grib2Dumper = Grib2Dumper(gridFile)
        val grib2Input = grib2Dumper.input
        grib2Input.products.stream().forEach { p: Grib2Product? ->
            grib2Dumper.printProduct(
                p,
                System.out
            )
        }
        grib2Input.records.stream().forEach { r: Grib2Record? ->
            try {
                grib2Dumper.printRecord(r, System.out, true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}