package jp.techacademy.marika.terao.qa_app

import android.util.Log
import java.io.Serializable

class favorite (val id:String,val title:String):Serializable{
    init {
        Log.d("Favoritemodel",title.toString())
        //imageBytes = bytes.clone()
    }
}
