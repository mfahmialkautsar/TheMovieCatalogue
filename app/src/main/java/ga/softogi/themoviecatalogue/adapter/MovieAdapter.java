package ga.softogi.themoviecatalogue.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

import ga.softogi.themoviecatalogue.R;
import ga.softogi.themoviecatalogue.activity.DetailActivity;
import ga.softogi.themoviecatalogue.entity.MovieData;
import ga.softogi.themoviecatalogue.network.NetworkContract;
import ga.softogi.themoviecatalogue.util.CustomOnItemClickListener;

import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<MovieData> movieDataList = new ArrayList<>();

    public MovieAdapter() {
    }

    public ArrayList<MovieData> getMovieDataList() {
        return movieDataList;
    }

    public void setMovieDataList(ArrayList<MovieData> movies) {
        movieDataList.clear();
        movieDataList.addAll(movies);
        notifyDataSetChanged();
    }

    private void addMovie(MovieData item) {
        movieDataList.add(item);
        notifyDataSetChanged();
    }

    public void addAllMovies(ArrayList<MovieData> movieDataList) {
        for (MovieData movieData : movieDataList) {
            addMovie(movieData);
        }
    }

    private void remove(MovieData item) {
        int position = movieDataList.indexOf(item);
        if (position >= 0) {
            movieDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem());
        }
    }

    private MovieData getItem() {
        return movieDataList.get(0);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_model, viewGroup, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {
        MovieData movieData = movieDataList.get(position);
        holder.bindMovie(movieData);

        holder.itemView.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                Uri uri = Uri.parse(CONTENT_URI_MOVIE + "/" + getMovieDataList().get(position).getId());
                intent.putExtra(DetailActivity.EXTRA_MOVIE, movieDataList.get(holder.getAdapterPosition()));
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return movieDataList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvRating;
        ImageView ivThumbnail;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        void bindMovie(MovieData movieData) {
            tvTitle.setText(movieData.getTitle());
            double rating = movieData.getVoteAverage();
            String theRating;

            Picasso.get()
                    .load(NetworkContract.IMG_URL + "w342" + movieData.getPosterPath())
                    .placeholder(itemView.getContext().getResources().getDrawable(R.drawable.ic_loading_24dp))
                    .error(itemView.getContext().getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                    .into(ivThumbnail);

            NumberFormat numberFormat = new DecimalFormat("#.0");
            if (Objects.equals(rating, 0.0)) {
                theRating = itemView.getContext().getString(R.string.no_rating);
            } else {
                theRating = numberFormat.format(rating);
            }
            tvRating.setText(String.format(" %s", theRating));
        }
    }
}
