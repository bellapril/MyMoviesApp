package com.example.mymovie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private OnItemClickListener onItemClickListener;

    public MovieAdapter(List<Movie> movies, OnItemClickListener onItemClickListener) {
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.textTitle.setText(movie.getTitle());
        holder.textOverview.setText(movie.getOverview());
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.imagePoster);

        // Set up click listener for item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void addMovies(List<Movie> movies) {
        int startPosition = this.movies.size();
        this.movies.addAll(movies);
        notifyItemRangeInserted(startPosition, movies.size());
    }

    public void setMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textOverview;
        ImageView imagePoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textOverview = itemView.findViewById(R.id.textOverview);
            imagePoster = itemView.findViewById(R.id.imagePoster);
        }
    }
}
