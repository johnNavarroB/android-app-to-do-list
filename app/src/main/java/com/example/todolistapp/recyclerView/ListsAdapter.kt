package com.example.todolistapp.recyclerView

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.ListFragment
import com.example.todolistapp.ListFragmentDirections
import com.example.todolistapp.R
import com.example.todolistapp.dataClasses.ItemList
import info.androidhive.fontawesome.FontTextView


class ListsAdapter( private val itemLists : List<ItemList> ) : RecyclerView.Adapter<ListsAdapter.ListsAdapterViewHolder>()
{


    class ListsAdapterViewHolder( view : View ) : RecyclerView.ViewHolder( view )
    {
        var tvName : TextView = view.findViewById( R.id.list_name )
        var tvEditPen : FontTextView = view.findViewById( R.id.list_edit_pen )
        var tvTrashCan : FontTextView = view.findViewById( R.id.list_trash_can )
    }


    override fun onCreateViewHolder( parent : ViewGroup, viewType : Int ) : ListsAdapterViewHolder
    {
        val view = LayoutInflater.from( parent.context ).inflate( R.layout.todo_list, parent,false )
        return ListsAdapterViewHolder( view )
    }


    @SuppressLint( "NotifyDataSetChanged" )
    override fun onBindViewHolder( holder : ListsAdapterViewHolder, position : Int )
    {
        holder.tvName.text = itemLists[ position ].name
        holder.tvTrashCan.setOnClickListener {

            ListFragment.viewModel.removeList( itemLists[ position ].listId, this)
            updateAdapter() // Updates the list
        }

        holder.tvEditPen.setOnClickListener {

            val alertDialog = AlertDialog.Builder( it.context )

            alertDialog.setTitle( "MODIFY LIST" )
            alertDialog.setMessage( "Enter new description" )

            val input = EditText( it.context )

            val layoutParameters = LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

            input.layoutParams = layoutParameters
            alertDialog.setView( input )

//            alertDialog.setIcon( R.drawable.key )

            alertDialog.setPositiveButton( "OK" )
            { dialog, which ->
                // Here you get the input text from the EditText

                val tempList = itemLists[ position ]
                tempList.name  = input.text.toString()
                ListFragment.viewModel.updateList(  tempList,this )
            }

            alertDialog.setNegativeButton( "Cancel" ) { dialog, which -> dialog.cancel() }

            alertDialog.show()
        }

        holder.itemView.setOnClickListener {

            val directions = ListFragmentDirections.fromListToTask( itemLists[ position ].listId )

            Navigation.findNavController( it ).navigate( directions )
        }
    }


    override fun getItemCount() : Int
    {
        return itemLists.size
    }


    @SuppressLint( "NotifyDataSetChanged" )
    fun updateAdapter()
    {
        this.notifyDataSetChanged()
    }
}