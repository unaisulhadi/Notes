package com.meridian.mynotes.Activity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils

import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.meridian.mynotes.Components.DatabaseClient
import com.meridian.mynotes.Components.Task
import com.meridian.mynotes.R
import com.meridian.mynotes.Utils.Const.mID
import com.meridian.mynotes.Utils.RecyclerItemClickListener
import com.meridian.mynotes.Utils.TaskAdapter
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.task_item.*
import kotlinx.android.synthetic.main.task_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val item = mID
    lateinit var rotate_cw: Animation
    lateinit var slide_up :Animation
    lateinit var slide_down:Animation
    lateinit var fade_in:Animation
    lateinit var fade_out:Animation
    lateinit var taskList:List<Task>
    lateinit var selectedTaskList:ArrayList<Task>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        Log.d("CURRENT_DATE",currentDate)


        rv_tasks.setHasFixedSize(true)
        rv_tasks.layoutManager = LinearLayoutManager(this)
        getTasks();

        rotate_cw = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        fade_in=AnimationUtils.loadAnimation(this, R.anim.fade_in)
        fade_out=AnimationUtils.loadAnimation(this, R.anim.fade_out)



        selectedTaskList= ArrayList()


        swapAddDelete(selectedTaskList.size)
        rv_tasks.addOnItemTouchListener(RecyclerItemClickListener(applicationContext, rv_tasks, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if(selectedTaskList.size >0){
                    swapAddDelete(selectedTaskList.size)
                    if(!selectedTaskList.contains(taskList.get(position))){
                        view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.md_grey_500))
                        selectedTaskList.add(taskList.get(position))
                        swapAddDelete(selectedTaskList.size)
                    }else{
                        view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.md_grey_200))
                        selectedTaskList.remove(taskList.get(position))
                        swapAddDelete(selectedTaskList.size)
                    }
                    Toast.makeText(applicationContext,"SELECTED=="+selectedTaskList.toString(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onItemLongClick(view: View, position: Int) {

                if(!selectedTaskList.contains(taskList.get(position))){
                    view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.md_grey_500))
                    selectedTaskList.add(taskList.get(position))
                    swapAddDelete(selectedTaskList.size)
                }
                Toast.makeText(applicationContext,"SELECTED=="+selectedTaskList.toString(),Toast.LENGTH_SHORT).show()
            }
        }))



        fab_add.setOnClickListener {
            fab_add.startAnimation(rotate_cw)
            selectedTaskList.clear()
            if (btn_add_layout.visibility == View.GONE) {
                btn_add_layout.visibility = View.VISIBLE
                home_layout.visibility= View.VISIBLE
                home_layout.startAnimation(fade_in)
                btn_add_layout.startAnimation(slide_up)
            } else {
                btn_add_layout.visibility = View.GONE
                home_layout.visibility= View.GONE
                home_layout.startAnimation(fade_out)
                btn_add_layout.startAnimation(slide_down)
                hideKeyboard(this)
                getTasks()
            }

        }

        btn_cancel_note.setOnClickListener {
            btn_add_layout.visibility = View.GONE
            home_layout.visibility= View.GONE
            home_layout.startAnimation(fade_out)
            btn_add_layout.startAnimation(slide_down)
            hideKeyboard(this)
        }

        btn_add_note.setOnClickListener {
            val title = edt_item_title.text.toString().trim()
            val note = edt_item_note.text.toString().trim()
            if(TextUtils.isEmpty(title)){
                Toast.makeText(applicationContext,"Please enter title",Toast.LENGTH_SHORT).show()
            }else{
                addNote(title,note,currentDate)
            }
        }

        delete_btn.setOnClickListener {
            Toast.makeText(applicationContext,"DELETE",Toast.LENGTH_SHORT).show()

        }
    }

    fun swapAddDelete(size:Int){
        if(size > 0){
            disable()
            delete_btn.visibility = View.VISIBLE
        }else{
            enable()
            delete_btn.visibility = View.GONE
        }
    }

    fun disable(){
        fab_add.isEnabled=false
        fab_add.visibility = View.GONE
    }

    fun enable(){
        fab_add.isEnabled=true
        fab_add.visibility = View.VISIBLE
    }


    fun addNote(title:String,note:String,date:String){


        class SaveTask:AsyncTask<Void,Void,Void>(){

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
                Toast.makeText(applicationContext,"Added",Toast.LENGTH_SHORT).show()
                edt_item_title.setText("")
                edt_item_note.setText("")
                hideAddLayout()
            }
        }

        val save = SaveTask()
        save.execute();


    }

    fun deleteNote(task: Task){
        class DeleteTask:AsyncTask<Void,Void,Void>(){

            override fun doInBackground(vararg params: Void?): Void? {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .delete(task);
                return null;
            }

            override fun onPostExecute(result: Void?) {
                 super.onPostExecute(result);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        val save = SaveTask()
        save.execute();
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
                val adapter = TaskAdapter(taskList)
                rv_tasks.setAdapter(adapter)
            }
        }

        val gt = GetTasks()
        gt.execute()
    }


    fun hideAddLayout(){
        hideKeyboard(this)
        if(btn_add_layout.visibility == View.VISIBLE){
            btn_add_layout.visibility = View.GONE
            btn_add_layout.startAnimation(slide_down)
            fab_add.startAnimation(rotate_cw)
            home_layout.startAnimation(fade_out)
            home_layout.visibility= View.GONE
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


}


