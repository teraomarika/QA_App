package jp.techacademy.marika.terao.qa_app

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.util.Base64
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_question_detail.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mToolbar: Toolbar
    private var mGenre = 0


    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mListView2: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mFavoriteArrayList: ArrayList<favorite>
    private lateinit var mAdapter: QuestionsListAdapter
    private lateinit var mAdapter2: favoriteListAdapter
    private lateinit var mNavigationView: NavigationView
    private lateinit var fab: FloatingActionButton
    var bbb: List<String> = mutableListOf()
    var ccc: Map<String, String> = mutableMapOf()
    private lateinit var params: FloatArray
    private var mGenreRef: DatabaseReference? = null

    var user1 = FirebaseAuth.getInstance().currentUser

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            if (mGenre == 99) {
                Log.d("ccc", ccc.keys.toString())
                for (favorite_item: String in ccc.keys) {
                    if (favorite_item.toString() == dataSnapshot.key.toString()) {

                        val map = dataSnapshot.value as Map<String, String>
                        val title = map["title"] ?: ""
                        val body = map["body"] ?: ""
                        val name = map["name"] ?: ""
                        val uid = map["uid"] ?: ""
                        val imageString = map["image"] ?: ""
                        val bytes =
                            if (imageString.isNotEmpty()) {
                                Base64.decode(imageString, Base64.DEFAULT)
                            } else {
                                byteArrayOf()
                            }

                        val answerArrayList = ArrayList<Answer>()
                        val answerMap = map["answers"] as Map<String, String>?
                        if (answerMap != null) {
                            for (key in answerMap.keys) {
                                val temp = answerMap[key] as Map<String, String>
                                val answerBody = temp["body"] ?: ""
                                val answerName = temp["name"] ?: ""
                                val answerUid = temp["uid"] ?: ""
                                val answer = Answer(answerBody, answerName, answerUid, key)
                                answerArrayList.add(answer)
                            }
                        }

                        val question = Question(
                            title, body, name, uid, dataSnapshot.key ?: "",
                            mGenre, bytes, answerArrayList
                        )

                        mQuestionArrayList.add(question)
                        mAdapter.notifyDataSetChanged()
                    }
                }

            } else {
                val map = dataSnapshot.value as Map<String, String>
                val title = map["title"] ?: ""
                val body = map["body"] ?: ""
                val name = map["name"] ?: ""
                val uid = map["uid"] ?: ""
                val imageString = map["image"] ?: ""
                val bytes =
                    if (imageString.isNotEmpty()) {
                        Base64.decode(imageString, Base64.DEFAULT)
                    } else {
                        byteArrayOf()
                    }
                val answerArrayList = ArrayList<Answer>()
                val answerMap = map["answers"] as Map<String, String>?
                if (answerMap != null) {
                    for (key in answerMap.keys) {
                        val temp = answerMap[key] as Map<String, String>
                        val answerBody = temp["body"] ?: ""
                        val answerName = temp["name"] ?: ""
                        val answerUid = temp["uid"] ?: ""
                        val answer = Answer(answerBody, answerName, answerUid, key)
                        answerArrayList.add(answer)
                    }
                }
                val question = Question(
                    title, body, name, uid, dataSnapshot.key ?: "",
                    mGenre, bytes, answerArrayList
                )
                mQuestionArrayList.add(question)
                mAdapter.notifyDataSetChanged()
            }
        }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    val map = dataSnapshot.value as Map<String, String>

                    // 変更があったQuestionを探す
                    for (question in mQuestionArrayList) {
                        if (dataSnapshot.key.equals(question.questionUid)) {
                            // このアプリで変更がある可能性があるのは回答(Answer)のみ
                            question.answers.clear()
                            val answerMap = map["answers"] as Map<String, String>?
                            if (answerMap != null) {
                                for (key in answerMap.keys) {
                                    val temp = answerMap[key] as Map<String, String>
                                    val answerBody = temp["body"] ?: ""
                                    val answerName = temp["name"] ?: ""
                                    val answerUid = temp["uid"] ?: ""
                                    val answer = Answer(answerBody, answerName, answerUid, key)
                                    question.answers.add(answer)
                                }
                            }

                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }


                override fun onChildRemoved(p0: DataSnapshot) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            }


            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
