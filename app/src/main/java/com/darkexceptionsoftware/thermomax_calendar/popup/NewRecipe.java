package com.darkexceptionsoftware.thermomax_calendar.popup;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.Time;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.darkexceptionsoftware.thermomax_calendar.OnSwipeTouchListener;
import com.darkexceptionsoftware.thermomax_calendar.R;
import com.darkexceptionsoftware.thermomax_calendar.data.Indrigent;
import com.darkexceptionsoftware.thermomax_calendar.data.RecipeModel;
import com.darkexceptionsoftware.thermomax_calendar.data.if_IOnBackPressed;
import com.darkexceptionsoftware.thermomax_calendar.databinding.FragmentEditrecipeBinding;
import com.darkexceptionsoftware.thermomax_calendar.web.Jsoup_parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRecipe extends AppCompatActivity implements if_IOnBackPressed, View.OnTouchListener {
    boolean previewmode = false;
    int previousSpinnerState = 0;
    String url;
    boolean hasscrapedata;
    List<String> ScrapedImgUrls = new ArrayList<>();
    int ScrapedImgUrls_position = 0;
    List<String> ScrapedTables = new ArrayList<>();
    int ScrapedTables_position = 0;
    List<String> ScrapedDiv = new ArrayList<>();
    int ScrapedDiv_position = 0;
    List<String> ScrapedSummary = new ArrayList<>();
    int ScrapedSummary_position = 0;
    Thread thread;
    private Activity activityReference;
    private FragmentEditrecipeBinding binding;
    private boolean untereinander = false;
    private RecipeModel info;
    private boolean local = false;
    private List<Indrigent> _indrigents;
    private long recipeid = 0;
    private int position;
    private boolean isediting = false;
    private Intent returnIntent = new Intent();
    private Document doc, doc_stripped;

    private String html = "";

    public NewRecipe() {
    }

    public NewRecipe(Activity _activityReference) {

        this.activityReference = _activityReference;
    }

    public static String getBaseUrl(String urlString) {

        if (urlString == null) {
            return null;
        }

        try {
            URL url = URI.create(urlString).toURL();
            return url.getProtocol() + "://" + url.getAuthority() + "/";
        } catch (Exception e) {
            return null;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        if (imm.isAcceptingText())
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


    }

    public void updateimage(String get_image_from, boolean save) {
        Glide
                .with(this)
                .asBitmap()
                .load(get_image_from)
                //.centerCrop()
                .into(new CustomTarget<Bitmap>(600, 600) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {

                        if (!local) {
                            boolean permission = ContextCompat.checkSelfPermission(
                                    getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_GRANTED;
                            // You can use the API that requires the permission.

                            String int_path;

                            if (permission && save) {
                                int_path = saveImage(info.getId() + "", bitmap);   // save your bitmap
                                info.setImagePath_internal(int_path);
                            }
                        }
                        binding.nrFieldImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.activityReference = this;
        binding = FragmentEditrecipeBinding.inflate(getLayoutInflater());

        String action;
        _indrigents = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            action = intent.getStringExtra("action");


            if (action.equals("parseany")) {
                url = intent.getStringExtra("result");

                StringBuilder text = new StringBuilder();
                File file = new File(getApplication().getFilesDir(), "htmltenp.txt");
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();

                    html = text.toString();
                } catch (IOException e) {
                    html = e.toString();
                }

                NewRecipe newRecipe = this;

                thread = new Thread() {
                    public void run() {
                        Jsoup_parse jparse;

                        try {
                            jparse = new Jsoup_parse(activityReference);

                            doc = jparse.getResultDoc(url);
                            doc_stripped = jparse.fromhtml(html);

                            hasscrapedata = true;
                            Activity ref = NewRecipe.this;
                            ref.runOnUiThread(newRecipe::onJsuopResult);
                            super.run();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }


            if (action.equals("edit")) {


                isediting = true;
                info = intent.getParcelableExtra("info");
                position = intent.getIntExtra("pos", -1);

                _indrigents = info.getINDRIGENTS();

                url = info.getUrl();
                binding.nrFieldName.setText(info.getName());
                binding.nrFieldAutor.setText(info.getCreator());

                String get_image_from;
                local = false;
                if (info.getImagePath_internal().equals("")) {
                    get_image_from = info.getImagePath();
                } else {
                    get_image_from = info.getImagePath_internal();
                    local = true;
                }
                updateimage(get_image_from, true);


                binding.nrFieldSummary.setText(info.getSummary());

                String result = "";
                for (Indrigent ind : info.getINDRIGENTS())
                    result += ind.getAmountString() + " " + ind.getAmountof() + " " + ind.getName() + "\n";

                binding.nrFieldZutaten.setText(result);

                recipeid = info.getId();
            }
        }

        if (intent != null) {
            position = intent.getIntExtra("pos", -1);
            action = intent.getStringExtra("action");
            if (action.equals("editRecipe")) {
                info = intent.getParcelableExtra("info");
                _indrigents = info.getINDRIGENTS();
            }


        }
        // switch

        Switch _switch = binding.switch1;
        Spinner spinner = binding.spinner;

        _switch.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                untereinander = true;
                spinner.setVisibility(View.VISIBLE);
                binding.spinnerText.setVisibility(View.VISIBLE);
            } else {
                untereinander = false;
                spinner.setVisibility(View.GONE);
                binding.spinnerText.setVisibility(View.GONE);

            }
        });

        // spinner
        List<String> list = new ArrayList<>();
        for (int i = 2; i <= 3; i++) {
            list.add(i + "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        binding.nrButtonTowebview.setOnClickListener(view -> {
            returnIntent = new Intent();
            returnIntent.putExtra("action", "tobrowser");
            returnIntent.putExtra("url", url);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
        binding.nrFieldImage.setOnClickListener(view -> startActivityForResult(getPickImageChooserIntent(), 200));
        binding.nrFieldImage.setOnLongClickListener(view -> {
            Bitmap bitmap = ((BitmapDrawable) binding.nrFieldImage.getDrawable()).getBitmap();
            String name = binding.nrFieldName.getText().toString();
            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Image title.jpg", null);
            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();

            return true;
        });
        binding.nrButtonPreview.setOnClickListener(view -> {
            if (previewmode) {
                previewmode = false;

                binding.nrWebview.setVisibility(View.GONE);
                binding.nrFieldZutaten.setVisibility(View.VISIBLE);
                binding.switch1.setVisibility(View.VISIBLE);
                binding.spinnerText.setVisibility(previousSpinnerState);
                binding.spinner.setVisibility(previousSpinnerState);
                binding.nrButtonPreview.setText(R.string.nr_desc_preview_on);
                binding.nrButtonSubmit.setVisibility(View.GONE);
            } else {
                previousSpinnerState = binding.spinner.getVisibility();
                previewmode = true;
                binding.nrWebview.setVisibility(View.VISIBLE);
                binding.nrFieldZutaten.setVisibility(View.GONE);
                binding.switch1.setVisibility(View.GONE);
                binding.spinnerText.setVisibility(View.GONE);
                binding.spinner.setVisibility(View.GONE);
                binding.nrButtonPreview.setText(R.string.nr_desc_preview_off);
                binding.nrButtonSubmit.setVisibility(View.VISIBLE);


                result_from_field();


                String builder2 = "<!DOCTYPE html>\n" +
                        "<html><head>\n" +
                        "<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">\n" +
                        "<title>Ohne_Titel_1</title>\n" +
                        "<link href=\"file:///android_asset/table_style.css\" rel=\"stylesheet\">" +
                        "</head><body>\n" +
                        "<table class=\"content-table\">\n" +
                        "<thead>" +
                        "<tr>" +
                        "<th><h2>Mg.</h2></th>" +
                        "<th><h2>Eh.</h2></th>" +
                        "<th><h2>Zutat</h2></th></tr></thead>";


                for (Indrigent item : _indrigents) {
                    builder2 += "<tr>";

                    String _t = item.getAmount().toString();

                    if (_t.equals("0.0")) {
                        _t = "1";

                    } else {
                        _t = _t.replace(".0", "");

                    }
                    builder2 += "<td>" + _t + "</td>";
                    builder2 += "<td>" + item.getAmountof() + "</td>";
                    builder2 += "<td>" + item.getName() + "</td>";

                    builder2 += "</tr>";
                }
                builder2 += "</table></body></html>";
                final String mimeType = "text/html";
                final String encoding = "UTF-8";
                binding.nrWebview.loadDataWithBaseURL("", builder2, mimeType, encoding, "");

            }

        });
        binding.nrButtonSubmit.setOnClickListener(view -> {
            try {

                String name = binding.nrFieldName.getText().toString();
                String Creator = binding.nrFieldAutor.getText().toString();
                String summary = binding.nrFieldSummary.getText().toString();

                if (recipeid == 0) recipeid = System.currentTimeMillis();

                Bitmap bm = ((BitmapDrawable) binding.nrFieldImage.getDrawable()).getBitmap();
                String ImagePath = saveImage(recipeid + "", bm);

                String RecDir = activityReference.getApplicationContext().getApplicationInfo().dataDir + "/files/";
                delete(RecDir, recipeid + "");

                RecipeModel newRecipe = new RecipeModel(getApplicationContext());
                newRecipe.setId(recipeid);
                newRecipe.setName(name);
                newRecipe.setCreator(Creator);

                newRecipe.setImagePath(ImagePath);
                newRecipe.setImagePath_internal(ImagePath);

                newRecipe.setINDRIGENTS(_indrigents);
                newRecipe.setSummary(summary);
                newRecipe.setUrl(url);

                newRecipe.serialize(RecDir, "" + newRecipe.getId() + ".rcp");

            } catch (
                    IOException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (isediting) {
                returnIntent = new Intent();
                returnIntent.putExtra("action", "edit");
                returnIntent.putExtra("result", "");
                returnIntent.putExtra("pos", position);

                setResult(Activity.RESULT_OK, returnIntent);
            } else {
                returnIntent = new Intent();
                returnIntent.putExtra("action", "findWeb");
                returnIntent.putExtra("result", "");
                setResult(Activity.RESULT_OK, returnIntent);
            }
            finish();
        });
        binding.nrFieldName.setOnFocusChangeListener((view, b) -> {
            resetdescriptions();

            binding.nrDescName.setText(R.string.nr_desc_name_tipp);
        });
        binding.nrFieldZutaten.setOnFocusChangeListener((view, b) -> {
            resetdescriptions();

            binding.nrDescIndrigents.setText(R.string.nr_desc_indrigents_tip);
        });
        binding.nrFieldSummary.setOnFocusChangeListener((view, b) -> {
            resetdescriptions();
            binding.nrDescSummary.setText(R.string.nr_desc_summary_tipp);
        });
        View view = binding.getRoot();
        setContentView(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onJsuopResult() {


        String BaseUrl = getBaseUrl(url);

        // NAME DES REZEPTS
        String[] Titel = doc.title().split("\\||von");
        binding.nrFieldName.setText(Titel[0]);

        // NAME DES AUTORS
        String url_Autor = getBaseUrl(url).substring(url.indexOf(".") + 1).replace("/", "");
        String _Autor = "";
        if (Titel.length > 1)
            _Autor = Titel[1].trim() + " [" + url_Autor + "]";
        else if (Titel.length > 2)
            _Autor = Titel[1].trim() + " (" + Titel[2].trim() + ")" + " [" + url_Autor + "]";
        else
            _Autor = Titel[0].trim().substring(Titel[0].indexOf(" - ") + 3) + " [" + url_Autor + "]";


        // BILDER
        Elements images = doc.getElementsByTag("img");
        for (Element image : images) {
            String ImageUrl = image.attr("src");
            if (ImageUrl.startsWith("/"))
                ImageUrl = BaseUrl + ImageUrl;

            if (ImageUrl.matches("^.*jpg|^.*bmp|^.*png"))
            ScrapedImgUrls.add(ImageUrl);
        }
        updateimage(ScrapedImgUrls.get(ScrapedImgUrls_position), false);


        Elements dividers = doc.select("div.recipe--full__ingredients");

        // ZUTATEN

        if (url_Autor.matches("einfachbacken.de|daskochrezept.de")) {
            tablefromscript("recipeIngredient");
            summaryfrompagetext();
        } else if (url_Autor.equals("chefkoch.de")) {
            tablefromtable();
            summaryfromdiv("div.ds-box");
        } else if (url_Autor.equals("essen-und-trinken.de")) {
            tablefrommixed("x-beautify-number", "p.recipe-ingredients__label.u-typo.u-typo--recipe-ingredients-text");
            summaryfromdiv("div.group.group--preparation-steps");
        } else {

            tablefromscript("recipeIngredient");
            summaryfrompagetext();
        }

        // for (Element div : dividers)
        //    ScrapedDiv.add(div.toString().replace("&quot", ""));
        // ScrapedDiv.sort(Comparator.comparingInt(String::length).reversed());

        if (ScrapedImgUrls.size() == 0)
            ScrapedImgUrls.add("");
        if (ScrapedTables.size() == 0)
            ScrapedTables.add("");
        if (ScrapedSummary.size() == 0)
            ScrapedSummary.add("");

        binding.nrFieldAutor.setText(_Autor);
        binding.nrFieldZutaten.setText(ScrapedTables.get(ScrapedTables_position));
        binding.nrFieldSummary.setText(ScrapedSummary.get(ScrapedSummary_position));
        binding.nrFieldZutaten.setOnClickListener(View::requestFocus);
        binding.nrFieldZutaten.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                binding.nrFieldImage.requestFocus();
                hideKeyboard(activityReference);

                ScrapedTables_position -= 1;
                if (ScrapedTables_position < 0)
                    ScrapedTables_position = ScrapedTables.size() - 1;
                binding.nrFieldZutaten.setText(ScrapedTables.get(ScrapedTables_position));
                Toast.makeText(getApplicationContext(), "(" + (ScrapedTables_position + 1) + "/" + ScrapedTables.size() + ") " + ScrapedTables.get(ScrapedTables_position), Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                binding.nrFieldImage.requestFocus();
                hideKeyboard(activityReference);

                ScrapedTables_position++;
                if (ScrapedTables_position >= ScrapedTables.size())
                    ScrapedTables_position = 0;
                binding.nrFieldZutaten.setText(ScrapedTables.get(ScrapedTables_position));
                Toast.makeText(getApplicationContext(), "(" + (ScrapedTables_position + 1) + "/" + ScrapedTables.size() + ") " + ScrapedTables.get(ScrapedTables_position), Toast.LENGTH_SHORT).show();
            }
        });
        binding.nrFieldSummary.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                binding.nrFieldImage.requestFocus();
                hideKeyboard(activityReference);

                ScrapedSummary_position -= 1;
                if (ScrapedSummary_position < 0)
                    ScrapedSummary_position = ScrapedSummary.size() - 1;
                binding.nrFieldSummary.setText(ScrapedSummary.get(ScrapedSummary_position));
                Toast.makeText(getApplicationContext(), "(" + (ScrapedSummary_position + 1) + "/" + ScrapedSummary.size() + ") " + ScrapedSummary.get(ScrapedSummary_position), Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                binding.nrFieldImage.requestFocus();
                hideKeyboard(activityReference);

                ScrapedSummary_position++;
                if (ScrapedSummary_position >= ScrapedSummary.size())
                    ScrapedSummary_position = 0;
                binding.nrFieldSummary.setText(ScrapedSummary.get(ScrapedSummary_position));
                Toast.makeText(getApplicationContext(), "(" + (ScrapedSummary_position + 1) + "/" + ScrapedSummary.size() + ") " + ScrapedSummary.get(ScrapedSummary_position), Toast.LENGTH_SHORT).show();
            }
        });
        binding.nrFieldImage.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeRight() {
                ScrapedImgUrls_position -= 1;
                if (ScrapedImgUrls_position < 0)
                    ScrapedImgUrls_position = ScrapedImgUrls.size() - 1;
                updateimage(ScrapedImgUrls.get(ScrapedImgUrls_position), false);
                Toast.makeText(getApplicationContext(), "(" + (ScrapedImgUrls_position + 1) + "/" + ScrapedImgUrls.size() + ") " + ScrapedImgUrls.get(ScrapedImgUrls_position), Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                ScrapedImgUrls_position++;
                if (ScrapedImgUrls_position >= ScrapedImgUrls.size())
                    ScrapedImgUrls_position = 0;
                updateimage(ScrapedImgUrls.get(ScrapedImgUrls_position), false);
                Toast.makeText(getApplicationContext(), "(" + (ScrapedImgUrls_position + 1) + "/" + ScrapedImgUrls.size() + ") " + ScrapedImgUrls.get(ScrapedImgUrls_position), Toast.LENGTH_SHORT).show();
            }


        });
        binding.nrButtonTowebview.setVisibility(View.VISIBLE);
    }

    public void tablefromscript(String keyword) {
        String einfachbacken = html;
        int found = einfachbacken.indexOf(keyword);
        if (found != -1) {
            einfachbacken = einfachbacken.substring(found);
            einfachbacken = einfachbacken.substring(einfachbacken.indexOf("[") + 1, einfachbacken.indexOf("]"));
            einfachbacken = einfachbacken.replace("\",\"", "\n").replace("\"", "");
            ScrapedTables.add(einfachbacken);
        }
    }

    public void tablefrommixed(String keyword, String keyword2) {
        Elements list1 = doc.select(keyword);
        Elements list2 = doc.select(keyword2);
        String result = "";
        for (int i = 0; i < list1.size() && i < list2.size(); i++) {
            String check_for_empty = list1.get(i).text().trim();
            if (check_for_empty.equals(""))
                check_for_empty = "1 x";

            String check_for_double = list2.get(i).text().trim();
            String[] split = check_for_double.split(" ");

            if (split.length > 2)
                if (split[0].equals(split[1]))
                    check_for_double = check_for_double.substring(split[0].length() + 1,check_for_double.length() );

            result += check_for_empty + " " + check_for_double + "\n";
        }
        ScrapedTables.add(result);
    }

    public void tablefromquery(String keyword) {
        Elements chefkoch = doc.select(keyword);
        List<String> lresult = new ArrayList<>();
        for (Element element : chefkoch) {
            String result = element.text();
            lresult.add(result);
        }
        ScrapedTables.addAll(lresult);
        ScrapedTables.sort(Comparator.comparingInt(String::length).reversed());
    }

    public void tablefromtable() {
        // TABELLE
        Elements tables = doc.select("table"); //select the first table.

        for (Element table : tables) {
            String tableresult = Jsoup_parse.TableResult(table);
            ScrapedTables.add(tableresult);
        }
    }

    public void summaryfrompagetext() {
        Elements Pages = doc.select("p");
        String summary = "";
        for (Element Page : Pages)
            summary += Page.text();
        ScrapedSummary.add(summary);
        ScrapedSummary.sort(Comparator.comparingInt(String::length));
    }

    public void summaryfromdiv(String keyword) {
        Elements chefkoch = doc.select(keyword);
        List<String> lresult = new ArrayList<>();
        for (Element element : chefkoch) {
            String result = element.text();
            if (result.length() > 40)
                lresult.add(result);
        }
        ScrapedSummary.addAll(lresult);
        ScrapedSummary.sort(Comparator.comparingInt(String::length).reversed());
    }

    public boolean delete(String _RecDir, String _filename) {
        boolean myFile = true;
        _filename += ".rcp";
        if (!new File(_RecDir + _filename + "/", "recipe.rcp").delete())
            myFile = false;

        if (!new File(_RecDir + _filename + "/", "indrigents.ind").delete())
            myFile = false;

        if (!new File(_RecDir + _filename + "/", "image.png").delete())
            myFile = false;

        if (!new File(_RecDir + _filename + "/").delete())
            myFile = false;

        return myFile;
    }

    public void result_from_field() {

        String indrigents = binding.nrFieldZutaten.getText().toString().replaceAll("[|;#]", "").trim();
        List<String> indrigentslist = Arrays.asList(indrigents.split("\n"));
        List<String> indrigents_untereinander = new ArrayList<>();
        String regex = "^([0-9]*[/\\.,]?[0-9]*)\\s*([a-zA-ZÄäÖöÜüß0-9/(),.]*)\\s?(.*)?";
        Pattern p = Pattern.compile(regex);

        if (untereinander) {
            int step = Integer.parseInt(binding.spinner.getSelectedItem().toString());

            List<String> indrigents_prepare = new ArrayList<>();

            for (String t : indrigentslist) {
                if (!t.equals(""))
                    indrigents_prepare.add(t);
            }

            for (int i = 0; i < indrigents_prepare.size() - step + 1; i += step) {

                String sumline = indrigents_prepare.get(i);

                for (int k = 1; k < step; k++)
                    sumline += " " + indrigents_prepare.get(i + k);

                indrigents_untereinander.add(sumline);
            }

            indrigentslist = indrigents_untereinander;

        }

        _indrigents.clear();
        for (String _item : indrigentslist) {
            if (_item.equals(""))
                continue;

            List<String> allMatches = new ArrayList<>();

            if (_item.matches(regex)) {
                Matcher m = p.matcher(_item);


                if (m.find()) {

                    allMatches.add(m.group(1));
                    allMatches.add(m.group(2));
                    allMatches.add(m.group(3));


                    if (allMatches.get(allMatches.size() - 1).equals("")) {
                        allMatches.set(allMatches.size() - 1, allMatches.get(allMatches.size() - 2));
                        allMatches.set(allMatches.size() - 2, "");
                    }

                    float f;

                    switch (allMatches.get(0)) {
                        case "1/2":
                            f = 0.5f;
                            break;
                        case "1/4":
                            f = 0.25f;
                            break;
                        case "1/3":
                            f = 0.33f;
                            break;
                        case "3/4":
                            f = 0.75f;
                            break;
                        default:
                            try {
                                f = Float.parseFloat(allMatches.get(0));
                            } catch (Exception e) {
                                f = 0f;
                            }
                            break;
                    }

                    String amountof = allMatches.get(1).trim();

                    if (amountof.equals(""))
                        amountof = "x";

                    String indrigent = allMatches.get(2).trim();

                    _indrigents.add(new Indrigent(f, amountof, indrigent, 0));
                }
            }
        }
    }

    public void resetdescriptions() {
        binding.nrDescName.setText(getResources().getString(R.string.nr_desc_name));
        binding.nrDescIndrigents.setText(getResources().getString(R.string.nr_desc_indrigents));
        binding.nrDescSummary.setText(getResources().getString(R.string.nr_desc_summary));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        return false;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);

        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = (Intent) allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }


        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        if (resultCode == Activity.RESULT_OK) {

            ImageView imageView = binding.nrFieldImage;

            Bitmap myBitmap;
            if (getPickImageResultUri(data) != null) {
                Uri picUri = getPickImageResultUri(data);

                try {
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    // myBitmap = rotateImageIfRequired(myBitmap, picUri);
                    myBitmap = getResizedBitmap(myBitmap, 300);

                    // CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
                    // croppedImageView.setImageBitmap(myBitmap);
                    imageView.setImageBitmap(myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {


                bitmap = (Bitmap) data.getExtras().get("data");

                myBitmap = bitmap;
                /* CircleImageView croppedImageView = (CircleImageView) findViewById(R.id.img_profile);
                if (croppedImageView != null) {
                    croppedImageView.setImageBitmap(myBitmap);
                }

                 */

                imageView.setImageBitmap(myBitmap);

            }

        }

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binding = null;

        if (thread != null)
            thread.interrupt();

        thread = null;
    }

    private String saveImage(String name, Bitmap image) {
        String savedImagePath = null;

        String imageFileName = name + ".jpg";
        String ImageDir = getApplicationContext().getApplicationInfo().dataDir + "/files";

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

    @Override
    public boolean BackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("action", "none");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        return true;
    }
}

