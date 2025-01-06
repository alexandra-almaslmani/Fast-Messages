package com.example.fastmessage;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    String phone, message;
    EditText phone_et, msg_et;
    ImageButton send_btn,capture_btn,multimedia_btn, save_btn;
    ImageView picture_iv;
    private static final int request_camera_permission = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fast Messages");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleBold);

        phone_et = findViewById(R.id.phone);
        msg_et = findViewById(R.id.massege);
        picture_iv = findViewById(R.id.picture);
        capture_btn = findViewById(R.id.camera);
        send_btn = findViewById(R.id.send);
        multimedia_btn = findViewById(R.id.multimedia);
        save_btn = findViewById(R.id.save);

        capture_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_camera_permission);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, request_camera_permission);
                }
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS}, 1);
                }
                else {
                    sendSMS();
                }
            }
        });

        multimedia_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 2);
                }
                else {
                    sendMMS();
                }
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                }
                else {
                    Bitmap bitmap = ((BitmapDrawable) picture_iv.getDrawable()).getBitmap();
                    saveImageToGallery(bitmap);
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void sendSMS(){
        phone = phone_et.getText().toString();
        message = msg_et.getText().toString();
        if(message.isEmpty()){
            Toast.makeText(MainActivity.this, "Please fill the message field", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!phone.matches("^[0-9]{10}$") || phone.isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,message,null,null);
            Toast.makeText(MainActivity.this, "SMS Message sent Successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, "SMS Message not sent, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMMS() {
        String phone = phone_et.getText().toString();
        String message = msg_et.getText().toString();
        if(phone.isEmpty() || message.isEmpty()){
            Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        picture_iv.setDrawingCacheEnabled(true);
        picture_iv.buildDrawingCache();
        Bitmap bitmap = picture_iv.getDrawingCache();

        Uri imageUri = saveImageToGalleryForSend(bitmap);
        if (imageUri != null) {
            Intent mmsIntent = new Intent(Intent.ACTION_SEND);
            mmsIntent.putExtra(Intent.EXTRA_TEXT, message);
            mmsIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            mmsIntent.setType("image/jpeg");
            mmsIntent.putExtra("address", phone);
            startActivity(Intent.createChooser(mmsIntent, "Send MMS"));
        } else {
            Toast.makeText(MainActivity.this, "Failed to send multimedia", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri saveImageToGalleryForSend(Bitmap bitmap) {
        Uri uri = null;
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "MMS Image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured for MMS");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    public void saveImageToGallery(Bitmap bitmap) {
        String savedImageURL = null;
        try {
            String imageTitle = "Captured Image_" + System.currentTimeMillis();
            String description = "Image captured by camera";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, imageTitle);
            values.put(MediaStore.Images.Media.DESCRIPTION, description);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                savedImageURL = uri.toString();
                Toast.makeText(this, "Image saved Successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == request_camera_permission){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            picture_iv.setImageBitmap(bitmap);
        }
        else{
            Toast.makeText(MainActivity.this, "Camera error or cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case request_camera_permission:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, request_camera_permission);
                }
                else {
                    Toast.makeText(MainActivity.this, "Permission denied to use camera", Toast.LENGTH_SHORT).show();
                }
                break;

            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMMS();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to send MMS", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Bitmap bitmap = ((BitmapDrawable) picture_iv.getDrawable()).getBitmap();
                    saveImageToGallery(bitmap);
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to write storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}