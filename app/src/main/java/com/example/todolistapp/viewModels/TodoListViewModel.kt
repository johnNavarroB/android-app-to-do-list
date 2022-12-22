package com.example.todolistapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.dataClasses.ItemList
import com.example.todolistapp.dataClasses.TodoItem
import com.example.todolistapp.recyclerView.ListsAdapter
import com.example.todolistapp.recyclerView.TasksAdapter
import com.example.todolistapp.service.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TodoListViewModel : ViewModel() {
    var allItems = mutableListOf<TodoItem>()
    var todoLists = mutableListOf<ItemList>()
    private val api = ApiInterface.create()


    // ---- ItemList: POST, GET, PUT & DELETE ----


    // (POST) Adds a list
    fun addList(itemList: ItemList, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
        val request = api.addApiList(itemList)

        request.enqueue(object : Callback<ItemList> {
            override fun onFailure(call: Call<ItemList>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(call: Call<ItemList>, response: Response<ItemList>) {
                if (response.isSuccessful) {
                    val list = response.body()
                    list?.items = mutableListOf() // add an empty list of items in list
                    if (list != null) todoLists.add(list)
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }


    // (LOCAL) Returns a specific list by its id
    fun getListById(listId: Int): ItemList? {
        return todoLists.find { it.listId == listId }
    }


    // (GET) Gets the list of lists
    fun getLists( onResponseFunction:Any )
    {
        val request = api.getApiLists()

        request.enqueue(object : Callback<MutableList<ItemList>> {
            override fun onFailure(call: Call<MutableList<ItemList>>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(
                call: Call<MutableList<ItemList>>,
                response: Response<MutableList<ItemList>>
            ) {
                if (response.isSuccessful) {
                    val lists = response.body()

                    if (lists != null) {
                        todoLists.clear()
                        todoLists.addAll(lists)
                        todoLists.forEach { list -> getItems(list.listId) }
                        getItems()
                        onResponseFunction
                    }
                }
            }
        })
    }


    // (PUT) Edits a list
    fun updateList(itemList: ItemList,
                   adapter: RecyclerView.Adapter<ListsAdapter.ListsAdapterViewHolder>? = null
    ) {
        val request = api.updateApiList(itemList)

        request.enqueue(object : Callback<ItemList> {
            override fun onFailure(call: Call<ItemList>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(call: Call<ItemList>, response: Response<ItemList>) {
                val body = response.body()

                if (response.isSuccessful) {
                    for (i in 0 until todoLists.size) { // Recorre la lista
                        if (todoLists[i].listId == itemList.listId) { // Comprueba que el id de la lista sea igual que el del item (FK)
                                todoLists[i] = itemList
                        }
                    }
                    adapter?.notifyDataSetChanged() // Updates the recycler view
                }
            }
        })
    }


    // (DELETE) Deletes a specific list by id
    fun removeList(listId: Int, adapter: ListsAdapter) {
        val request = api.deleteApiList(listId)

        request.enqueue(object : Callback<ItemList> {
            override fun onFailure(call: Call<ItemList>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(call: Call<ItemList>, response: Response<ItemList>) {
                if (response.isSuccessful) {
                    for (i in 0 until todoLists.size) {
                        if (todoLists[i].listId == listId) {
                                todoLists.remove(todoLists[i])
                                break
                            }
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
        })
    }


    // ---- TodoItem: POST, GET, PUT & DELETE ----


    // (POST) Adds an item to a specific list
    fun addTask(todoItem: TodoItem, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) {
        val request = api.addApiItem(todoItem.list.listId, todoItem)

        request.enqueue(object : Callback<TodoItem> {
            override fun onFailure(call: Call<TodoItem>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(call: Call<TodoItem>, response: Response<TodoItem>) {
                if (response.isSuccessful) {
                    val task = response.body()
                    if (task != null) getListById(todoItem.list.listId)?.items?.add(task)

                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }


    // (GET) Gets all the items
    fun getItems() {
        val request = api.getApiItems()

        request.enqueue(object : Callback<MutableList<TodoItem>> {
            override fun onFailure(call: Call<MutableList<TodoItem>>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(
                call: Call<MutableList<TodoItem>>,
                response: Response<MutableList<TodoItem>>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()

                    if (items != null) allItems = items
                }
            }
        })
    }


    // (GET) Gets the items of a list
    fun getItems(listId: Int) {
        val request = api.getApiItems(listId)

        request.enqueue(object : Callback<MutableList<TodoItem>> {
            override fun onFailure(call: Call<MutableList<TodoItem>>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(
                call: Call<MutableList<TodoItem>>,
                response: Response<MutableList<TodoItem>>
            ) {
                if (response.isSuccessful) {
                    val items = response.body()

                    if (items != null) todoLists.find { it.listId == listId }?.items = items
                }
            }
        })
    }


    // (PUT) Updates a specific item
    fun updateTask(todoItem: TodoItem, adapter: TasksAdapter) {
        val request = api.updateApiItem(todoItem)

        request.enqueue(object : Callback<TodoItem> {
            override fun onFailure(call: Call<TodoItem>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(call: Call<TodoItem>, response: Response<TodoItem>) {
                if (response.isSuccessful) {
                    for (i in 0 until todoLists.size) { // Recorre la lista
                        if (todoLists[i].listId == todoItem.list.listId) { // Comprueba que el id de la lista sea igual que el del item (FK)
                            for (j in 0 until todoLists[i].items.size) { // Recorre la lista de items
                                if (todoLists[i].items[j].idItem == todoItem.idItem) {
                                    todoLists[i].items[j] = todoItem
                                    break
                                }
                            }
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }


    // (DELETE) Deletes a specific item by its id
    fun removeTask(listId: Int, taskId: Int, adapter: TasksAdapter) {
        val request = api.deleteApiItem(taskId)

        request.enqueue(object : Callback<TodoItem> {
            override fun onFailure(call: Call<TodoItem>, throwable: Throwable) {
                Log.e("ERROR", throwable.message.toString())
            }

            override fun onResponse(call: Call<TodoItem>, response: Response<TodoItem>) {
                if (response.isSuccessful) {
                    for (i in 0 until todoLists.size) {
                        if (todoLists[i].listId == listId) {
                            for (j in 0 until todoLists[i].items.size) {
                                if (todoLists[i].items[j].idItem == taskId) {
                                    todoLists[i].items.removeAt(j)
                                    break
                                }
                            }
                        }
                    }
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    // (local) mark unmark an item
    fun setItemDone(item: TodoItem, done: Boolean, adapter: TasksAdapter){
        item.fet = done
        updateTask(item, adapter) // update api
    }
}