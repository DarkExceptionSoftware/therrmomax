package com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.DateModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecycleViewOnClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecycleViewAdapter_Recipe extends RecyclerView.Adapter<RecycleViewAdapter_Recipe.MyViewHolder> {

    private static RecycleViewOnClickListener itemListener;

    public static enum ViewMode {
        RECIPEBOOK("Recipebook", 0),
        WEEKLIST("Weeklist", 1);

        private String stringValue;
        private int intValue;
        private void Gender(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        ViewMode(String male, int i) {
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }

    Calendar c;
    List<String> Dates;
    List<DateModel> week_dm;
    Handler mainHandler = new Handler();

    Context context;
    ArrayList<RecipeModel> Recipes;
    ArrayList<DateModel> Recipes_week;
    RecyclerView.SmoothScroller smoothScroller;
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatDay = new SimpleDateFormat("EE");

    Fragment _fra;
    Date today = new Date();

    MainActivity ref;
    public RecyclerView.SmoothScroller getSmoothScroller() {
        return smoothScroller;
    }


    public RecycleViewAdapter_Recipe(Fragment _fra, Context context, ViewMode viewMode, RecycleViewOnClickListener recyclerViewClickListener) {

        this.context = context;
        this.Recipes_week = MainActivity.get_RecipeDates_week();

        if (viewMode == ViewMode.RECIPEBOOK) {
            this.Recipes = MainActivity.get_RecipeModel();
        }

        if (viewMode == ViewMode.WEEKLIST){
            Recipes = new ArrayList<RecipeModel>();
            for (DateModel dItem : Recipes_week){
                Long Id = dItem.getModelID();
                HashMap recipeMap = MainActivity.getRecipeMap();
                RecipeModel recToAdd = (RecipeModel) recipeMap.get(Id);

                if (recToAdd != null){
                    Recipes.add(recToAdd);
                }
            }
            this.Recipes = Recipes;
        }

            this.Recipes = Recipes;

        this._fra = _fra;

        ref =  (MainActivity) _fra.getActivity();
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


        for (DateModel ditem : Recipes_week){
            if (ditem.getModel() == Recipes.get(position).getId()){
                holder.rv_rcp_cv_setday.setVisibility(View.VISIBLE);
                holder.rv_rcp_setday.setVisibility(View.VISIBLE);
                holder.rv_rcp_setday.setText(ditem.getDay());
            break;
            }
        }



        holder.rv_rcp_recipe.setText(Recipes.get(position).getName());
        if (Recipes.get(position).getName().length() > 20)
            holder.rv_rcp_recipe.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

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

        TextView rv_rcp_recipe, rv_rcp_autor, rv_rcp_setday;
        ImageView rv_rcp_imageview;

        CardView rv_rcp_cardView, rv_rcp_cv_setday;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_rcp_recipe = itemView.findViewById(R.id.rv_rcp_recipe);
            rv_rcp_autor = itemView.findViewById(R.id.rv_rcp_autor);
            rv_rcp_imageview = itemView.findViewById(R.id.rv_rcp_imageview);

            rv_rcp_setday = itemView.findViewById(R.id.rcp_dayset);
            rv_rcp_cv_setday = itemView.findViewById(R.id.rcp_cv_day);
            rv_rcp_cardView = itemView.findViewById((R.id.rv_rcp_cardView));

            rv_rcp_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onItemClick(getAdapterPosition(), "show");
                }
            });
            rv_rcp_cardView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    itemListener.onItemlongClick(getAdapterPosition(), "show");
                    return true;
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




