package com.meridian.mynotes.Utils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.meridian.mynotes.Components.Task
import com.meridian.mynotes.R
import kotlinx.android.synthetic.main.task_item.view.*

class TaskAdapter(val tasks:List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item,viewGroup,false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

//        holder.loadAnim()

        val task = tasks.get(position)
        holder.item_title.setText(task.title)
        holder.item_note.setText(task.note)
        holder.item_date.setText(task.date)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val item_title = itemView.task_item_title
        val item_note = itemView.task_item_note
        val item_date = itemView.task_item_date
        val layout = itemView.task_item_layout
        fun loadAnim(){
//            img_user.setAnimation(AnimationUtils.loadAnimation(itemView.context,R.anim.fade_transition_animation))
            layout.setAnimation(AnimationUtils.loadAnimation(itemView.context,R.anim.fade_scale_animation))

        }
    }
}