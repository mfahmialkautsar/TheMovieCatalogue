package mfahmialkautsar.thefavoritemovie.adapter;

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

import java.util.ArrayList;
import java.util.Objects;

import mfahmialkautsar.thefavoritemovie.CustomOnItemClickListener;
import mfahmialkautsar.thefavoritemovie.R;
import mfahmialkautsar.thefavoritemovie.activity.DetailContentActivity;
import mfahmialkautsar.thefavoritemovie.entity.ContentItem;

import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.CONTENT_URI_MOVIE;
import static mfahmialkautsar.thefavoritemovie.db.DatabaseContract.TableColumns.CONTENT_URI_TV;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private ArrayList<ContentItem> mData = new ArrayList<>();

    public ContentAdapter() {
    }

    public ArrayList<ContentItem> getData() {
        return mData;
    }

    public void setData(ArrayList<ContentItem> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    private void remove(ContentItem item) {
        int position = mData.indexOf(item);
        if (position >= 0) {
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem());
        }
    }

    private ContentItem getItem() {
        return mData.get(0);
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
                if (Objects.equals(mData.get(position).getType(), ContentItem.TYPE_MOVIE)) {
                    uri = uriMovie;
                } else if (Objects.equals(mData.get(position).getType(), ContentItem.TYPE_TV)) {
                    uri = uriTv;
                }
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvRating;
        ImageView ivThumbnail;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        void bind(ContentItem content) {
            tvTitle.setText(content.getTitle());
            double rating = content.getRating();
            String theRating;

            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w342" + content.getPosterPath())
                    .placeholder(itemView.getContext().getResources().getDrawable(R.drawable.ic_loading_24dp))
                    .error(itemView.getContext().getResources().getDrawable(R.drawable.ic_error_outline_black_24dp))
                    .into(ivThumbnail);

            if (Objects.equals(rating, 0.0)) {
                theRating = itemView.getContext().getString(R.string.no_rating);
            } else {
                theRating = String.valueOf(rating);
            }
            tvRating.setText(String.format(" %s", theRating));
        }
    }
}
