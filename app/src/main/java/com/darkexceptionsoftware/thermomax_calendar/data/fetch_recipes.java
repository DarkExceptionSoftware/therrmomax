package com.darkexceptionsoftware.thermomax_calendar.data;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import com.darkexceptionsoftware.thermomax_calendar.MainActivity;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.ui.kochbuch.RecycleViewAdapter_Recipe;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class  fetch_recipes extends AsyncTask<String, Void, String> {

    Activity activityReference;
    RecycleViewAdapter_Recipe rva;
    List<RecipeModel> Recipes;

    ProgressDialog  dialog;
    String RecDir;
    File directory;
    List<String> files;


    public fetch_recipes(Activity activityReference, RecycleViewAdapter_Recipe rva,    List<RecipeModel> Recipes) {
        this.activityReference = activityReference;
        this.rva = rva;
        this.Recipes = Recipes;
    }

    @Override
    protected void onPreExecute() {



        RecDir = activityReference.getApplicationContext().getApplicationInfo().dataDir + "/files/";
        File directory = new File(RecDir);


        files = Arrays.asList(directory.list());

        MainActivity ref = (MainActivity) activityReference;
        Collections.sort(files,Collections.reverseOrder());

        if (!files.equals(ref.old_files)){
            dialog = new ProgressDialog(activityReference);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
        dialog.setTitle("Loading " + files.size() + " Recipes...");
        dialog.setMax(files.size());
        dialog.show();

        }


    }

    @Override
    protected String doInBackground(String... strings) {

        MainActivity ref = (MainActivity) activityReference;

        if (files.equals(ref.old_files))
            return null;

        ref.old_files = files;
        RecipeModel recipe = null;
        Recipes.clear();
        HashMap recippemap = MainActivity.getRecipeMap();

        String result = "";
        recippemap.clear();



        for (String recipeitem : files){

            dialog.incrementProgressBy(1);
            try {
                recipe = new RecipeModel(activityReference.getApplicationContext());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (!recipeitem.contains(".rcp"))
                continue;

            Boolean success = recipe.deserialize(RecDir,recipeitem);

            if (success){
                Recipes.add(recipe);
                recippemap.put(recipe.getId(),recipe );
            }else{
                recipe.delete(RecDir,recipeitem);

            }

        }
        new Thread(){
            public void run(){
                activityReference.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (rva != null){
                            rva.notifyDataSetChanged();

                        RecyclerView rv = activityReference.findViewById(R.id.rcv_cookbook);

                        rv.invalidate();}
                    }
                });
            }
        }.start();
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (dialog != null)
        dialog.dismiss();

    }
}
