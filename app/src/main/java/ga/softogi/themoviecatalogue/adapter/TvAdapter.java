package ga.softogi.themoviecatalogue.adapter;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import ga.softogi.themoviecatalogue.entity.TvData;
import ga.softogi.themoviecatalogue.network.NetworkContract;
import ga.softogi.themoviecatalogue.util.CustomOnItemClickListener;

import static android.provider.BaseColumns._ID;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.BACKDROP_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.GENRE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.OVERVIEW;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.POSTER_PATH;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RATING;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RELEASE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.RUNTIME;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TITLE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.TYPE;

public class TvAdapter extends RecyclerView.Adapter<TvAdapter.TvViewHolder> {
    private ArrayList<TvData> tvDataList = new ArrayList<>();

    public TvAdapter() {
    }

    public ArrayList<TvData> getTvDataList() {
        return tvDataList;
    }

    public void setTvDataList(ArrayList<TvData> tvs) {
        tvDataList.clear();
        tvDataList.addAll(tvs);
        notifyDataSetChanged();
    }

    private void addTv(TvData tvData) {
        tvDataList.add(tvData);
        notifyDataSetChanged();
    }

    public void addAllTvs(ArrayList<TvData> tvDataList) {
        for (TvData tvData : tvDataList) {
            addTv(tvData);
        }
    }

    public void remove(TvData item) {
        int position = tvDataList.indexOf(item);
        if (position >= 0) {
            tvDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public TvData getItem(int position) {
        return tvDataList.get(position);
    }

    @NonNull
    @Override
    public TvViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_model, viewGroup, false);
        return new TvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TvViewHolder holder, int position) {
        TvData tvData = tvDataList.get(position);
        holder.bindTv(tvData);

        holder.itemView.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
//                Intent favIntent = new Intent(holder.itemView.getContext(), DetailFavActivity.class);

                Uri uri = Uri.parse(CONTENT_URI_TV + "/" + getTvDataList().get(position).getId());
//                String[] projection = {
//                        _ID, TITLE, OVERVIEW, RELEASE, GENRE, RUNTIME, RATING, POSTER_PATH, BACKDROP_PATH, TYPE
//                };
                intent.putExtra(DetailActivity.EXTRA_TV, tvDataList.get(holder.getAdapterPosition()));
                intent.setData(uri);
//                String selection = _ID + " =?";
//                String[] selectionArgs = {String.valueOf(getTvDataList().get(position).getId())};
//                Cursor cursor = holder.itemView.getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
//                if (cursor != null && cursor.getCount() > 0) {
//                    favIntent.setData(uri);
//                    cursor.close();
//                    holder.itemView.getContext().startActivity(favIntent);
//                } else {
                    holder.itemView.getContext().startActivity(intent);
//                }
            }
        }));
    }

    @Override
    public int getItemCount() {
        return tvDataList.size();
    }

    public class TvViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        //        TextView tvOverview;
//        TextView tvRelease;
        TextView tvRating;
        ImageView ivThumbnail;

        public TvViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
//            tvOverview = itemView.findViewById(R.id.tv_overview);
//            tvRelease = itemView.findViewById(R.id.tv_release);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        void bindTv(TvData tvData) {
            tvTitle.setText(tvData.getName());
            String overview = tvData.getOverview();
            String theOverview;
            double rating = tvData.getVoteAverage();
            String theRating;

            Picasso.get()
                    .load(NetworkContract.IMG_URL + "w342" + tvData.getPosterPath())
                    .placeholder(itemView.getContext().getResources().getDrawable(R.drawable.ic_loading_24dp))
                    .error(itemView.getContext().getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                    .into(ivThumbnail);

            if (TextUtils.isEmpty(overview)) {
                theOverview = itemView.getContext().getString(R.string.overview_not_found);
            } else {
                theOverview = overview;
            }

            NumberFormat numberFormat = new DecimalFormat("#.0");
            if (Objects.equals(rating, 0.0)) {
                theRating = itemView.getContext().getString(R.string.no_rating);
            } else {
                theRating = numberFormat.format(rating / 2) + "/5";
            }
//            tvOverview.setText(theOverview);
            tvRating.setText(String.format(" %s", theRating));

//            String release = tvData.getFirstAirDate();
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//            try {
//                Date date = dateFormat.parse(release);
//                @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy");
//                String releaseDate = newDateFormat.format(date);
////                tvRelease.setText(releaseDate);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
        }
    }
}
