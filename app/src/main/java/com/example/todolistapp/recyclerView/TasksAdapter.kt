package com.example.todolistapp.recyclerView

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.ListFragment
import com.example.todolistapp.R
import com.example.todolistapp.dataClasses.TodoItem
import info.androidhive.fontawesome.FontTextView


class TasksAdapter(private val todoItems: MutableList<TodoItem>) :
    RecyclerView.Adapter<TasksAdapter.ItemListAdapterViewHolder>() {


    class ItemListAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.task_name)
        var tvEditPen: FontTextView = view.findViewById(R.id.task_edit_pen)
        var tvTrashCan: FontTextView = view.findViewById(R.id.task_trash_can)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false)
        return ItemListAdapterViewHolder(view)
    }


    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun onBindViewHolder(holder: ItemListAdapterViewHolder, position: Int) {
        holder.tvName.text = todoItems[position].descripcio

        holder.tvTrashCan.setOnClickListener {

            ListFragment.viewModel.removeTask(
                todoItems[position].list.listId,
                todoItems[position].idItem,
                this
            )
        }

        // change background color depending of fet attribute
        if (todoItems[position].fet) {
            holder.tvName.setBackgroundResource(R.drawable.mark_button_container_style)
            holder.tvEditPen.setBackgroundResource(R.color.dark_checked)
            holder.tvName.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG)
        }else {
            holder.tvName.setBackgroundResource(R.drawable.button_container_style)
            holder.tvEditPen.setBackgroundResource(R.color.dark_secondary)
            holder.tvName.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG)
        }

        // listeners
        holder.tvEditPen.setOnClickListener {

            val alertDialog = AlertDialog.Builder(it.context)
            alertDialog.setTitle("MODIFY TASK")
            alertDialog.setMessage("Enter new task")

            val input = EditText(it.context)
            input.setText(todoItems[position].descripcio)
            val layoutParameters = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )

            input.layoutParams = layoutParameters

            alertDialog.setView(input)

            alertDialog.setPositiveButton("OK")
            { dialog, which ->
                // Here you get the input text from the EditText

                val newItemDescription = input.text.toString()
                val todoItem = TodoItem(
                    todoItems[position].idItem,
                    newItemDescription,
                    false,
                    0,
                    todoItems[position].list
                )

                ListFragment.viewModel.updateTask(todoItem, this)
            }

            alertDialog.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

            alertDialog.show()
        }

        holder.itemView.setOnClickListener {


            // Check task
            /*

            Del item cambias la variable de item.done = !item.done
            if ( item.done )
            {
               setbackgroundcolor = verde
            }
            else
            { setbackgroundcolor = default (@string/secondary) }
             */

//            if (todoItems[position].fet) {
//                holder.tvName.setBackgroundResource(R.drawable.mark_button_container_style)
//            } else {
//                holder.tvName.setBackgroundResource(R.drawable.button_container_style)
//            }
            ListFragment.viewModel.setItemDone(todoItems[position],!todoItems[position].fet, this) // update api conyeny
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int {
        return todoItems.size
    }
}