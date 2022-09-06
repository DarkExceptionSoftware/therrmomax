package com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.DateModel;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.if_RecycleViewOnClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RecycleViewAdapter_Recipe extends RecyclerView.Adapter<RecycleViewAdapter_Recipe.MyViewHolder> {

    private static if_RecycleViewOnClickListener itemListener;

    private static boolean local = false;

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


    public RecycleViewAdapter_Recipe(Fragment _fra, Context context, ViewMode viewMode, if_RecycleViewOnClickListener recyclerViewClickListener) {

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

        holder.rv_rcp_cv_setday.setVisibility(View.GONE);
        holder.rv_rcp_setday.setVisibility(View.GONE);
        holder.rv_rcp_setday.setText("");

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

        boolean success = true;

        RecipeModel info = Recipes.get(position);
        String get_image_from;
        local = false;

        if (info.getImagePath_internal().equals("")) {
            get_image_from = info.getImagePath();
        } else {
            get_image_from = info.getImagePath_internal();
            local = true;
        }
        Log.d("Gilde", "DVIEW - INT - " + info.getImagePath_internal());
        Log.d("Gilde", "DVIEW - SRC - " + info.getImagePath());

        Glide
                .with(_fra)
                .asBitmap() // There is no such method
                .load(get_image_from)
                .placeholder(R.drawable.img_norecipepic)
                .centerCrop()
                .into(new CustomTarget<Bitmap>(600,600) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {

                        if (!local) {
                            Boolean permission = false;
                            if (ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED) {
                                // You can use the API that requires the permission.
                                permission = true;
                            } else {
                                // You can directly ask for the permission.
                                // The registered ActivityResultCallback gets the result of this request.
                                //MainActivity.requestPermissionLauncher.launch(
                                //       Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            }

                            String int_path = "";

                            if (permission)
                                int_path = saveImage(info.getId() + "", bitmap);   // save your bitmap

                            info.setImagePath_internal(int_path);
                            Log.d("Gilde", "DVIEW - RES - " + int_path);
                        }
                        holder.rv_rcp_imageview.setImageBitmap(bitmap);
                        holder.rv_rcp_imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        /*


        Glide
                .with(_fra)
                .asBitmap().load(get_image_from)
                .centerCrop()
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .listener(new RequestListener<Bitmap>() {


                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object
                                      o, Target<Bitmap> target, boolean b) {

                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap>
                                      target, DataSource dataSource, boolean b) {



                                  return false;
                              }
                          }
                ).
                submit();
                */


    }


    private String saveImage(String name, Bitmap image) {
        String savedImagePath = null;

        String imageFileName = name + ".jpg";

        String ImageDir = context.getApplicationInfo().dataDir + "/files";

        File storageDir = new File(ImageDir, "pictures");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {

            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            // galleryAddPic(savedImagePath);
            //Toast.makeText(getApplicationContext(), "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public int getItemCount() {

        int c = Recipes.size();
        return c;
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
}




