package com.example.todolistapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.todolistapp.dataClasses.ItemList
import com.example.todolistapp.recyclerView.ListsAdapter
import com.example.todolistapp.viewModels.TodoListViewModel
import info.androidhive.fontawesome.FontTextView


class ListFragment : Fragment( R.layout.fragment_list )
{
    lateinit var rvList : RecyclerView
    private lateinit var addBtn : FontTextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    companion object
    {
        lateinit var viewModel : TodoListViewModel
    }


    @SuppressLint( "NotifyDataSetChanged" )
    override fun onViewCreated( view : View, savedInstanceState : Bundle? ) {
        super.onViewCreated( view, savedInstanceState )

        viewModel = ViewModelProvider( this )[ TodoListViewModel::class.java ]

        rvList = view.findViewById( R.id.list_recycler_view )
        rvList.adapter = ListsAdapter( viewModel.todoLists ) // Adds the adapter to the recycler view
        viewModel.getLists( fun ( ) { rvList.adapter?.notifyDataSetChanged() } )
        addBtn = view.findViewById( R.id.list_add_btn )
        swipeRefresh = view.findViewById(R.id.swipe_list_frame)
        // listeners
        addBtn.setOnClickListener {

            val alertDialog = AlertDialog.Builder( it.context )

            alertDialog.setTitle( "ADD LIST" )
            alertDialog.setMessage( "Enter description" )

            val input = EditText( it.context )

            val layoutParameters = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT )

            input.layoutParams = layoutParameters
            alertDialog.setView( input )

            alertDialog.setPositiveButton( "ADD" )
            { dialog, which ->

                val listId = viewModel.todoLists.size
                val listDescription = input.text.toString() // Here you get the input text from the EditText

                val list = ItemList( listId, listDescription )

                viewModel.addList( list, rvList.adapter )

                rvList.adapter?.notifyDataSetChanged()
            }

            alertDialog.setNegativeButton( "Cancel" ) { dialog, which -> dialog.cancel() }

            alertDialog.show()
        }
        swipeRefresh.setOnRefreshListener {
            viewModel.getLists(refreshLists())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshLists() {
        Log.i("swipeRefresh", "onRefresh called from SwipeRefreshLayout")
        rvList.adapter?.notifyDataSetChanged()
        swipeRefresh.isRefreshing = false
    }
}