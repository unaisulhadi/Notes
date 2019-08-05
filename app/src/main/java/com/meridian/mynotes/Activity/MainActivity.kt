package com.meridian.mynotes.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils

import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.meridian.mynotes.Components.DatabaseClient
import com.meridian.mynotes.Components.Task
import com.meridian.mynotes.R
import com.meridian.mynotes.Utils.RecyclerItemClickListener
import com.meridian.mynotes.Utils.TaskAdapter
import kotlinx.android.synthetic.main.task_item.*
import kotlinx.android.synthetic.main.task_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.animation.LayoutAnimationController
import java.text.FieldPosition


class MainActivity : AppCompatActivity() {

    lateinit var rotate_cw: Animation
    lateinit var slide_up: Animation
    lateinit var slide_down: Animation
    lateinit var fade_in: Animation
    lateinit var fade_out: Animation
    lateinit var taskList: List<Task>
    lateinit var selectedTaskList: ArrayList<Task>
    lateinit var adapter: TaskAdapter
    lateinit var currentDate: String
    lateinit var pref:SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    companion object Mode {
        internal var mode = "Light"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref=this.getSharedPreferences("MyPref",Context.MODE_PRIVATE)


        mode = pref.getString("VIEW_MODE","Light") as String

        if(mode=="Light"){
            switch_mode.setChecked(false)
            lightMode()
        }else{
            switch_mode.setChecked(true)
            darkMode()
        }



//
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        currentDate = sdf.format(Date())
        Log.d("CURRENT_DATE", currentDate)

        val resId = R.anim.layout_animation_fall_down;
        val animation = AnimationUtils.loadLayoutAnimation(applicationContext, resId)
        rv_tasks.setHasFixedSize(true)
        rv_tasks.layoutManager = LinearLayoutManager(this)
//        rv_tasks.layoutAnimation=animation
        getTasks();

        rotate_cw = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out)




        selectedTaskList = ArrayList()


        swapAddDelete(selectedTaskList.size)
        rv_tasks.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, rv_tasks, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (selectedTaskList.size > 0) {
                    swapAddDelete(selectedTaskList.size)

                    if (!selectedTaskList.contains(taskList.get(position))) {
                        if (mode == "Light") {
                            view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_400))
                        } else {
                            view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_600))
                        }

                        selectedTaskList.add(taskList.get(position))
                        swapAddDelete(selectedTaskList.size)
                    } else {

                        if (mode == "Light") {
                            view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_200))
                        } else {
                            view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.search_dark))
                        }
                        selectedTaskList.remove(taskList.get(position))
                        swapAddDelete(selectedTaskList.size)
                    }
//                    Toast.makeText(applicationContext,"SELECTED=="+selectedTaskList.toString(),Toast.LENGTH_SHORT).show()
                }else{
//                    hideSelectedColor(view)
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

                if (!selectedTaskList.contains(taskList.get(position))) {
                    if (mode == "Light") {
                        view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_400))
                    } else {
                        view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_600))
                    }
                    selectedTaskList.add(taskList.get(position))
                    swapAddDelete(selectedTaskList.size)
                }
//                Toast.makeText(applicationContext,"SELECTED=="+selectedTaskList.toString(),Toast.LENGTH_SHORT).show()
            }
        }))



        editor = pref.edit()
        switch_mode.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                editor.putString("VIEW_MODE","Dark")
                val intent = Intent(applicationContext,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                Toast.makeText(applicationContext,"Dark Mode",Toast.LENGTH_SHORT).show()
                editor.commit()
            }else{
                editor.putString("VIEW_MODE","Light")
                val intent = Intent(applicationContext,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                Toast.makeText(applicationContext,"Light Mode",Toast.LENGTH_SHORT).show()
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
                delete_btn.visibility=View.GONE
                update_btn.visibility=View.GONE
                share_btn.visibility=View.GONE
                fab_add.hide()
                adapter.getFilter().filter(s)
                if(s.length==0){
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
            if(btn_edit_layout.visibility==View.VISIBLE){
                btn_edit_layout.visibility = View.GONE
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_edit_layout.startAnimation(slide_down)
                hideKeyboard(this)
            }else if(btn_view_layout.visibility == View.VISIBLE){
                btn_view_layout.visibility = View.GONE
                home_layout.visibility = View.GONE
                home_layout.startAnimation(fade_out)
                btn_view_layout.startAnimation(slide_down)
                fab_add.show()

                hideKeyboard(this)
            }else
            {
                btn_add_layout.visibility = View.GONE
                hideKeyboard(this)
                delete_btn.visibility = View.GONE
                update_btn.visibility = View.GONE
                share_btn.visibility=View.GONE
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
            val task= selectedTaskList.get(0)
            Log.d("TASK_SELECTED",task.title+"  "+task.note)

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
            share_btn.visibility=View.GONE
            selectedTaskList.clear()
            fab_add.show()

        }
    }

    fun shareNote(task:Task){
        val share = Intent(Intent.ACTION_SEND)
        share.type="text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT,task.title)
        share.putExtra(Intent.EXTRA_TEXT,task.note)
        startActivity(Intent.createChooser(share,"Send note via"))
    }

    fun hideSelectedColor(view:View){
        if(mode=="Light"){
            view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.md_grey_200))
        }
    }


    override fun onResume() {
        super.onResume()
        getTasks()
    }

    override fun onBackPressed() {
//
        if(btn_view_layout.visibility == View.VISIBLE){
            btn_view_layout.visibility=View.GONE
            btn_view_layout.startAnimation(slide_down)
            home_layout.visibility=View.GONE
            home_layout.startAnimation(fade_out)
            fab_add.show()
        }else if(btn_edit_layout.visibility == View.VISIBLE){
            btn_edit_layout.visibility = View.GONE
            btn_edit_layout.startAnimation(slide_down)
            home_layout.visibility=View.GONE
            home_layout.startAnimation(fade_out)
        }else if(btn_add_layout.visibility==View.VISIBLE){
            btn_add_layout.visibility = View.GONE
            btn_add_layout.startAnimation(slide_down)
            home_layout.visibility=View.GONE
            home_layout.startAnimation(fade_out)
        }else if(selectedTaskList.size>0){
            selectedTaskList.clear()
            delete_btn.visibility=View.GONE
            update_btn.visibility= View.GONE
            share_btn.visibility= View.GONE
            fab_add.show()
            getTasks()
        }else{
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
            update_btn.visibility=View.GONE
            delete_btn.visibility=View.GONE
            share_btn.visibility=View.GONE
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
                share_btn.visibility=View.VISIBLE
            } else {
                update_btn.visibility = View.GONE
                share_btn.visibility=View.GONE
            }
            disable()
            delete_btn.visibility = View.VISIBLE
        } else {
            enable()
            update_btn.visibility = View.GONE
            delete_btn.visibility = View.GONE
            share_btn.visibility=View.GONE
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
                adapter.notifyItemRemoved(position)
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
                adapter = TaskAdapter(taskList)
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
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun darkMode() {
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
        fab_add.setRippleColor(ContextCompat.getColor(applicationContext, R.color.md_grey_200));
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
        fab_add.setRippleColor(ContextCompat.getColor(applicationContext, R.color.md_grey_200));
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
    }
}


