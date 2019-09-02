package com.meridian.mynotes.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils

import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.content.*
import android.os.AsyncTask
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.meridian.mynotes.Components.DatabaseClient
import com.meridian.mynotes.Components.Task
import com.meridian.mynotes.R
import com.meridian.mynotes.Utils.RecyclerItemClickListener
import com.meridian.mynotes.Utils.TaskAdapter
import kotlinx.android.synthetic.main.task_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var rotate_cw: Animation
    lateinit var slide_up: Animation
    lateinit var slide_down: Animation
    lateinit var fade_in: Animation
    lateinit var fade_out: Animation
    lateinit var taskList: List<Task>
    var adapter: TaskAdapter? = null
    lateinit var currentDate: String
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor


    companion object Mode {
        internal var mode = "Dark"
        lateinit var selectedTaskList: ArrayList<Task>
        lateinit var fab_add_: FloatingActionButton
        lateinit var share_btn_: ImageView
        lateinit var update_btn_: ImageView
        lateinit var delete_btn_: ImageView
    }

    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)

        fab_add_ = fab_add
        share_btn_ = share_btn
        update_btn_ = update_btn
        delete_btn_ = delete_btn


        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;
        pref = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        mode = pref.getString("VIEW_MODE", "Light") as String

        if (mode == "Light") {
            switch_mode.setChecked(false)
            lightMode()
//            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            switch_mode.setChecked(true)
            darkMode()
//            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
//

//
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        currentDate = sdf.format(Date())
        Log.d("CURRENT_DATE", currentDate)

        val resId = R.anim.layout_animation_fall_down;
        val animation = AnimationUtils.loadLayoutAnimation(applicationContext, resId)
        rv_tasks.setHasFixedSize(true)
        rv_tasks.layoutManager = LinearLayoutManager(this)
        rv_tasks.layoutAnimation = animation
        getTasks();


        rotate_cw = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out)



        edt_search_note.isCursorVisible = false
        selectedTaskList = ArrayList()
        delete_btn.visibility = View.GONE
        update_btn.visibility = View.GONE
        share_btn.visibility = View.GONE
        selectedTaskList.clear()
        fab_add.show()

        swapAddDelete(selectedTaskList.size)
        rv_tasks.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, rv_tasks, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                hideKeyboard(this@MainActivity)
                if (selectedTaskList.size > 0) {
                    swapAddDelete(selectedTaskList.size)


                } else {
                    val task = taskList.get(position)
                    fab_add.hide()
                    home_layout.visibility = View.VISIBLE
                    home_layout.startAnimation(fade_in)
                    btn_view_layout.visibility = View.VISIBLE
                    btn_view_layout.startAnimation(slide_up)
                    view_item_title.setText(task.title)
                    view_item_note.setText(task.note)
                }
            }

            override fun onItemLongClick(view: View, position: Int) {


            }
        }))


        editor = pref.edit()
        switch_mode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editor.putString("VIEW_MODE", "Dark")
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                startActivity(intent)
                Toast.makeText(applicationContext, "Dark Mode", Toast.LENGTH_SHORT).show()
                editor.commit()
            } else {
                editor.putString("VIEW_MODE", "Light")
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                startActivity(intent)
                Toast.makeText(applicationContext, "Light Mode", Toast.LENGTH_SHORT).show()
                editor.commit()
            }
        }


        title_header.setOnClickListener {
            if (mode.equals("Light")) {
                editor.putString("VIEW_MODE", "Dark")
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                startActivity(intent)
                Toast.makeText(applicationContext, "Dark Mode", Toast.LENGTH_SHORT).show()
                editor.commit()
            } else {
                editor.putString("VIEW_MODE", "Light")
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
//                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                startActivity(intent)
                Toast.makeText(applicationContext, "Light Mode", Toast.LENGTH_SHORT).show()
                editor.commit()
            }
        }

        edt_search_note.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                selectedTaskList.clear()
                delete_btn.visibility = View.GONE
                update_btn.visibility = View.GONE
                share_btn.visibility = View.GONE
                fab_add.hide()
                adapter?.getFilter()?.filter(s)
                if (s.length == 0) {
                    fab_add.show()
                }
            }
        })

        edit_add_note.setOnClickListener {

            val task = selectedTaskList.get(0)


            //            updateTask(selectedTaskList.get(0))
            if (TextUtils.isEmpty(edit_item_title.text.toString().trim())) {
                Toast.makeText(applicationContext, "Please enter title", Toast.LENGTH_SHORT).show()
            } else {
                updateTask(task)
            }
        }

        edit_cancel_note.setOnClickListener {
            btn_edit_layout.visibility = View.GONE
            home_layout.visibility = View.GONE
            home_layout.startAnimation(fade_out)
            btn_edit_layout.startAnimation(slide_down)
            hideKeyboard(this)
        }


        home_layout.setOnClickListener {
            if (btn_edit_layout.visibility == View.VISIBLE) {
                btn_edit_layout.visibility = View.GONE
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_edit_layout.startAnimation(slide_down)
                hideKeyboard(this)
            } else if (btn_view_layout.visibility == View.VISIBLE) {
                btn_view_layout.visibility = View.GONE
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_view_layout.startAnimation(slide_down)
                fab_add.show()

                hideKeyboard(this)
            } else {
                btn_add_layout.visibility = View.GONE
                hideKeyboard(this)
                delete_btn.visibility = View.GONE
                update_btn.visibility = View.GONE
                share_btn.visibility = View.GONE
                selectedTaskList.clear()
                fab_add.show()
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_add_layout.startAnimation(slide_down)

            }
//            getTasks()
        }

        fab_add.setOnClickListener {
            fab_add.startAnimation(rotate_cw)
            selectedTaskList.clear()
            if (btn_add_layout.visibility == View.GONE) {
                btn_add_layout.visibility = View.VISIBLE
                home_layout.visibility = View.VISIBLE
                home_layout.startAnimation(fade_in)
                btn_add_layout.startAnimation(slide_up)
            } else {
                btn_add_layout.visibility = View.GONE
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_add_layout.startAnimation(slide_down)
                hideKeyboard(this)
            }

        }

        btn_cancel_note.setOnClickListener {
            btn_add_layout.visibility = View.GONE
            home_layout.visibility = View.GONE
            home_layout.startAnimation(fade_out)
            btn_add_layout.startAnimation(slide_down)
            hideKeyboard(this)
        }

        btn_add_note.setOnClickListener {
            val title = edt_item_title.text.toString().trim()
            val note = edt_item_note.text.toString().trim()
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(applicationContext, "Please enter title", Toast.LENGTH_SHORT).show()
            } else {
                addNote(title, note, currentDate)
            }
        }

        Log.d("TEST", "TEST")

        delete_btn.setOnClickListener {
            //
            for (i in 0..taskList.lastIndex) {
                for (j in 0..selectedTaskList.lastIndex) {
                    if (taskList.get(i) == selectedTaskList.get(j)) {
                        deleteNote(taskList.get(i), i)
                    }
                }
            }
            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
        }

        update_btn.setOnClickListener {
            val task = selectedTaskList.get(0)
            Log.d("TASK_SELECTED", task.title + "  " + task.note)

            edit_item_title.setText(task.title.toString())
            edit_item_note.setText(task.note.toString())

            if (btn_edit_layout.visibility == View.VISIBLE) {
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_edit_layout.startAnimation(slide_down)
                btn_edit_layout.visibility = View.GONE
            } else {
                home_layout.visibility = View.VISIBLE
                home_layout.startAnimation(fade_in)
                btn_edit_layout.visibility = View.VISIBLE
                btn_edit_layout.startAnimation(slide_up)
            }
        }


        share_btn.setOnClickListener {
            shareNote(selectedTaskList.get(0))
            delete_btn.visibility = View.GONE
            update_btn.visibility = View.GONE
            share_btn.visibility = View.GONE
            selectedTaskList.clear()
            fab_add.show()

        }

        main_layout_content.setOnClickListener {
            if (edt_search_note.isCursorVisible) {
                edt_search_note.isCursorVisible = false
            }
        }

        edt_search_note.setOnClickListener {
            edt_search_note.isCursorVisible = true
        }

        view_item_note.setOnLongClickListener {

            myClip = ClipData.newPlainText("text", view_item_note.text);
            myClipboard?.setPrimaryClip(myClip);

            Toast.makeText(this, "Note Copied", Toast.LENGTH_SHORT).show();
            return@setOnLongClickListener true
        }
    }

    fun shareNote(task: Task) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, task.title)
        share.putExtra(Intent.EXTRA_TEXT, task.title + "\n" + task.note)
        startActivity(Intent.createChooser(share, "Send note via"))
    }

    fun hideSelectedColor(view: View) {
        if (mode == "Light") {
            view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_200))
        }
    }


    override fun onResume() {
        super.onResume()
        edt_search_note.text.clear()
        delete_btn.visibility = View.GONE
        update_btn.visibility = View.GONE
        share_btn.visibility = View.GONE
        selectedTaskList.clear()
        fab_add.show()
        getTasks()
    }

    override fun onBackPressed() {
//
        edt_search_note.isCursorVisible = false

        if (btn_view_layout.visibility == View.VISIBLE) {
            btn_view_layout.visibility = View.GONE
            btn_view_layout.startAnimation(slide_down)
            home_layout.visibility = View.GONE
            home_layout.startAnimation(fade_out)
            fab_add.show()
        } else if (btn_edit_layout.visibility == View.VISIBLE) {
            btn_edit_layout.visibility = View.GONE
            btn_edit_layout.startAnimation(slide_down)
            home_layout.visibility = View.GONE
            home_layout.startAnimation(fade_out)
        } else if (btn_add_layout.visibility == View.VISIBLE) {
            btn_add_layout.visibility = View.GONE
            btn_add_layout.startAnimation(slide_down)
            home_layout.visibility = View.GONE
            home_layout.startAnimation(fade_out)
        } else if (selectedTaskList.size > 0) {
            selectedTaskList.clear()
            delete_btn.visibility = View.GONE
            update_btn.visibility = View.GONE
            share_btn.visibility = View.GONE
            fab_add.show()
            getTasks()
        } else {
            super.onBackPressed()
        }
    }

    fun hideEditLayout() {
        hideKeyboard(this)
        if (btn_edit_layout.visibility == View.VISIBLE) {
            btn_edit_layout.visibility = View.GONE
            btn_edit_layout.startAnimation(slide_down)
            home_layout.startAnimation(fade_out)
            home_layout.visibility = View.GONE
            edit_item_title.setText("")
            edit_item_note.setText("")
            update_btn.visibility = View.GONE
            delete_btn.visibility = View.GONE
            share_btn.visibility = View.GONE
            fab_add.show()
            hideKeyboard(this)
        }
        getTasks();
    }


    fun updateTask(task: Task) {
        val title = edit_item_title.text.toString().trim()
        val note = edit_item_note.text.toString().trim()
        val date = currentDate


        class UpdateTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg params: Void?): Void? {
                task.title = title
                task.note = note
                task.date = date
                task.isSelected = false
                DatabaseClient.getInstance(applicationContext).appDatabase
                        .taskDao()
                        .update(task)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                edt_search_note.setText("")
                Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
                selectedTaskList.clear()
                hideEditLayout()
            }
        }

        val update = UpdateTask()
        update.execute();
    }


    fun swapAddDelete(size: Int) {
        if (size > 0) {
            if (selectedTaskList.size == 1) {
                update_btn.visibility = View.VISIBLE
                share_btn.visibility = View.VISIBLE
            } else {
                update_btn.visibility = View.GONE
                share_btn.visibility = View.GONE
            }
            disable()
            delete_btn.visibility = View.VISIBLE
        } else {
            enable()
            update_btn.visibility = View.GONE
            delete_btn.visibility = View.GONE
            share_btn.visibility = View.GONE
        }
    }

    fun disable() {
        fab_add.hide()
    }

    fun enable() {
        fab_add.show()
    }


    fun addNote(title: String, note: String, date: String) {


        class SaveTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg params: Void?): Void? {
                val task = Task()
                task.title = title
                task.note = note
                task.date = date
                DatabaseClient.getInstance(applicationContext).getAppDatabase()
                        .taskDao()
                        .insert(task);
                return null;
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                Toast.makeText(applicationContext, "Added", Toast.LENGTH_SHORT).show()
                edt_item_title.setText("")
                edt_item_note.setText("")
                hideAddLayout()
            }
        }

        val save = SaveTask()
        save.execute();


    }

    fun deleteNote(task: Task, position: Int) {
        class DeleteTask : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg params: Void?): Void? {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .delete(task);
                return null;
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result);
                edt_search_note.setText("")
                selectedTaskList.clear()
                swapAddDelete(selectedTaskList.size)
                getTasks()
            }
        }

        val delete = DeleteTask()
        delete.execute();
    }

    private fun getTasks() {
        class GetTasks : AsyncTask<Void, Void, List<Task>>() {

            override fun doInBackground(vararg voids: Void): List<Task> {
                return DatabaseClient
                        .getInstance(applicationContext)
                        .appDatabase
                        .taskDao()
                        .all
            }

            override fun onPostExecute(tasks: List<Task>) {
                super.onPostExecute(tasks)
                taskList = tasks;
                adapter = TaskAdapter(applicationContext, taskList)
                rv_tasks.setAdapter(adapter)
            }
        }

        val gt = GetTasks()
        gt.execute()
    }


    fun hideAddLayout() {
        hideKeyboard(this)
        if (btn_add_layout.visibility == View.VISIBLE) {
            btn_add_layout.visibility = View.GONE
            btn_add_layout.startAnimation(slide_down)
            fab_add.startAnimation(rotate_cw)
            home_layout.startAnimation(fade_out)
            home_layout.visibility = View.GONE
            hideKeyboard(this)
        }
        getTasks();
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        edt_search_note.isCursorVisible = false
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun darkMode() {
        mode = "Dark"
        main_layout.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.dark_bg));
        title_header.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))
        edt_search_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_white_1000))
        edt_search_note.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.search_dark_hint))
        search_layout.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.search_et_bg_dark))
        btn_search.setImageResource(R.drawable.ic_search_dark)
        fab_add.hide()
        fab_add.setImageResource(R.drawable.ic_add_dark)
        fab_add.show()
        fab_add.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_200));
        btn_add_layout.setBackgroundResource(R.drawable.add_note_layout_dark)
        edt_item_title.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))
        edt_item_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))
        edt_item_title.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))
        edt_item_note.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))
        btn_cancel_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))
        btn_add_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))

        btn_edit_layout.setBackgroundResource(R.drawable.add_note_layout_dark)
        edit_item_title.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))
        edit_item_title.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))
        edit_item_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))
        edit_item_note.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))
        edit_cancel_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))
        edit_add_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_text_hint))

        btn_view_layout.setBackgroundResource(R.drawable.add_note_layout_dark)
        view_item_title.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))
        view_item_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.dark_rv_title))



        update_btn.setImageResource(R.drawable.ic_pencil_outline_light)
        share_btn.setImageResource(R.drawable.ic_share_light)

    }

    fun lightMode() {
        mode = "Light"
        main_layout.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.main_layout_bg));
        title_header.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_blue_grey_800))
        edt_search_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        edt_search_note.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.md_grey_500))
        search_layout.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.search_et_bg))
        btn_search.setImageResource(R.drawable.ic_search)
        fab_add.hide()
        fab_add.setImageResource(R.drawable.ic_add_black_24dp)
        fab_add.show()
        fab_add.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_500));
        btn_add_layout.setBackgroundResource(R.drawable.add_note_layout)
        edt_item_title.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        edt_item_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        edt_item_title.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.text_color_hint))
        edt_item_note.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.text_color_hint))
        btn_cancel_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        btn_add_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))

        btn_edit_layout.setBackgroundResource(R.drawable.add_note_layout)
        edit_item_title.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        edit_item_title.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.text_color_hint))
        edit_item_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        edit_item_note.setHintTextColor(ContextCompat.getColor(applicationContext, R.color.text_color_hint))
        edit_cancel_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        edit_add_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))

        btn_view_layout.setBackgroundResource(R.drawable.add_note_layout)
        view_item_title.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))
        view_item_note.setTextColor(ContextCompat.getColor(applicationContext, R.color.md_black_1000))

        update_btn.setImageResource(R.drawable.ic_pencil_outline)
        share_btn.setImageResource(R.drawable.ic_share)
    }
}


