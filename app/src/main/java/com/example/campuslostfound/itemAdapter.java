package com.example.campuslostfound;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.ViewHolder> {

    Context context;
    List<itemModel> itemList;

    public itemAdapter(Context context, List<itemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        itemModel item = itemList.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvLocation.setText("Location: " + item.getLocation());
        holder.tvStatus.setText(item.getStatus());

        // Base64 image ko wapas Bitmap mein convert karke ImageView mein show karna
        String imgStr = item.getImageBase64();
        if (imgStr != null && !imgStr.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(imgStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.ivItem.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.ivItem.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.ivItem.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("desc", item.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLocation, tvStatus;
        ImageView ivItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivItem = itemView.findViewById(R.id.ivItem);
        }
    }
}