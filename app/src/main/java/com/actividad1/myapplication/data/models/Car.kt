package com.actividad1.myapplication.data.models

data class Car(
    val placa: String,
    val modelo: String,
    val chofer: String,
    val activo: Boolean,
    val _idKit: String,
    val _id: String
)

data class NewCar(
    val placa: String,
    val modelo: String,
    val chofer: String,
    val activo: Boolean
)