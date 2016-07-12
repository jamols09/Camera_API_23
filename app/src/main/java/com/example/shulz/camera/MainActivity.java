package com.example.shulz.camera;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.DomainCombiner;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return;
        }
    }

    private void selectImage(){
        final String options[] = {"Take Photo","Library","Cancel"};

        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
        build.setTitle("Add Photo");
        build.setCancelable(true);
        build.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean result = Utility.checkPermission(MainActivity.this); //request permission of application
                if (options[i].equals("Take Photo")){
                    userChoosenTask="Take Photo";
                    if(result){
                        cameraIntent();
                    }else if(options[i].equals("Library")){
                        userChoosenTask="Library";
                        if(result){
                            galleryIntent();
                        }else if (options[i].equals("Cancel")){
                            dialog.dismiss();
                        }
                    }

                }
            }
        });
        build.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permission, int grantResults)
    {
        switch (requestCode)
        {
            case Utility.MY_PERMISSIONS_READ_EXTERNAL_STORAGE:  //request memory reading but now know why needed to implement.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Library"))
                        galleryIntent();
                }
                else
                {
                    //deny permission -> exit
                    Intent intent = new Intent(this, MainActivity.class); //declare intent
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //all of the other activities on top of it will be closed this Intent will be delivered to the top
                    intent.putExtra("Exit me", true);
                    startActivity(intent);
                    finish();
                }
        }
    }

    private void cameraIntent()//open the camera of user
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//launch the existing camera app of user
        startActivityForResult(intent, REQUEST_CAMERA);//call
    }

    private void galleryIntent() //runs on version 3.0 and above (honeycomb)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_FILE_NAME);//calls onactivityResult
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//call from startacitivityforresult
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == SELEC_FILE_NAME)
            {
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == REQUEST_CAMERA)
            {
                onCaptureImageResult(data);
            }
        }
    }
    // disable code warnings. deprecated code & unused methods/variables
    @SuppressWarnings("deprecation")
    @SuppressWarnings("unused")

    private void onSelectFromGalleryResult(Intent data)//create bitmap which handles image
    {
        Bitmap bit = null;
        if (data != null)
        {
            try
            {
                bit = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),data.getData());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bit);
    }

    private void onCaptureImageResult(Intent data)
    {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,80,bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis()+".jpg");

        FileOutputStream f;
        try
        {
            destination.createNewFile();
            f = new FileOutputStream(destination);
            f.write(bytes.toByteArray());
            f.close();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        ivImage.setImageBitmap(thumbnail);


    }
}
//http://stackoverflow.com/questions/3226495/how-to-exit-from-the-application-and-show-the-home-screen#
//http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
//Github rep name CAMERA_API_23