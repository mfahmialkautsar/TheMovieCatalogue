package ga.softogi.themoviecatalogue.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import ga.softogi.themoviecatalogue.entity.ContentItem;
import ga.softogi.themoviecatalogue.CustomOnItemClickListener;
import ga.softogi.themoviecatalogue.activity.DetailContentActivity;
import ga.softogi.themoviecatalogue.R;

import static ga.softogi.themoviecatalogue.activity.DetailContentActivity.EXTRA_CONTENT;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private ArrayList<ContentItem> mData = new ArrayList<>();

    public ArrayList<ContentItem> getData() {
        return mData;
    }

    public void setData(ArrayList<ContentItem> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_model, viewGroup, false);
        return new ContentViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentViewHolder holder, int position) {
        holder.bind(getData().get(position));

        holder.itemView.setOnClickListener(new CustomOnItemClickListener(position, new CustomOnItemClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int position) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailContentActivity.class);

                Uri uri = null;
                Uri uriMovie = Uri.parse(CONTENT_URI_MOVIE + "/" + getData().get(position).getId());
                Uri uriTv = Uri.parse(CONTENT_URI_TV + "/" + getData().get(position).getId());
                if (Objects.equals(mData.get(position).getType(), "Movie")) {
                    uri = uriMovie;
                } else if (Objects.equals(mData.get(position).getType(), "TV")) {
                    uri = uriTv;
                }
                intent.setData(uri);
                intent.putExtra("extra_position", position);
                intent.putExtra(EXTRA_CONTENT, mData.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvOverview;
        TextView tvRelease;
        TextView tvRating;
        ImageView ivThumbnail;
        Button favorite;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvOverview = itemView.findViewById(R.id.tv_overview);
            tvRelease = itemView.findViewById(R.id.tv_release);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            favorite = itemView.findViewById(R.id.favorite);
        }

        void bind(ContentItem content) {
            tvTitle.setText(content.getTitle());
            String overview = content.getOverview();
            String theOverview;
            String rating = content.getRating();
            String theRating;

            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500/" + content.getPosterPath())
                    .placeholder(itemView.getContext().getResources().getDrawable(R.drawable.ic_loading_24dp))
                    .error(itemView.getContext().getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                    .into(ivThumbnail);

            if (TextUtils.isEmpty(overview)) {
                theOverview = itemView.getContext().getString(R.string.overview_not_found);
            } else {
                theOverview = overview;
            }

            if (Objects.equals(rating, "0")) {
                theRating = itemView.getContext().getString(R.string.no_rating);
            } else {
                theRating = rating;
            }
            tvOverview.setText(theOverview);
            tvRating.setText(String.format(" %s", theRating));

            String release = content.getRelease();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date = dateFormat.parse(release);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy");
                String releaseDate = newDateFormat.format(date);
                tvRelease.setText(releaseDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
