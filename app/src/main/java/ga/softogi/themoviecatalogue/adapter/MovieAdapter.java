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
    private MovieData movieData;
//    private TvData tvData;

    private ArrayList<MovieData> movieDataList = new ArrayList<>();

    public MovieAdapter() {
    }

    public ArrayList<MovieData> getMovieDataList() {
        return movieDataList;
    }

//    private ArrayList<TvData> tvDataList = new ArrayList<>();
//
//    public ArrayList<TvData> getTvDataList() {
//        return tvDataList;
//    }
//
//    public void setTvDataList(ArrayList<TvData> tvs) {
//        tvDataList.clear();
//        tvDataList.addAll(tvs);
//        notifyDataSetChanged();
//    }
//    private OnMovieItemSelectedListener onMovieItemSelectedListener;

    public void setMovieDataList(ArrayList<MovieData> movies) {
        movieDataList.clear();
        movieDataList.addAll(movies);
        notifyDataSetChanged();
    }

    private void addMovie(MovieData item) {
        movieDataList.add(item);
        notifyDataSetChanged();
//        notifyItemInserted(movieDataList.size() - 1);
    }

//    private void addTv(TvData tv) {
//        tvDataList.add(tv);
//    }

    public void addAllMovies(ArrayList<MovieData> movieDataList) {
        for (MovieData movieData : movieDataList) {
            addMovie(movieData);
        }
    }

//    public void addAllTvs(ArrayList<TvData> tvDataList) {
//        for (TvData tvData : tvDataList) {
//            addTv(tvData);
//        }
//    }

    public void remove(MovieData item) {
        int position = movieDataList.indexOf(item);
        if (position >= 0) {
            movieDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public MovieData getItem(int position) {
        return movieDataList.get(position);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_model, viewGroup, false);
//        final TvViewHolder movieViewHolder = new TvViewHolder(view);
//        movieViewHolder.itemView.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
//            @Override
//            public void onItemClicked(View view, int position) {
//                int adapterPos = movieViewHolder.getAdapterPosition();
//                if (adapterPos != RecyclerView.NO_POSITION) {
//                    if (onMovieItemSelectedListener != null) {
//                        onMovieItemSelectedListener.onItemClick(movieViewHolder.itemView, adapterPos);
//                    }
//                }
//            }
//        }));
        return new MovieViewHolder(view);
    }

    public int position;

    public int getThePosition() {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, int position) {

//        if (Objects.equals(movieDataList.get(position).getType(), ContentItem.TYPE_MOVIE)) {
            movieData = movieDataList.get(position);
            holder.bindMovie(movieData);
//        }

//        if (Objects.equals(tvDataList.get(position).getType(), ContentItem.TYPE_TV)) {
//            tvData = tvDataList.get(position);
//            holder.bindTv(tvData);
//        }

        holder.itemView.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                MovieAdapter.this.position = position;
//                Intent favIntent = new Intent(holder.itemView.getContext(), DetailFavActivity.class);

//                Uri uri = null;
                Uri uri = Uri.parse(CONTENT_URI_MOVIE + "/" + getMovieDataList().get(position).getId());
//                String[] projection = {
//                        _ID, TITLE, OVERVIEW, RELEASE, GENRE, RUNTIME, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
//                };
//                    uri = uriMovie;
//                } else if (Objects.equals(tvDataList.get(position).getType(), ContentItem.TYPE_TV)) {
//                    intent.putExtra(DetailActivity.EXTRA_TV, tvDataList.get(holder.getAdapterPosition()));
//                    uri = uriTv;
//                }
//                String selection = _ID + " =?";
//                String[] selectionArgs = {String.valueOf(getMovieDataList().get(position).getId())};
//                Cursor cursor = holder.itemView.getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
//                //                Uri uriTv = Uri.parse(CONTENT_URI_TV + "/" + getMovieDataList().get(position).getId());
//                //                if (Objects.equals(movieDataList.get(position).getType(), ContentItem.TYPE_MOVIE)) {
//                if (cursor != null && cursor.getCount() > 0) {
//                    favIntent.setData(uri);
//                    cursor.close();
//                    holder.itemView.getContext().startActivity(favIntent);
//                } else {
                    intent.putExtra(DetailActivity.EXTRA_MOVIE, movieDataList.get(holder.getAdapterPosition()));
                    intent.setData(uri);
                    holder.itemView.getContext().startActivity(intent);
//                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return movieDataList.size();
    }

//    public void setOnMovieItemSelectedListener(OnMovieItemSelectedListener onMovieItemSelectedListener) {
//        this.onMovieItemSelectedListener = onMovieItemSelectedListener;
//    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvRating;
        ImageView ivThumbnail;

        public MovieViewHolder(@NonNull View itemView) {
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

//    public interface OnMovieItemSelectedListener {
//        void onItemClick(View view, int position);
//    }
}
