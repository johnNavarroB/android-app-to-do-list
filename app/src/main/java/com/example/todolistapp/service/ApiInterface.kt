package com.example.todolistapp.service

import com.example.todolistapp.dataClasses.ItemList
import com.example.todolistapp.dataClasses.TodoItem
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiInterface
{
    // POST, GET, PUT & DELETE requests
    companion object
    {
        private var BASE_URL = "https://todo-list-springboot.herokuapp.com"

        fun create() : ApiInterface
        {
            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl( BASE_URL )
                .addConverterFactory( GsonConverterFactory.create() )
                .client(client)
                .build()

            return retrofit.create( ApiInterface::class.java )
        }
    }


    // ---- ItemList: POST, GET, PUT & DELETE ----


    // Adds a new item list
    @POST( "/todolists" )
    fun addApiList( @Body itemList: ItemList ) : Call<ItemList>


    // Updates the list of item lists
    @GET( "/todolists" )
    fun getApiLists() : Call< MutableList<ItemList> >


    // Modifies an item list
    @PUT( "/todolists" )
    fun updateApiList( @Body itemList : ItemList ) : Call<ItemList>

    // Deletes a specific list by its id
    @DELETE( "/todolists/{id}" )
    fun deleteApiList( @Path( "id" ) id : Int ) : Call<ItemList>


    // ---- TodoItem: POST, GET, PUT & DELETE ----


    // Adds a new item to a specific list
    @POST( "/todolists/{id}/todoitems" )
    fun addApiItem( @Path( "id" ) id : Int, @Body todoItem : TodoItem ) : Call<TodoItem>


    // Gets all the items
    @GET( "/todoitems" )
    fun getApiItems() : Call< MutableList<TodoItem> >


    // Gets the items of a specific list
    @GET( "/todolists/{id}/todoitems" )
    fun getApiItems( @Path( "id" ) id : Int ) : Call< MutableList<TodoItem> >


    // Updates a specific item
    @PUT( "/todoitems" )
    fun updateApiItem( @Body todoItem : TodoItem ) : Call<TodoItem>


    // Deletes a specific item by its id
    @DELETE( "/todoitems/{id}" )
    fun deleteApiItem( @Path( "id" ) id : Int ) : Call<TodoItem>
}