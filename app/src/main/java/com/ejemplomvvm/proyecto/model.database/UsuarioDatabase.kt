package com.ejemplomvvm.proyecto.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ejemplomvvm.proyecto.model.database.dao.UserDao
import com.ejemplomvvm.proyecto.model.database.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class UsuarioDatabase: RoomDatabase() {
    abstract fun getUserDao(): UserDao
}