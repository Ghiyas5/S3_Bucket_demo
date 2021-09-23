package com.example.s3_bucket_demo

import android.R.attr
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.http.HttpResponse.builder
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.S3Object
import java.util.stream.DoubleStream.builder
import java.util.stream.Stream.builder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.S3ClientOptions.builder
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.util.ImmutableMapParameter.builder
import com.amazonaws.services.s3.model.S3ObjectSummary

import com.amazonaws.services.s3.model.ObjectListing
import com.xw.repo.BubbleSeekBar
import com.xw.repo.BubbleSeekBar.CustomSectionTextArray
import com.xw.repo.BubbleSeekBar.OnProgressChangedListenerAdapter
import java.util.function.Consumer
import kotlin.concurrent.thread
import android.animation.ObjectAnimator
import android.animation.ValueAnimator

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import java.lang.Exception
import java.security.AccessController.getContext
import android.widget.SeekBar.OnSeekBarChangeListener
import android.R.attr.stepSize
import android.os.CountDownTimer
import android.widget.*
import androidx.core.graphics.drawable.toDrawable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {
 var prog : Int = 0
     var  click = true
    lateinit  var textView: TextView
    lateinit  var btn: ImageButton
    var isClicked = false
    var isStarted = false
    var bubbleSeekBar4: BubbleSeekBar? = null
    var pluslist:ArrayList<String> = ArrayList()
    var minuslist: ArrayList<String> = ArrayList()
    private lateinit var currentTime:String
    var list: ArrayList<String> = ArrayList()

    private val seconds = 120 // two min


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        createTransferUtility()
         textView   = findViewById(R.id.text);
         btn   = findViewById(R.id.btn);

        bubbleSeekBar4  = findViewById(R.id.aw)

        bubbleSeekBar4!!.getConfigBuilder()
            .min(-60f)
            .max(60f)
            .progress(0f)
            .sectionCount(12)
            .trackColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .secondTrackColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            .thumbColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .showSectionText()
            .sectionTextColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .sectionTextSize(18)
            .showThumbText()
            .thumbTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            .thumbTextSize(18)
            .bubbleColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
            .bubbleTextSize(18)
            .showSectionMark()
            .seekBySection()
            .autoAdjustSectionMark()
            .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
            .build()



//        val anim = ValueAnimator.ofInt(0, bubbleSeekBar4?.getMax()!!.toInt())
//        anim.duration = 60000
//        anim.addUpdateListener { animation ->
//            var animProgress = animation.animatedValue as Int
//            anim.setInterpolator(LinearInterpolator())
//            var progress = Math.round((animProgress / 2).toDouble()).toInt() * 2
//            bubbleSeekBar4?.setProgress(animProgress.toFloat())
//        }
        textView.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        })



        bubbleSeekBar4?.setOnProgressChangedListener(object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
            override fun onProgressChanged(bubbleSeekBar: BubbleSeekBar, progress: Int, progressFloat: Float, fromUser: Boolean) {


              //  textView.setText(dateString)
//                if (fromUser){
//                    bubbleSeekBar4?.setProgress(progress.toFloat())
//                    bubbleSeekBar4?.animation?.start()
//                }
                if(bubbleSeekBar4?.progressFloat!! == bubbleSeekBar4?.getMax()!!){

                }

            }

        })
        btn.setOnClickListener(View.OnClickListener {
            if(bubbleSeekBar4?.progress!! < bubbleSeekBar4?.getMax()!!) {
                if (!isClicked) {
                    if (isStarted) {
                        isStarted = true
                        isClicked = true
                      //  anim.resume()
//                        btn.setImageDrawable(R.drawable.ic_baseline_play.toDrawable())
                    } else {
                        isClicked = true
                        isStarted = true
                     //   anim.start()
//                        btn.setImageDrawable(R.drawable.ic_baseline_pause.toDrawable())
                    }
                } else {
                    isClicked = false
                   // anim.pause()
//                    btn.setImageDrawable(R.drawable.ic_baseline_play.toDrawable())
                }
            }
            else{
              //  anim.start()
            }
        })

    }

    fun getCurrentDate(){
        val textView :TextView = findViewById(R.id.abcText)
        var currentcalendar = Calendar.getInstance()
        var minuscalendar = Calendar.getInstance()
        var pluscalendar = Calendar.getInstance()
        var format:SimpleDateFormat  = SimpleDateFormat("yyyy-MM-dd-HHmm") //hours and minutes, 24hr clock
        // var currentTime = format.format(currentcalendar.getTime())
        //currentlist.add(format.format(currentcalendar.getTime()))
        currentTime=format.format(currentcalendar.time)
        var x = 1
        while (x<=12){
            minuscalendar.add(Calendar.SECOND, -300);
            minuslist.add(format.format(minuscalendar.getTime()))
            pluscalendar.add(Calendar.SECOND, 300);
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


    }


}




