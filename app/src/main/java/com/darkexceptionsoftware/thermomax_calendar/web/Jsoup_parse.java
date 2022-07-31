package com.darkexceptionsoftware.thermomax_calendar.web;

import static com.darkexceptionsoftware.thermomax_calendar.MainActivity._RecipeDates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.popup.Confirm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class Jsoup_parse extends AsyncTask<String, Void, String> {

    public RecipeModel newRecipe;
    public Activity returnReference;
    public Activity activity;
    public Context context;
    private int TIMEOUT_IN_MS = 5000;
    private String targetAdress = "";
    private String title = "";
    private TextView textView;

    public Jsoup_parse(Activity _activity) throws MalformedURLException, IOException {
        this.activity = _activity;
        this.context = _activity.getApplicationContext();
    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }

    public void setReturnReference(Activity returnReference) {
        this.returnReference = returnReference;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public String getTargetAdress() {
        return targetAdress;
    }

    public void setTargetAdress(String targetAdress) {
        this.targetAdress = targetAdress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected void onPreExecute() {
        textView.setText("Loading...");
    }

    @Override
    protected String doInBackground(String... urls) {

        String result = "";

        try {

            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            HtmlToPlainText formatter = new HtmlToPlainText();


            Document doc = Jsoup.connect(targetAdress).get();
            doc.outputSettings(new Document.OutputSettings().prettyPrint(false));

            Elements elements;
            String temp = "";

            // update("Scraping Title...");

            String separator = "|", tabseparator = ";", tablineseparator = "#";
            temp = doc.title().substring(17);
            temp = replaceLast(temp, " von ", separator);
            // update("TEST:\n"+ temp);

            String[] split = temp.split(separator);
            List<String> _temp = new LinkedList<String>(Arrays.asList(split));
            List<String> __temp = new LinkedList<String>(Arrays.asList(split));

            temp = temp.replace(" von " + _temp.get(_temp.size() - 1), "");

            result += temp + separator; //Name des Rezepts
            // result += _temp.get(_temp.size()-1) + separator; // Autor des Rezepts
            // update("Scraping Recipe...");

            int i = 0;
            elements = doc.select("p"); // get each element that matches the CSS selector
            for (Element element : elements) {


                String plainText = formatter.getPlainText(element); // format that element to plain text
                plainText = plainText.replace("\n", "");
                plainText = plainText.replace("  ", " ");
                result += plainText + separator;
                //  update("Scraping Recipe (" + i++ +")...\n"+ plainText);

            }

            // table

            // update("Scraping Indrigents...");
            temp = "";
            _temp.clear();


            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");
            String regex = "^[0-9]{1,4}\\.?[0-9]{0,4}?[^@]{0,256}";

            for (Element element : rows) {
                Elements cols = element.select("td");

                for (int q = 0; q < cols.size(); q++) {
                    String plainText = cols.get(q).toString(); // format that element to plain text

                    if (plainText.contains("span>")) {

                        int start = plainText.indexOf("<span>") + 6;
                        int end = plainText.indexOf("</span>");

                        if (start > -1 && start < end)
                            plainText = plainText.substring(start, end);

                    } else {
                        plainText = "etwas";
                    }
                    int x;


                    plainText = plainText.replace("<strong>", "");
                    plainText = plainText.replace("</strong>", "");
                    plainText = plainText.replace("½", ".5");

                    if (plainText.startsWith(".")) {
                        plainText = "0" + plainText;
                    }


                    if (q == 0) {

                        if (plainText.matches(regex)) {

                            plainText = plainText.replaceFirst(" ", ";");

                            if (plainText.substring(plainText.length() - 1).equals(";"))
                                plainText = plainText.substring(0, plainText.length() - 1);
                        } else {
                            if (!plainText.equals(""))
                                plainText = ";" + plainText;
                        }

                    }

                    if (!plainText.equals("")) {

                        if (q < cols.size())
                            result += plainText + tabseparator;
                    }
                }
                result += tablineseparator + "\n";
            }


            result += separator;

            Elements images = doc.getElementsByTag("img");

            for (Element image : images) {
                String source = image.attr("src");

                result += source + "\n\n";
            }


            return result;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.e("JSOUP", exceptionAsString);

        }

        return "";

    }

    public void update(String value) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(5000);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String text = textView.getText().toString();
                                textView.setText(text + "\n\n" + value + "\n\n");
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            ;
        };
        thread.start();
    }

    @Override
    protected void onPostExecute(String result) {
        // process results
        if (result.equals("")) {
            Intent intent = new Intent(context, Confirm.class);
            // TextView editText = (TextView) findViewById(R.id.confirm_label);
            intent.putExtra("action", "question");
            intent.putExtra("pos", 0);

            intent.putExtra("info", "Ein Fehler ist aufgetreten.\nMöchtest du die\nletzte Aktion wiederholen?");
            returnReference.startActivityForResult(intent, 1);
        }
        textView.setText(result);
        try {
            CreateRecipe(result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.e("JSOUP", exceptionAsString);
        }
        // FloatingActionButton fab = activity.findViewById(R.id.fab);

        // fab.setVisibility(View.VISIBLE);
    }

    public void CreateRecipe(String _input) throws MalformedURLException, PackageManager.NameNotFoundException {

        String[] _field = _input.split("\\|");

        newRecipe = new RecipeModel(returnReference.getApplication().getApplicationContext());

        // ID is time in seconds
        Time time = new Time();
        time.setToNow();
        newRecipe.setId(time.toMillis(false));

        // save the URL the recipe is created from
        newRecipe.setUrl(targetAdress);

        // Set doctitle to name
        newRecipe.setName(_field[0].trim());

        // Set autor
        newRecipe.setCreator(_field[1].trim());

        // Set the method
        newRecipe.setSummary(_field[2]);

        // Set the table
        List<Indrigent> indrigentslist = newRecipe.getINDRIGENTS();

        String table = _field[3].replace("\n", "");
        table = table.replace(";;", ";");
        String[] _table = table.split("#");
        for (int i = 0; i < _table.length; i++) {

            if (_table[i].length() < 4)
                continue;

            String[] row = _table[i].split(";");

            Log.d("PARSE", "<" + row.toString() + ">");

            Float amount;
            String amountOf;
            String name;

            if (row[0].equals("")) {

                try {
                    amount = Float.parseFloat(row[1].trim());
                    amountOf = "X";
                    name = row[row.length - 1].trim();

                } catch (NumberFormatException e) {
                    amount = 0f;
                    amountOf = row[0].trim();
                    name = row[1].trim();

                }
            }else{
                amount = Float.parseFloat(row[0].trim());

                if (row[row.length - 1].trim().equals("")){
                    name = row[1].trim();
                    amountOf = "x";
                }

                amountOf = row[1].trim();
                name = row[row.length-1].trim();
            }

            Indrigent indrigent = new Indrigent();
            indrigent.setAmount(amount);
            indrigent.setAmountof(amountOf);
            indrigent.setName(name);


            indrigentslist.add(indrigent);
        }


        // Set image url
        newRecipe.setImagePath(_field[4].trim());
        String RecDir = context.getApplicationInfo().dataDir + "/files/";
        File file = new File(RecDir + +newRecipe.getId() + ".rcp");
        if (file.exists())
            file.delete();

        newRecipe.serialize(RecDir, "" + newRecipe.getId() + ".rcp");
    }
}









