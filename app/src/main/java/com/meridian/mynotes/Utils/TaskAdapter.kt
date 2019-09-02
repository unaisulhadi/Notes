package com.meridian.mynotes.Utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Toast
import com.meridian.mynotes.Activity.MainActivity.Mode
import com.meridian.mynotes.Activity.MainActivity.Mode.delete_btn_
import com.meridian.mynotes.Activity.MainActivity.Mode.fab_add_
import com.meridian.mynotes.Activity.MainActivity.Mode.selectedTaskList
import com.meridian.mynotes.Activity.MainActivity.Mode.share_btn_
import com.meridian.mynotes.Activity.MainActivity.Mode.update_btn_
import com.meridian.mynotes.Components.Task
import com.meridian.mynotes.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.task_item.view.*

class TaskAdapter(val context: Context, val tasks: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    var light_mode = Mode.mode
    var datasFiltered: List<Task> = tasks
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item, viewGroup, false)
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
        holder.setIsRecyclable(false)
//        holder.item_layout.setAnimation(AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_transition_animation))


        if (task.isSelected) {
            if (light_mode.equals("Light")) {
                holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_400))
                holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
            }else{
                holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_layout_selected))
                holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.item_title_dark))
                holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.item_note_dark))
            }
        } else {
            if(light_mode.equals("Light")) {
                holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_200))
                holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
            }else{
                holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.search_dark))
                holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.item_title_dark))
                holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.item_note_dark))
            }
        }


        holder.item_layout.setOnClickListener {
            if (selectedTaskList.size > 0) {
                swapAddDelete(selectedTaskList.size)
                if (!selectedTaskList.contains(datasFiltered.get(position))) {
                    datasFiltered.get(position).isSelected = true

                    if(light_mode.equals("Light")) {
                        holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_400))
                        holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                        holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                    }else{
                        holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_layout_selected))
                        holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.item_title_dark))
                        holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.item_note_dark))
                    }
                    selectedTaskList.add(datasFiltered.get(position))
                    swapAddDelete(selectedTaskList.size)
                } else {
                    datasFiltered.get(position).isSelected = false

                    if(light_mode.equals("Light")) {
                        holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_200))
                        holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                        holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                    }else{
                        holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.search_dark))
                        holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.item_title_dark))
                        holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.item_note_dark))
                    }
                    selectedTaskList.remove(datasFiltered.get(position))
                    swapAddDelete(selectedTaskList.size)
                }
//                Toast.makeText(context, "SELECTED==" + selectedTaskList.toString(), Toast.LENGTH_SHORT).show()
            } else {
                if(light_mode.equals("Light")) {
                    holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_200))
                    holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                    holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                }else{
                    holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.search_dark))
                    holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.item_title_dark))
                    holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.item_note_dark))
                }
                val tasks = datasFiltered.get(position)
                fab_add_.hide()
            }

        }

        holder.item_layout.setOnLongClickListener {
            if (!selectedTaskList.contains(datasFiltered.get(position))) {
                datasFiltered.get(position).isSelected = true
                if(light_mode.equals("Light")) {
                    holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_400))
                    holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                    holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.md_black_1000))
                }else{
                    holder.item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_layout_selected))
                    holder.item_title.setTextColor(ContextCompat.getColor(context, R.color.item_title_dark))
                    holder.item_note.setTextColor(ContextCompat.getColor(context, R.color.item_note_dark))
                }
                selectedTaskList.add(datasFiltered.get(position))
                swapAddDelete(selectedTaskList.size)
            }
            return@setOnLongClickListener true;
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

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item_title = itemView.task_item_title
        val item_note = itemView.task_item_note
        val item_date = itemView.task_item_date
        val item_layout = itemView.task_item_layout
        fun loadAnim() {
//            img_user.setAnimation(AnimationUtils.loadAnimation(itemView.context,R.anim.fade_transition_animation))
            item_layout.setAnimation(AnimationUtils.loadAnimation(itemView.context, R.anim.fade_scale_animation))

        }
    }

    fun swapAddDelete(size: Int) {
        if (size > 0) {
            if (selectedTaskList.size == 1) {
                update_btn_.visibility = View.VISIBLE
                share_btn_.visibility = View.VISIBLE
            } else {
                update_btn_.visibility = View.GONE
                share_btn_.visibility = View.GONE
            }
            disable()
            delete_btn_.visibility = View.VISIBLE
        } else {
            enable()
            update_btn_.visibility = View.GONE
            delete_btn_.visibility = View.GONE
            share_btn_.visibility = View.GONE
        }
    }

    fun disable() {
        fab_add_.hide()
    }

    fun enable() {
        fab_add_.show()
    }

    fun hideSelectedColor(view: View) {

        view.task_item_layout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_200))

    }
}