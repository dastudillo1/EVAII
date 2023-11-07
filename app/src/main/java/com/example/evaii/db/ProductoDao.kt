package com.example.evaii.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductoDao {

    @Query("SELECT * FROM producto ORDER BY comprado")
    fun findAll(): List<Producto>

    @Query("SELECT COUNT(*) FROM producto")
    fun contar(): Int

    @Insert
    fun insertar(producto: Producto):Long

    @Update
    fun actualizar(producto: Producto)

    @Delete
    fun eliminar(producto: Producto)

    @Query("SELECT * FROM producto")
    fun getAll():List<Producto>

    @Insert
    fun insertAll(vararg contactos:Producto)
    @Update
    fun update(vararg contactos:Producto)

    @Insert
    fun insert(contacto:Producto):Long

    @Query("DELETE FROM producto")
    fun getDeleteAll()

}