package com.sa.restaurant.app.RestaurantsActivity

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection

class Downloadurl{

    var isUsed:Boolean=true
    var data:String?=null
    fun readUrl( url:String):String{
        var inputStream:InputStream?=null
        var urlConnection: HttpURLConnection?=null
        try{

            var url:URL= URL(url)
          urlConnection =url.openConnection() as HttpURLConnection

            urlConnection.connect()

            inputStream= urlConnection.getInputStream()
            var bufferedReader:BufferedReader= BufferedReader(InputStreamReader(inputStream))
            var sb:StringBuffer= StringBuffer()
            var line:String=""
            while(isUsed){
                line=bufferedReader.readLine()
                if(line==null)
                {
                    isUsed=false
                }else{

                    sb.append(line)
                }
            }
          data=sb.toString()

        }catch (e:MalformedURLException) {
              e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }finally {
          inputStream!!.close()
            urlConnection!!.disconnect()
        }

        return data!!
    }
}