package com.ejemplomvvm.proyecto.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ejemplomvvm.proyecto.model.database.entities.UserEntity

@Dao
interface UserDao {

    //@Query("SELECT * FROM usuarios")
    @Query("SELECT * FROM usuarios ORDER BY nombre DESC")
    suspend fun getAllUsers():List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(usuarios:List<UserEntity>)

    @Insert
    suspend fun insertar(usuario: UserEntity)

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos()

    @Update
    suspend fun actualizarUsuario(usuario: UserEntity)

}