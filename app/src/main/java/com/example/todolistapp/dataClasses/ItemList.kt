package com.example.todolistapp.dataClasses


data class ItemList( var listId : Int, var name : String, var items : MutableList<TodoItem> = mutableListOf() )