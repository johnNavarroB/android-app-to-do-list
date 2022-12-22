package com.example.todolistapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.todolistapp.dataClasses.TodoItem
import com.example.todolistapp.recyclerView.TasksAdapter
import info.androidhive.fontawesome.FontTextView


class TaskFragment : Fragment( R.layout.fragment_task )
{
    private lateinit var rvTask : RecyclerView
    private lateinit var addBtn : FontTextView
    private lateinit var title : TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private fun getNewId() : Int {
        var i = 1
        while ( true ){
            i ++
            val item = ListFragment.viewModel.allItems.find { it.idItem == i }
            item ?: break
        }
        return i
    }


    @SuppressLint( "NotifyDataSetChanged" )
    override fun onViewCreated( view : View, savedInstanceState : Bundle? ) {
        super.onViewCreated( view, savedInstanceState )
        val listId = arguments?.getInt( "list_id" ) // Gets the parameter sent by the navigation_action
        if ( listId != null ) {
            val listTitle = ListFragment.viewModel.getListById( listId )?.name

            title = view.findViewById( R.id.container )
            title.text = listTitle

            rvTask = view.findViewById( R.id.task_recycler_view )
            updateTasksbyListId(listId)
        }

        swipeRefresh = view.findViewById(R.id.swipe_task_frame)
        addBtn = view.findViewById( R.id.task_add_btn )
        // listeners
        addBtn.setOnClickListener {

            val alertDialog = AlertDialog.Builder( it.context )

            alertDialog.setTitle( "ADD TASK" )
            alertDialog.setMessage( "What is the task about?" )

            val input = EditText( it.context )

            val layoutParameters = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT )

            input.layoutParams = layoutParameters
            alertDialog.setView( input )

            alertDialog.setPositiveButton( "ADD" )
            { dialog, which ->
                // Here you get the input text from the EditText

                if ( listId != null )
                {
                    val taskItemList = ListFragment.viewModel.getListById( listId )
                    val newTaskId = getNewId()

                    if ( taskItemList != null )
                    {
                        val todoItem = TodoItem( newTaskId, input.text.toString(), false, 0, taskItemList )

                        ListFragment.viewModel.addTask( todoItem, rvTask.adapter )

                        rvTask.adapter?.notifyDataSetChanged()
                    }
                }
            }

            alertDialog.setNegativeButton( "Cancel" ) { dialog, which -> dialog.cancel() }

            alertDialog.show()
        }
        swipeRefresh.setOnRefreshListener {
            ListFragment.viewModel.getLists(refreshLists(listId!!))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTasksbyListId(listId:Int) {
        rvTask.adapter = TasksAdapter(ListFragment.viewModel.getListById( listId )!!.items) // Adds the adapter to the recycler view
        rvTask.adapter?.notifyDataSetChanged()
    }

    private fun refreshLists(listId:Int) {
        Log.i("swipeRefresh", "onRefresh called from SwipeRefreshLayout from tasks")
        updateTasksbyListId(listId).also { swipeRefresh.isRefreshing = false }
    }
}