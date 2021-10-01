package com.example.s3_bucket_demo.grib_file;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;

/**
 *
 * Home http://www.nco.ncep.noaa.gov/pmb/products/wave/ <code> 
 * java GridDecoder http://www.ftp.ncep.noaa.gov/data/nccf/com/wave/prod/glwu.20180214/glwu.grlc_2p5km_sr.t02z.grib2
 * </code>
 *
 * @author humbertodias
 */
public class Grib2Decoder {



	@RequiresApi(api = Build.VERSION_CODES.O)
	public static void main(String ... args) throws Throwable {

		if (args.length < 1) {
			throw new RuntimeException("Usage GRIB2_URL_OR_FILE");
		}
		File gribfile = Util.downloadFileFromURL("https://aerocast.s3.amazonaws.com/models/hrrrlatest/hrrr.18z.fcsthr.03z-2021-08-22-20-57.grb");
	//	File gribFile = Util.downloadFileIfNecessary(args[0]);
		Grib2Dumper grib2Dumper = new Grib2Dumper(gribfile);
		grib2Dumper.dump(System.out, false);
	}

}
