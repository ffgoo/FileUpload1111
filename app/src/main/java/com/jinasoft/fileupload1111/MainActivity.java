package com.jinasoft.fileupload1111;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNOW = new SimpleDateFormat("yyyyMMdd-HHmmss");
    String filename = sdfNOW.format(date);

    private static final int REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST = 1;
    private CircleImageView imageView;

    private String RES;
    private String fileinfo ="/sdcard/DCIM/Camera/000000.jpg";
    private String fileinfo2;
    private String ANDROID_ID;

    private String filePath,filePath2;
    Button button,camerabutton;

    final String uploadFilePath = "/storage/emulated/0/DCIM/Camera/";
    final String uploadFileName = "loading.jpg";




    String upLoadServerUri = "http://58.230.203.182/Landpage/Add_Realtor_Image.php";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        camerabutton = findViewById(R.id.camerabutton);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        ANDROID_ID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);


//        InsertData task = new InsertData();
//        task.execute(upLoadServerUri);


        imageView = findViewById(R.id.image);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                InsertData task = new InsertData();
                task.execute(upLoadServerUri);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Context context = null;
        String name = filename + ".png";


        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    /**이미지 경로**/
                    Uri imageUri = data.getData();
                    filePath = getRealPathFromURI(imageUri);

                        /**이미지 뷰에 띄우기**/
                    InputStream in = getContentResolver().openInputStream(data.getData());
//                    Bitmap img = BitmapFactory.decodeStream(in);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 12;
                    Bitmap src = BitmapFactory.decodeFile(filePath, options);
                    in.close();
//                    Toast.makeText(this, src.toString(), Toast.LENGTH_LONG).show();
                    imageView.setImageBitmap(src);

                    fileinfo2 = src.toString();

//                    String file =getCacheDir().toString();
//                    Toast.makeText(this, file, Toast.LENGTH_LONG).show();
                   saveBitmapToJpeg(src,filename+".png");



            } catch(Exception e){

            }
//                String file =getCacheDir().toString();
//                Toast.makeText(this, file, Toast.LENGTH_LONG).show();
                //                    return tempFile.getAbsolutePath();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
/** 갤러리 퍼미션**/
    private void checkPermissions(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1052);

        }

    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1052: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted.

                } else {


                    // Permission denied - Show a message to inform the user that this app only works
                    // with these permissions granted

                }
                return;
            }

        }
    }


    /**파일 이름,경로 가져오기**/
    private String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();

        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        fileinfo = result;
        return result;
    }
    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    public String saveBitmapToJpeg(Bitmap bitmap, String fileName){

        File storage = getCacheDir(); // 이 부분이 임시파일 저장 경로

        fileName = filename + ".png";  // 파일이름은 마음대로!

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            filePath2 =tempFile.getAbsolutePath();
            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }
    /** 이미지, 텍스트 전송 **/
    public class InsertData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        final SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                JSONObject obj = new JSONObject(result);
                String sRES = obj.getString("Response");
                RES=sRES;


                if (RES.equals("Success")) {
                    Toast.makeText(MainActivity.this,  result, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this,  result, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(CreateClubActivity.this,ClubListActivity.class);
//                    startActivity(intent);
                }
            } catch (JSONException ex) {
                Toast.makeText(MainActivity.this,  result, Toast.LENGTH_SHORT).show();
            }


        }


        @Override
        protected String doInBackground(String... params) {

            String sourceFileUri = filePath2;
            String fileName = filename;


            DataOutputStream dos = null;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;


            File sourceFile = new File(sourceFileUri);

            try {

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoInput(true); // Allow Inputs
                httpURLConnection.setDoOutput(true); // Allow Outputs
                httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


                httpURLConnection.setRequestProperty("file", fileName+".png");
                httpURLConnection.setRequestProperty("token", ANDROID_ID);
                httpURLConnection.setRequestProperty("cell_phone_number" ,"");


                dos = new DataOutputStream(httpURLConnection.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"token\""+ lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(ANDROID_ID + lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"cell_phone_number\""
                        + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("" + lineEnd);

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + fileName+".png" + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                dos.flush();
                dos.close();


                int responseStatusCode = httpURLConnection.getResponseCode();


                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
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