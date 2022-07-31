package com.darkexceptionsoftware.thermomax_calendar.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;


import com.darkexceptionsoftware.thermomax_calendar.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RecipeModel implements Serializable, Parcelable {

    private long id;
    private String url;
    private String name;
    private String creator;

    protected RecipeModel(Parcel in) {
        id = in.readLong();
        url = in.readString();
        name = in.readString();
        creator = in.readString();
        Summary = in.readString();
        ImagePath = in.readString();
        tips = in.readString();
        CryptedID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(creator);
        dest.writeString(Summary);
        dest.writeString(ImagePath);
        dest.writeString(tips);
        dest.writeString(CryptedID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecipeModel> CREATOR = new Creator<RecipeModel>() {
        @Override
        public RecipeModel createFromParcel(Parcel in) {
            return new RecipeModel(in);
        }

        @Override
        public RecipeModel[] newArray(int size) {
            return new RecipeModel[size];
        }
    };

    public String getCreator() {
        return creator;
    }


    public void setCreator(String creator) {
        this.creator = creator;
    }

    private String Summary;
    private String ImagePath;
    private transient Bitmap picture;
    private transient Context context;
    private transient List<Indrigent> INDRIGENTS;
    private transient List<cookAction> COOKACTIONS;
    private String tips;
    private String CryptedID;
    private transient String filename;
    private java.lang.Object Object;
    public RecipeModel(Context context, int id, String _name, String _summary, Bitmap _picture, String _tips) {
        this.context = context;
        this.id = id;
        this.name = _name;
        Summary = _summary;
        this.tips = _tips;
        this.picture = _picture;
    }
    public RecipeModel(Context context) throws MalformedURLException, PackageManager.NameNotFoundException {
        reset(context);
    }

    public static String decrypt(String value) {

        String SECRET_KEY = "meinhamsterkannk";
        String INIT_VECTOR = "s87ßdfs8ß8bßa";

        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(value, Base64.URL_SAFE));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getCryptedID() {
        return CryptedID;
    }

    public void setCryptedID(String cryptedID) {
        CryptedID = cryptedID;
    }

    public List<cookAction> getCOOKACTIONS() {
        return COOKACTIONS;
    }

    public List<Indrigent> getINDRIGENTS() {
        return INDRIGENTS;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public final void reset(Context context) {
        this.context = context.getApplicationContext();
        this.INDRIGENTS = new ArrayList<Indrigent>();
        this.COOKACTIONS = new ArrayList<>();



        this.name = "";
        this.Summary = "";
        this.CryptedID = "";
        this.filename = "";

        String ImageDir = context.getApplicationInfo().dataDir + "/files/rImages/";
        ImagePath = ImageDir + "noimage.png";

        Bitmap bMap= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_baseline_dining_24);

        try {
            PackageManager m = context.getPackageManager();
            String s = context.getPackageName();
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;



            File podcastFolder = new File(ImageDir);
            if (!podcastFolder.exists()) {
                podcastFolder.mkdir();
            }

            File file = new File(podcastFolder, "noimage.png");
            if (!file.exists()) {
                FileOutputStream out = new FileOutputStream(file);
                bMap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();

            }


            picture = bMap;
        } catch (Exception e) {
        }

    }

    public final String getInfo() {

        String Info = "";
        String datestring = "";
        // decrypt for additional data

        if (!this.getCryptedID().equals("")) {
            String secretinfo = decrypt(this.getCryptedID());
            String[] splitted_secretinfo = secretinfo.split("#");
            try {
                long _date = Long.parseLong(splitted_secretinfo[0]);
                datestring = new SimpleDateFormat("MM/dd/yyyy").format(_date);
            } catch (Exception e) {
                Log.e(this.toString(), "error getting Date from recipe");
            }
        } else {
            datestring = "no given date";
        }

        Info = "Name:           " + this.name + "\r\n";
        Info += "Creation Date: " + datestring + "\r\n";
        Info += "Tips:          " + this.getTips() + "\r\n";
        Info += "Imagepath:     " + this.getImagePath() + "\r\n\r\n";
        Info += "Summary:       " + this.getSummary() + "\r\n\r\n";
        Info += "Filename:      " + this.getFilename() + "\r\n\r\n";



        return Info;
    }

    public boolean serialize(String _RecDir, String _filename){

            File myFile = new File(_RecDir + _filename + "/",  "recipe.rcp");
            if (!myFile.getParentFile().exists())
                myFile.getParentFile().mkdir();

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(myFile);
                ObjectOutputStream oout = null;
                oout = new ObjectOutputStream(new BufferedOutputStream(out));
                oout.writeObject(this);
                oout.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        // boolean c_success = serializeCook(_RecDir, _filename);
        boolean i_success = serializeIndrigents(_RecDir, _filename);
            return true;
    }

    public boolean serializeCook(String _RecDir, String _filename){

        File myFile = new File(_RecDir + _filename + "/",  "cookaction.coa");
        if (!myFile.getParentFile().exists())
            myFile.getParentFile().mkdir();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(myFile);
            ObjectOutputStream oout = null;
            oout = new ObjectOutputStream(new BufferedOutputStream(out));
            oout.writeObject(this.COOKACTIONS);
            oout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean serializeIndrigents(String _RecDir, String _filename){

        File myFile = new File(_RecDir + _filename + "/",  "indrigents.ind");
        if (!myFile.getParentFile().exists())
            myFile.getParentFile().mkdir();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(myFile);
            ObjectOutputStream oout = null;
            oout = new ObjectOutputStream(new BufferedOutputStream(out));
            oout.writeObject(this.INDRIGENTS);
            oout.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public boolean deserialize(String _RecDir, String _filename){

        RecipeModel o = null;

            File myFile = new File(_RecDir + _filename + "/",  "recipe.rcp");
            try {
                FileInputStream fis = null;
                ObjectInputStream in = null;

                fis = new FileInputStream(myFile);
                in = new ObjectInputStream(fis);
                 o = (RecipeModel) in.readObject();
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i("RECIPE", "not found: " + myFile.toString());

                return false;
            }

        this.name =  o.getName();
        this.creator =  o.getCreator();
        this.Summary =  o.getSummary();
        this.tips =  o.getTips();
        this.CryptedID = o.getCryptedID();
        this.ImagePath = o.getImagePath();

        //lade cookactions und indrigents

        List<Indrigent> p = null;

        myFile = new File(_RecDir + _filename + "/",  "indrigents.ind");
        try {
            FileInputStream fis = null;
            ObjectInputStream in = null;

            fis = new FileInputStream(myFile);
            in = new ObjectInputStream(fis);
            p = (List<Indrigent>) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();

            Log.i("INDRIGENT", "not found: " + myFile.toString());
            return false;
        }

        this.INDRIGENTS = p;

        //region cookactions

/*        List<cookAction> q = null;

        myFile = new File(_RecDir + _filename + "/",  "cookaction.coa");
        try {
            FileInputStream fis = null;
            ObjectInputStream in = null;

            fis = new FileInputStream(myFile);
            in = new ObjectInputStream(fis);
            q = (List<cookAction>) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i("COOKACTION", "not found: " + myFile.toString());

            return false;
        }

        this.COOKACTIONS = q;*/

        //endregion cookactions

        o = null; p = null; ;



            if (this.getImagePath() == null) {

                this.setImagePath(_RecDir + _filename + "/image.png");
            }





        return true;
    } }


