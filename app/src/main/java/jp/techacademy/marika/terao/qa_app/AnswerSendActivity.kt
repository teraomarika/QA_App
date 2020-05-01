package jp.techacademy.marika.terao.qa_app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_answer_send.*

class AnswerSendActivity : AppCompatActivity(), View.OnClickListener,
    DatabaseReference.CompletionListener {

    private lateinit var mQuestion: Question

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        if (extras != null) {
            mQuestion = extras.get("question") as Question
        }

//        UI準備
        sendButton.setOnClickListener(this)

    }

    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {
        progressBar.visibility = View.GONE

        if (databaseError == null) {
            finish()
        } else {
            Snackbar.make(findViewById(android.R.id.content), "投稿に失敗しました", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onClick(v: View?) {
        // キーボードが出てたら閉じる
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (v != null) {
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val answerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
            .child(mQuestion.questionUid).child(AnswersPATH)

        val data = HashMap<String, String>()

        // UID
        data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid

//        表示名　　Preferenceから名前をとる
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sp.getString(NameKEY, "")
        data["name"] = name!!

        // 回答を取得する
        val answer = answerEditText.text.toString()

        if (answer.isEmpty()) {
            // 回答が入力されていない時はエラーを表示するだけ
            if (v != null) {
                Snackbar.make(v, "回答を入力して下さい", Snackbar.LENGTH_LONG).show()
            }
            return

        }
        data["body"] = answer

        progressBar.visibility = View.VISIBLE
        answerRef.push().setValue(data, this)


    }


}
