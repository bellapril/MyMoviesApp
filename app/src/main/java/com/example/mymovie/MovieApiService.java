package com.example.mymovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiService {

    // Endpoint untuk mendapatkan film populer
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    // Endpoint untuk pencarian film berdasarkan query
    @GET("search/movie")
    Call<MovieResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page
    );

    // Endpoint untuk mendapatkan film terbaru
    @GET("movie/latest")
    Call<MovieResponse> getLatestMovies(
            @Query("api_key") String apiKey
    );

    // Endpoint untuk mendapatkan film berdasarkan kategori (misalnya, genre)
    @GET("discover/movie")
    Call<MovieResponse> getMoviesByCategory(
            @Query("api_key") String apiKey,
            @Query("with_genres") String genreId, // ID genre
            @Query("page") int page
    );

    // Endpoint untuk mendapatkan film dengan rating tertinggi
    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    // Endpoint untuk mendapatkan film yang akan datang
    @GET("movie/upcoming")
    Call<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    // Endpoint untuk mendapatkan film yang sedang tayang
    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );
}
