package com.example.testappforandroidsetup

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
class MainActivity : AppCompatActivity() {

    private lateinit var myTextView: TextView // TextView referansı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        myTextView = findViewById(R.id.textview_http) // TextView öğesini bulun


        val retrofit = Retrofit.Builder()
            .baseUrl("http://me.atxled.com/") // Ana URL
            .addConverterFactory(ScalarsConverterFactory.create()) // Düz metin dönüştürücü
            .build()

        val service = retrofit.create(AtxApiService::class.java)

        service.fetchContent().enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    val content = response.body() // Yanıt içeriği

                    //val testHtml = """<pre>select the Pi with the mac address matching the last 4 in this list<p> 10.0.0.138 <a href=http://10.0.0.138/ >5b76</a> &nbsp; &nbsp; April 21 2024 11:07:25   5B76 <br> 10.0.0.146 <a href=http://10.0.0.146/ >5be2</a> &nbsp; &nbsp; April 21 2024 11:17:48   5be2 <br><p>  <a href=http://localhost >This Pi</a> (only if using an HDMI monitor)<br><p>  <a href=http://hoobs.local >Hoobs</a> only valid if a Hoobs system is on the same LAN<br>"""

                    println("Alınan içerik: $content")
                    val document: Document = Jsoup.parse(content)

                    val anchorTags = document.select("a")

                    for (anchor in anchorTags) {
                        val href = anchor.attr("href") // Href özelliği
                        val linkText = anchor.text() // Bağlantı metni

                        println("IP: $href, Kod: $linkText \n")
                        myTextView.append("IP: $href, Kod: $linkText\n")
                    }
                    myTextView.append("-----END-----")
                } else {
                    println("Yanıt başarısız oldu: ${response.message()}")
                    myTextView.append("Yanıt başarısız: ${response.message()}\n")

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                println("HTTP isteği başarısız oldu: ${t.message}")
                myTextView.append("HTTP isteği başarısız: ${t.message}\n")

            }
        })


    }
}