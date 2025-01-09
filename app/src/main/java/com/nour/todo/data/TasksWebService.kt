package com.nour.todo.data

import Task
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TasksWebService {

    // Récupérer toutes les tâches
    @GET("/rest/v2/tasks/")
    suspend fun fetchTasks(): Response<List<Task>>

    // Créer une nouvelle tâche
    @POST("/rest/v2/tasks/")
    suspend fun create(@Body task: Task): Response<Task>

    // Mettre à jour une tâche existante
    @POST("/rest/v2/tasks/{id}")
    suspend fun update(@Body task: Task, @Path("id") id: String = task.id): Response<Task>

    // Supprimer une tâche par son ID
    @DELETE("/rest/v2/tasks/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}
