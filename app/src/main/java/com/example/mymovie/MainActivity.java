package com.example.mymovie;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate; // Import AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;  // Import Switch widget
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String API_KEY = "82ad7384783da317ddb5aa11b2803ec9";
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SearchView searchView;
    private Spinner spinnerCategory;
    private Switch themeSwitch;  // Declare the theme switch variable
    private int currentPage = 1;
    private boolean isLoading = false;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // Jika pertama kali diluncurkan, set layout ke activity_welcome
            setContentView(R.layout.activity_welcome);

            Button btnStart = findViewById(R.id.btnStart);
            btnStart.setOnClickListener(v -> {
                // Setelah klik, tandai bahwa aplikasi sudah dilihat pertama kali
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstLaunch", false);
                editor.apply();

                // Pindah ke halaman utama
                navigateToMainPage();
            });
        } else {
            // Jika bukan pertama kali, langsung ke halaman utama
            navigateToMainPage();
        }
    }

    // Fungsi untuk pindah ke halaman utama
    private void navigateToMainPage() {
        setContentView(R.layout.activity_main); // Pindah ke layout activity_main


        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        spinnerCategory = findViewById(R.id.categorySpinner);
        themeSwitch = findViewById(R.id.themeSwitch);  // Initialize the theme switch

        SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        applyDarkMode(isDarkMode);  // Apply the saved theme

        // Set up the Switch for Dark Mode
        themeSwitch.setChecked(isDarkMode);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            applyDarkMode(isChecked);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
        });

        // Initialize RecyclerView and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(movieAdapter);

        // Set up the Spinner with categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.movie_categories, // Define this in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Fetch movies for the default category (popular)
        fetchMoviesByCategory("popular", currentPage);

        // Spinner Item Selected Listener
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                // Mengambil kategori dari Spinner
                String category = parentView.getItemAtPosition(position).toString().toLowerCase();
                currentPage = 1; // Reset halaman ke 1 ketika kategori berubah
                fetchMoviesByCategory(category, currentPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Jika tidak ada item yang dipilih
            }
        });

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    currentPage = 1;
                    fetchMovies(query, currentPage);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    fetchMoviesByCategory("popular", currentPage); // Reset to popular when search is empty
                }
                return false;
            }
        });
    }

    // Theme Switch functionality (to toggle between light and dark modes)
    private void applyDarkMode(boolean isDarkMode) {
        // Apply Dark Mode or Light Mode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void fetchMoviesByCategory(String category, int page) {
        if (isLoading) return;

        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApiService movieApiService = retrofit.create(MovieApiService.class);
        Call<MovieResponse> call;

        switch (category) {
            case "top rated":
                call = movieApiService.getTopRatedMovies(API_KEY, page);
                break;
            case "upcoming":
                call = movieApiService.getUpcomingMovies(API_KEY, page);
                break;
            case "now playing":
                call = movieApiService.getNowPlayingMovies(API_KEY, page);
                break;
            case "popular":
            default:
                call = movieApiService.getPopularMovies(API_KEY, page);
                break;
        }

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    if (page == 1) {
                        movieAdapter.setMovies(movies); // Replace old movies
                    } else if (!movies.isEmpty()) {
                        movieAdapter.addMovies(movies); // Add more movies
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load movies.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovies(String query, int page) {
        if (isLoading) return;

        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApiService movieApiService = retrofit.create(MovieApiService.class);
        movieApiService.searchMovies(API_KEY, query, page).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    movieAdapter.setMovies(movies);
                } else {
                    Toast.makeText(MainActivity.this, "No movies found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading = false;
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Movie movie) {
        // Handle the movie item click event
        Toast.makeText(this, "Clicked on: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
