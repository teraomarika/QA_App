package jp.techacademy.marika.terao.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class QuestionsListAdapter(context: Context):BaseAdapter() {
    private var mLayoutInflater: LayoutInflater
    private var mQuestionArrayList = ArrayList<Question>()

    init {
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_questions, parent, false)
        }
        val titleText = convertView!!.findViewById<View>(R.id.titleTextView) as TextView
        titleText.text = mQuestionArrayList[position].title

        val nameText = convertView.findViewById<View>(R.id.nameTextView) as TextView
        nameText.text = mQuestionArrayList[position].name

        val resText = convertView.findViewById<View>(R.id.resTextView) as TextView
        val resNum = mQuestionArrayList[position].answers.size
        resText.text = resNum.toString()

        val bytes = mQuestionArrayList[position].imageBytes
        if (bytes.isNotEmpty()) {
            val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                .copy(Bitmap.Config.ARGB_8888, true)
            val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
            imageView.setImageBitmap(image)
//
//            val favoriteButton=convertView.findViewById<View>(R.id.favoriteButton)as Button

        }
        return convertView
    }

    fun setQuestionArrayList(questionArrayList: ArrayList<Question>){
        mQuestionArrayList=questionArrayList
}

    override fun getItem(position: Int): Any {
        return mQuestionArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mQuestionArrayList.size
    }





}