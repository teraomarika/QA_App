package jp.techacademy.marika.terao.qa_app

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.R


import android.content.Intent


import kotlinx.android.synthetic.main.activity_question_detail.*


class favoriteDetailActivity : AppCompatActivity() {


    class FavoriteDetailActivity : AppCompatActivity() {

        private lateinit var mQuestion: Question
        private lateinit var mFavorite: favorite
        private lateinit var mAdapter: QuestionDetailListAdapter
        private lateinit var mAnswerRef: DatabaseReference
        val user: FirebaseUser? = null
        private lateinit var toastButton: Button
        var mAuthListenr: FirebaseAuth.AuthStateListener? = null
        var user1 = FirebaseAuth.getInstance().currentUser

        //private var toastButton: Button? = null
        var checkFlag: Boolean = false
        private val mEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val map = dataSnapshot.value as Map<String, String>
                Log.d("aqaqaqaqaqaqa", "aqaqqqaqaqaqaqaqa")
                val answerUid = dataSnapshot.key ?: ""

                for (answer in mQuestion.answers) {
                    // 同じAnswerUidのものが存在しているときは何もしない
                    if (answerUid == answer.answerUid) {
                        return
                    }
                }

                val body = map["body"] ?: ""
                val name = map["name"] ?: ""
                val uid = map["uid"] ?: ""

                val answer = Answer(body, name, uid, answerUid)
                mQuestion.answers.add(answer)
                listView.adapter = mAdapter
                mAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val map = dataSnapshot.value as Map<String, String>
                Log.d("っっっっっ", "aqaqqqaqaqaqaqaqa")
                val answerUid = dataSnapshot.key ?: ""

                for (answer in mQuestion.answers) {
                    // 同じAnswerUidのものが存在しているときは何もしない
                    if (answerUid == answer.answerUid) {
                        return
                    }
                }

                val body = map["body"] ?: ""
                val name = map["name"] ?: ""
                val uid = map["uid"] ?: ""

                val answer = Answer(body, name, uid, answerUid)
                mQuestion.answers.add(answer)
                listView.adapter = mAdapter
                mAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_question_detail)
            Log.d("aaa", "qqq")

            //val dataBaseReference = FirebaseDatabase.getInstance().reference

            val dataBaseReference = FirebaseDatabase.getInstance().reference
            var extras = intent.extras

            var tmp = extras?.get("favorite")

            //mQuestion = extras.get("favorite") as Favorite
            Log.d("quesitititi", mQuestion.title)

            var testRef = dataBaseReference.child("favorite").child(user1?.uid.toString())

            Log.d("aab", "はじまり")

            testRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("aab", "キャンセルとおる")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    //登録していなかったらログインページへ

                    Log.d("aab", "とおる")
                    Log.d("aab", p0.childrenCount.toString())
                    if (user1 != null) {
                        toastButton.setBackgroundColor(Color.rgb(192, 192, 192))
                        Log.d("aaabbb", "sss")
                        toastButton.text = "お気に入り登録をする"
                    }
                    if (p0.childrenCount > 0) {
                        Log.d("ffff", "ffff")
                        Log.d("ffff", mQuestion.questionUid.toString())

                        for (item in p0.children) {
                            Log.d("fffaab", item.toString())
                            if (item.key.toString() == mQuestion.questionUid.toString()) {
                                Log.d("fffaaa", item.toString())
                                checkFlag = true
                                toastButton.setBackgroundColor(Color.rgb(0, 204, 255))
                                toastButton.text = "お気に入り登録を外す"
                            }
                        }
                    }

                    Log.d("aab", "とおる2")

                }
            })
            if (user1 != null) {
                toastButton.setBackgroundColor(Color.rgb(192, 192, 192))
                toastButton.text = "お気に入りに登録をする"

                toastButton.setOnClickListener() {
                    if (user1 == null) {
                        // ログインしていなければログイン画面に遷移させる
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.d("aaaaa", "fffff")
                        if (checkFlag == false) {
                            checkFlag = true
                            // お気に入りに登録

                            Log.d("xxx", user1?.uid.toString())
                            testRef.child(mQuestion.questionUid).setValue(mQuestion.title)
                            Toast.makeText(this, "お気に入りに登録しました", Toast.LENGTH_SHORT).show()
                            toastButton.setBackgroundColor(Color.rgb(0, 204, 255))
                            toastButton.text = "お気に入り登録を外す"
                            toastButton.shadowRadius
                        } else {

                            checkFlag = false
                            // お気に入りから外す
                            Toast.makeText(this, "お気に入りからはずしました", Toast.LENGTH_SHORT).show()
                            toastButton.setBackgroundColor(Color.rgb(192, 192, 192))
                            var delete = testRef.child(mQuestion.questionUid)
                            delete.setValue(null)
                            toastButton.text = "お気に入り登録をする"
                            toastButton.shadowRadius


                        }
                    }
                }
            }
            // 渡ってきたQuestionのオブジェクトを保持する

            //println(mQuestion.questionUid)
            //val data = HashMap<String, String>()
            //data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
            //data.setValue(mQuestion.questionUid)
            Log.d("ccccc", "cccc")
            title = mQuestion.title

            // ListViewの準備
            mAdapter = QuestionDetailListAdapter(this, mQuestion)
            listView.adapter = mAdapter
            mAdapter.notifyDataSetChanged()


            fab.setOnClickListener {
                // ログイン済みのユーザーを取得する

                var user = FirebaseAuth.getInstance().currentUser

                if (user == null) {
                    // ログインしていなければログイン画面に遷移させる
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                } else {

                    // Questionを渡して回答作成画面を起動する
                    // --- ここから ---
                    val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                    intent.putExtra("question", mQuestion)
                    startActivity(intent)
                    // ---  ここまで ---
                }
            }





            mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
                .child(mQuestion.questionUid).child(AnswersPATH)
            mAnswerRef.addChildEventListener(mEventListener)


        }

        fun getRealPathFromURI(uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = managedQuery(uri, projection, null, null, null)
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }
    }


}