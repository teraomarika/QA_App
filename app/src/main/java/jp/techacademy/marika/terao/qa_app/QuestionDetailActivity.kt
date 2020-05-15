package jp.techacademy.marika.terao.qa_app

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*
import java.util.HashMap

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    val user: FirebaseUser? = null

    //    private lateinit var mFavorite: Favorite
    private lateinit var toastButton: Button
    var mAuthListenr: FirebaseAuth.AuthStateListener? = null
    var user1 = FirebaseAuth.getInstance().currentUser
    val favorite_data = HashMap<String, String>()
    var checkFlag: Boolean = false


    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

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
            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                if (answerUid == answerUid) {
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
    }

    override fun onResume() {
        super.onResume()


        if (user1 != null) {
            var toastButton: Button = findViewById(R.id.show_toast_button)
            toastButton.setVisibility(View.VISIBLE)
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        var extras = intent.extras

        if (extras != null) {
            mQuestion = extras.get("question") as Question
        }

        //mQuestion = extras.get("favorite") as Favorite

        var testRef = dataBaseReference.child("favorite").child(user1?.uid.toString())

        testRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("aab", "キャンセルとおる")
            }

            override fun onDataChange(p0: DataSnapshot) {
                //登録していなかったらログインページへf

                if (user != null) {
                    toastButton.setBackgroundColor(Color.rgb(192, 192, 192))
                    toastButton.text = "お気に入り登録をする"
                }
                if (p0.childrenCount > 0) {
                    Log.d("ffff", mQuestion.questionUid.toString())

                    for (item in p0.children) {
                        Log.d("お気に入り持ってるのはこれ", item.toString())
                        if (item.key.toString() == mQuestion.questionUid.toString()) {
                            Log.d("fffaaa", item.toString())
                            checkFlag = true
                            var toastButton: Button = findViewById(R.id.show_toast_button)
                            toastButton.setVisibility(View.VISIBLE)
                            toastButton.setBackgroundColor(Color.rgb(0, 204, 255))
                            toastButton.text = "お気に入り登録を外す"
                        }
                    }
                }

                Log.d("aab", "とおる2")

            }
        })
        if (user1 != null) {
            var toastButton: Button = findViewById(R.id.show_toast_button)
            toastButton.setVisibility(View.VISIBLE)
            toastButton.setBackgroundColor(Color.rgb(192, 192, 192))
            toastButton.text = "お気に入り登録をする"

            toastButton.setOnClickListener() {
                if (user1 == null) {
                    // ログインしていなければログイン画面に遷移させる
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("aaaaa", "押した")
                    if (checkFlag == false) {
                        checkFlag = true
                        // お気に入りに登録

                        Log.d("xxx", user1?.uid.toString())
                        favorite_data["title"] = mQuestion.title
                        favorite_data["name"] = mQuestion.name
                        favorite_data["genre"] = mQuestion.genre.toString()
                        favorite_data["uid"] = mQuestion.questionUid


                        testRef.child(mQuestion.questionUid).setValue(favorite_data)
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
                        mAdapter.notifyDataSetChanged()
                        toastButton.text = "お気に入り登録をする"
                        toastButton.shadowRadius


                    }
                }
            }
        }

        // 渡ってきたQuestionのオブジェクトを保持する

        title = mQuestion.title


        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }


        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
            .child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }


    override fun onStart() {
        super.onStart()
        if (user1 != null) {
            Log.d("aaa", "qqq")
            var toastButton: Button = findViewById(R.id.show_toast_button)
            toastButton.setVisibility(View.VISIBLE)
        }
    }

    fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }
}




