package com.meridian.mynotes.Utils

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import com.meridian.mynotes.Activity.MainActivity
import com.meridian.mynotes.Components.Task
import com.meridian.mynotes.R
import kotlinx.android.synthetic.main.task_item.view.*

class TaskAdapter(val tasks:List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    var light_mode = MainActivity.Mode.mode
    var datasFiltered:List<Task> = tasks
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item,viewGroup,false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datasFiltered.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {

//        holder.loadAnim()

        val task = datasFiltered.get(position)
        holder.item_title.setText(task.title)
        holder.item_note.setText(task.note)
        holder.item_date.setText(task.date)

        if(light_mode=="Dark"){
            holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.search_dark))
            holder.item_title.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.dark_rv_title))
            holder.item_note.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.md_grey_400))

        }else{
            holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.md_grey_200))
            holder.item_title.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.md_black_1000))
            holder.item_note.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.md_blue_grey_700))
        }

    }

    internal fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    datasFiltered = tasks
                } else {
                    val filteredList = java.util.ArrayList<Task>()
                    for (row in tasks) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.title.toLowerCase().contains(charString.toLowerCase()) || row.title.contains(
                                        charSequence
                                )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    datasFiltered = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = datasFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                datasFiltered = filterResults.values as List<Task>
                notifyDataSetChanged()
            }
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val item_title = itemView.task_item_title
        val item_note = itemView.task_item_note
        val item_date = itemView.task_item_date
        val item_layout = itemView.task_item_layout
        fun loadAnim(){
//            img_user.setAnimation(AnimationUtils.loadAnimation(itemView.context,R.anim.fade_transition_animation))
            item_layout.setAnimation(AnimationUtils.loadAnimation(itemView.context,R.anim.fade_scale_animation))

        }
    }
}