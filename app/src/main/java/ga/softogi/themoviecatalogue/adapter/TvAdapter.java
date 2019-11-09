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
import ga.softogi.themoviecatalogue.entity.TvData;
import ga.softogi.themoviecatalogue.network.NetworkContract;
import ga.softogi.themoviecatalogue.util.CustomOnItemClickListener;

import static ga.softogi.themoviecatalogue.db.FavDatabaseContract.TableColumns.CONTENT_URI_TV;

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

    private void remove(TvData item) {
        int position = tvDataList.indexOf(item);
        if (position >= 0) {
            tvDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem());
        }
    }

    private TvData getItem() {
        return tvDataList.get(0);
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

                Uri uri = Uri.parse(CONTENT_URI_TV + "/" + getTvDataList().get(position).getId());
                intent.putExtra(DetailActivity.EXTRA_TV, tvDataList.get(holder.getAdapterPosition()));
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return tvDataList.size();
    }

    class TvViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvRating;
        ImageView ivThumbnail;

        TvViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
        }

        void bindTv(TvData tvData) {
            tvTitle.setText(tvData.getName());
            double rating = tvData.getVoteAverage();
            String theRating;

            Picasso.get()
                    .load(NetworkContract.IMG_URL + "w342" + tvData.getPosterPath())
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
