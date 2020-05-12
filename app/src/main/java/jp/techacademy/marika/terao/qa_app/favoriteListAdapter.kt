package jp.techacademy.marika.terao.qa_app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.ArrayList

class favoriteListAdapter(context: Context):BaseAdapter(){
    private var mLayoutInflater: LayoutInflater
    private var mFavoriteArrayList = ArrayList<favorite>()

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        Log.d("FavoriteListAdapter", "Count")
        return mFavoriteArrayList.size
    }

    override fun getItem(position: Int): Any {
        Log.d("FavoriteListAdapter", "Item")
        return mFavoriteArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        Log.d("FavoriteListAdapter", "Id")
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d("FavoriteListAdapter", convertView.toString())
        var convertView = convertView
        if(position == 0) {
            Log.d("zero", "zero")

        } else {
            Log.d("not zero", "not zero")
        }
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_favorite, parent, false)
            Log.d("FavoriteListAdapter2", convertView.toString())
        }

        val titleText = convertView!!.findViewById<View>(R.id.nameTextView) as TextView
        //titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50.0F)
        //titleText.setGravity(Gravity.LEFT)
        //titleText.layoutDirection
        titleText.text = "★ " + mFavoriteArrayList[position].title


        return convertView
    }

    fun setFavoriteArrayList(favoriteArrayList: ArrayList<favorite>) {
        mFavoriteArrayList = favoriteArrayList
    }
}