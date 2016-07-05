package com.example.shulz.camera;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void cameraIntent()//open the camera of user
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//launch the existing camera of user
        startActivityForResult(intent, 0);//call
    }

    private void galleryIntent() //runs on version 3.0 and above (honeycomb)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"),SELECT_FILE);
    }
}
