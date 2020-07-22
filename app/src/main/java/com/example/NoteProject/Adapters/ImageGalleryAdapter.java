package com.example.NoteProject.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.NoteProject.R;

import java.util.ArrayList;



public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryViewHolder> {
    Activity mContext;
    ArrayList image_path;

    public ImageGalleryAdapter(Activity context, ArrayList image_path) {
        mContext = context;
        this.image_path = image_path;
    }

    @Override
    public ImageGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.image_gallery_row, parent, false);
        return new ImageGalleryViewHolder(rootView);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ImageGalleryViewHolder holder, final int position) {

        if (position == 0) {
            holder.custom_image_gallery.setScaleType(ImageView.ScaleType.CENTER);
        } else {
            holder.custom_image_gallery.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext)
                    .load(image_path.get(position))
                    .into(holder.custom_image_gallery);
        }


    }

    @Override
    public int getItemCount() {
        return image_path.size();
    }

    public class ImageGalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView custom_image_gallery;

        public ImageGalleryViewHolder(View itemView) {
            super(itemView);
            custom_image_gallery = itemView.findViewById(R.id.custom_image_gallery);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() == 0) {
                      //  ((ImageActivity) mContext).GetClickedImage();
                    } else {
                        String imagePath = image_path.get(getAdapterPosition()).toString();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("gallery", imagePath);
                        mContext.setResult(Activity.RESULT_CANCELED, returnIntent);
                        mContext.finish();
                    }
                }
            });
        }
    }
}
