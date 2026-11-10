package org.truthwatchindia.app.data.api

import org.truthwatchindia.app.data.model.WpCategory
import org.truthwatchindia.app.data.model.WpPage
import org.truthwatchindia.app.data.model.WpPost
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class Cf7Response(
    val status: String? = null,
    val message: String? = null
)

interface WordPressApiService {

    @GET("posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 15,
        @Query("page") page: Int = 1,
        @Query("categories") categories: Long? = null,
        @Query("search") search: String? = null,
        @Query("_embed") embed: String? = "1"
    ): Response<List<WpPost>>

    @GET("posts")
    suspend fun getLatestHeadlines(
        @Query("per_page") perPage: Int = 5,
        @Query("_embed") embed: String? = "1"
    ): Response<List<WpPost>>

    @GET("posts/{id}")
    suspend fun getPost(
        @Path("id") id: Long,
        @Query("_embed") embed: String? = "1"
    ): Response<WpPost>

    @GET("categories")
    suspend fun getCategories(
        @Query("per_page") perPage: Int = 50,
        @Query("orderby") orderby: String = "count",
        @Query("order") order: String = "desc"
    ): Response<List<WpCategory>>

    @GET("pages/{id}")
    suspend fun getPage(
        @Path("id") id: Long
    ): Response<WpPage>

    @FormUrlEncoded
    @POST("https://truthwatchindia.org/wp-json/contact-form-7/v1/contact-forms/{id}/feedback")
    suspend fun submitFeedback(
        @Path("id") formId: Long = 6,
        @Field("your-name") name: String,
        @Field("your-email") email: String,
        @Field("your-subject") subject: String,
        @Field("your-message") message: String,
        @Field("_wpcf7_unit_tag") unitTag: String = "wpcf7-f6-o1"
    ): Response<Cf7Response>
}
