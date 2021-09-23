package com.example.s3_bucket_demo

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.S3ObjectSummary
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.arrayListOf as arrayListOf

class MainActivity2 : AppCompatActivity() {

    var COGNITO_POOL_ID = "us-east-1:f9e6dfb7-0ec5-4bf2-9d5f-11439d2bed87"
    var BUCKET_NAME = "aerocast"
    private var transferUtility: TransferUtility? = null
    var transferRecord: ArrayList<Float> = ArrayList()
    var pluslist:ArrayList<String> = ArrayList()
    var minuslist: ArrayList<String> = ArrayList()
    private lateinit var currentTime:String
    var list: ArrayList<String> = ArrayList()

   // lateinit var list : ArrayList()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


//        Handler().postDelayed({
//            createTransferUtility()
//        }, 2000)


        Thread {
            createTransferUtility()
            runOnUiThread {
                // Post the result to the main thread
            }
        }.start()

        getCurrentDate()
    }

    private fun createTransferUtility() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            getApplicationContext(),
            COGNITO_POOL_ID,
            Regions.US_EAST_1
        )
        val s3Client = AmazonS3Client(credentialsProvider)
        transferUtility = TransferUtility(s3Client, this.getApplicationContext())
        val objectListing: ObjectListing = s3Client.listObjects(ListObjectsRequest().withBucketName(BUCKET_NAME).withPrefix("radar/klix/lixtileskml/"))
        for (objectSummary: S3ObjectSummary in objectListing.objectSummaries) {
            println(" - " + objectSummary.key + "  " + "(size = " + objectSummary.size + ")")
        }
    }
    fun getCurrentDate(){
        val textView :TextView = findViewById(R.id.abcText)
        var currentcalendar = Calendar.getInstance()
        var minuscalendar = Calendar.getInstance()
        var pluscalendar = Calendar.getInstance()
        var format:SimpleDateFormat  = SimpleDateFormat("yyyy-MM-dd-HH:mm") //hours and minutes, 24hr clock
       // var currentTime = format.format(currentcalendar.getTime())
        //currentlist.add(format.format(currentcalendar.getTime()))
        currentTime=format.format(currentcalendar.time)
        var x = 1
        while (x<=12){
            minuscalendar.add(Calendar.SECOND, -5);
            minuslist.add(format.format(minuscalendar.getTime()))
            pluscalendar.add(Calendar.SECOND, 5);
            pluslist.add(format.format(pluscalendar.getTime()))
            x++
        }

      list.addAll(minuslist)
        list.add(currentTime)
        list.addAll(pluslist)
      //  list = (minuslist+currentlist+pluslist) as ArrayList<String>

//        list.add(minuslist.toString())
//        list.add(currentlist.toString())
//        list.add(pluslist.toString())

        var b :String  = ""
        for(a in list) {
           b = a+","+b
        }
        textView.setText(b.toString())

    }
}