package com.jinasoft.fileupload1111;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class PopupActivity extends AppCompatActivity {




    private TextView tNAME;
    private TextView tNUM;
    private TextView tADD;
    private ImageView imgview;


    private ArrayList<String> token;
    private ArrayList<String> name;
    private ArrayList<String> profile;
    private String MyName;
    private String Name111;
    private String ImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        imgview = findViewById(R.id.img);


        final SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);// 아이디, 주소 , 전화번호
        MyName = pref.getString("nickname","");

        Name111 = "안양공인중개사6";

        token = new ArrayList<>();
        name = new ArrayList<>();
        profile = new ArrayList<>();




        tNAME=findViewById(R.id.tNAME);
        tNUM=findViewById(R.id.tNUM);
        tADD=findViewById(R.id.tADD);

        Intent intent = getIntent();
        String name ="에이공인중개사";
        String number = "010-135-5468";
        String address = "경기도 안양시";



        tNAME.setText(name);
        tNUM.setText(number);
        tADD.setText(address);

        InsertData task = new InsertData();
        task.execute("http://58.2330.203.182/Landpage/Realtor_List.php");


        Thread mThread = new Thread() {

            @Override
            public void run() {
                try {
                    URL url = new URL (baseShoppingURL);
                }
            }
        }





    }


    class InsertData extends AsyncTask<String, Void, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                JSONObject obj = new JSONObject(result);
                JSONArray jsonArray = obj.getJSONArray("Response");


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject subJsonObject = jsonArray.getJSONObject(i);


                    String sToken = subJsonObject.getString("token");
                    String sName = subJsonObject.getString("name");
                    String sProfile_Image = subJsonObject.getString("profile_Image");
                    token.add(sToken);
                    name.add(sName);
                    profile.add(sProfile_Image);

                    if(name.get(i).equals(Name111)) {
                        ImageURL = profile.get(i);
                    }

                }
//                try {
//                    URL url = new URL(ImageURL);
//                    URLConnection conn = url.openConnection();
//                    conn.connect();
//                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
//                    Bitmap bm = BitmapFactory.decodeStream(bis);
//                    bis.close();
//                    imgview.setImageBitmap(bm);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                Toast.makeText(PopupActivity.this,ImageURL,Toast.LENGTH_SHORT).show();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                URL url = new URL("http://58.230.203.182/Landpage/Realtor_List.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {


                return new String("Error: " + e.getMessage());
            }



        }
    }

}
