package com.darkexceptionsoftware.thermomax_calendar.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.LogRecord;

public class RecycleViewAdapter_Recipe extends RecyclerView.Adapter<RecycleViewAdapter_Recipe.MyViewHolder> {

    private static RecycleViewOnClickListener itemListener;


    Handler mainHandler = new Handler();

    Context context;
    ArrayList<RecipeModel> Recipes;
    RecyclerView.SmoothScroller smoothScroller;

    Fragment _fra;
    Date today = new Date();

    public RecyclerView.SmoothScroller getSmoothScroller() {
        return smoothScroller;
    }


    public RecycleViewAdapter_Recipe(Fragment _fra, Context context, ArrayList<RecipeModel> Recipes, RecycleViewOnClickListener recyclerViewClickListener) {

        this.context = context;
        this.Recipes = Recipes;
        this._fra = _fra;

        this.itemListener = recyclerViewClickListener;

        smoothScroller = new LinearSmoothScroller(context) {

            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;

            }
        };
    }


    @NonNull
    @Override
    public RecycleViewAdapter_Recipe.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.rcv_row_recipe, parent, false);
        return new RecycleViewAdapter_Recipe.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter_Recipe.MyViewHolder holder, int position) {

        //holder.date = Recipes.get(position).getDate();

        holder.rv_rcp_recipe.setText(Recipes.get(position).getName());
        holder.rv_rcp_autor.setText(Recipes.get(position).getCreator());

        String url = Recipes.get(position).getImagePath();


        Glide
                .with(_fra)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.sample)
                .into(holder.rv_rcp_imageview);



    }

    @Override
    public int getItemCount() {
        return Recipes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView rv_rcp_recipe, rv_rcp_autor;
        ImageView rv_rcp_imageview;

        CardView rv_rcp_cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_rcp_recipe = itemView.findViewById(R.id.rv_rcp_recipe);
            rv_rcp_autor = itemView.findViewById(R.id.rv_rcp_autor);
            rv_rcp_imageview = itemView.findViewById(R.id.rv_rcp_imageview);

            rv_rcp_cardView = itemView.findViewById((R.id.rv_rcp_cardView));

            rv_rcp_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(getAdapterPosition(), "show");
                }
            });
        }


    }
    class FetchImage extends Thread{

        String url;
        Bitmap bitmap;
        ImageView position;

        FetchImage(String url, ImageView position){

            this.position = position;
            this.url = url;

        }

        @Override
        public void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                   // progressDialog = new ProgressDialog(MainActivity.this);
                   //progressDialog.setMessage("Getting your pic....");
                   // progressDialog.setCancelable(false);
                   // progressDialog.show();
                }
            });

            InputStream inputStream = null;
            try {
                inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                ImageView p = position;
                p.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                   // if (progressDialog.isShowing())
                   //     progressDialog.dismiss();
                   // binding.imageView.setImageBitmap(bitmap);

                }
            });




        }
    }
}