//                setContentView(R.layout.activity_main)
//

                setContentView(R.layout.activity_main)
                if(mGenre !=99){
                    fab=findViewById<FloatingActionButton>(R.id.fab)
                }

                mToolbar = findViewById(R.id.toolbar)
                setSupportActionBar(mToolbar)


                val fab = findViewById<FloatingActionButton>(R.id.fab)
                fab.setOnClickListener { view -> // ジャンルを選択していない場合（mGenre == 0）はエラーを表示するだけ
                    if (mGenre == 0) {
                        Snackbar.make(view, "ジャンルを選択してください", Snackbar.LENGTH_LONG).show()
                    } else {
                    }
                    // ログイン済みのユーザーを取得する

//                    val user = FirebaseAuth.getInstance().currentUser

                    if (user1 == null||mGenre!=99) {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        val intent = Intent(applicationContext, QuestionSendActivity::class.java)
                        intent.putExtra("genre", mGenre)
                        startActivity(intent)
                    }
                }


                // ナビゲーションドロワーの設定
                val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                val toggle =
                    ActionBarDrawerToggle(
                        this,
                        drawer,
                        mToolbar,
                        R.string.app_name,
                        R.string.app_name
                    )
                drawer.addDrawerListener(toggle)
                toggle.syncState()

                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                navigationView.setNavigationItemSelectedListener(this)

//        Firebase
                mDatabaseReference = FirebaseDatabase.getInstance().reference

//        ListViewの準備
                mListView = findViewById(R.id.listView)
//                mAdapter = QuestionsListAdapter(this)
                mQuestionArrayList = ArrayList<Question>()
//                mAdapter.notifyDataSetChanged()

                mListView.setOnItemClickListener { parent, view, position, id ->
                    // Questionのインスタンスを渡して質問詳細画面を起動する
                    val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
                    intent.putExtra("question", mQuestionArrayList[position])
                    startActivity(intent)
                }
                mAdapter.notifyDataSetChanged()

            }

            override fun onResume() {
                super.onResume()
                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                mNavigationView = findViewById(R.id.nav_view)
                mNavigationView.setNavigationItemSelectedListener(this)
                navigationView.setNavigationItemSelectedListener(this)

                var user1 = FirebaseAuth.getInstance().currentUser
                if (user1 != null) {

                    val menuNav = mNavigationView.menu
                    val tmp = menuNav.findItem(R.id.nav_favorite)
                    tmp.setVisible(true)    //すぐ 反映したい

                } else {
                    val menuNav = mNavigationView.menu
                    val tmp = menuNav.findItem(R.id.nav_favorite)
                    tmp.setVisible(false)
                }


                // 1:趣味を既定の選択とする
                if (mGenre == 0) {
                    onNavigationItemSelected(navigationView.menu.getItem(0))
                }
                val favoriteRef = FirebaseAuth.getInstance().currentUser
                if (user1 != null) {
                    var favoriteRef = mDatabaseReference.child("favorite").child(user1!!.uid)
                    favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            ccc = p0.value as Map<String, String>
                        }
                    })
                }


            }

            override fun onCreateOptionsMenu(menu: Menu): Boolean {
                // Inflate the menu; this adds items to the action bar if it is present.
                menuInflater.inflate(R.menu.menu_main, menu)
                return true
            }

            override fun onOptionsItemSelected(item: MenuItem): Boolean {
                val id = item.itemId
                if (id == R.id.action_settings) {
                    val intent = Intent(applicationContext, SettingActivity::class.java)
                    startActivity(intent)
                    return true
                }
                return super.onOptionsItemSelected(item)
            }

            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.itemId
                val user = FirebaseAuth.getInstance().currentUser


                if (id == R.id.nav_hobby) {
                    mToolbar.title = "趣味"
                    mGenre = 1
                } else if (id == R.id.nav_life) {
                    mToolbar.title = "生活"
                    mGenre = 2
                } else if (id == R.id.nav_health) {
                    mToolbar.title = "健康"
                    mGenre = 3
                } else if (id == R.id.nav_compter) {
                    mToolbar.title = "コンピューター"
                    mGenre = 4
                } else if (id == R.id.nav_favorite) {
                    if (user == null) {
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        mToolbar.title = "登録"
                    } else {
                        mToolbar.title = "お気に入り"
                        mGenre = 99
                    }
                }


                val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawer.closeDrawer(GravityCompat.START)


                // 質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットし直す
                mQuestionArrayList.clear()
                mAdapter.settQuestionArrayList(mQuestionArrayList)
                mListView.adapter = mAdapter

                // 選択したジャンルにリスナーを登録する

                if (mGenreRef != null) {
                    mGenreRef!!.removeEventListener(mEventListener)
                }
                if (mGenre == 99) {

                    // お気に入り呼び出し
                    val array: ArrayList<Int> = arrayListOf(0, 1, 2, 3)
                    for (i in array) {
                        mGenreRef = mDatabaseReference.child(ContentsPATH).child(i.toString())
                        mGenreRef!!.addChildEventListener(mEventListener)
                    }
                    mGenreRef = mDatabaseReference.child(ContentsPATH).child(mGenre.toString())
                    mGenreRef!!.addChildEventListener(mEventListener)

                } else {
                    mGenreRef = mDatabaseReference.child(ContentsPATH).child(mGenre.toString())
                    mGenreRef!!.addChildEventListener(mEventListener)
                }
                return true

            }

        }
