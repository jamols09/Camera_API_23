package com.example.shulz.camera;


import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;

import android.net.Uri;

import android.os.Environment;
import android.provider.MediaStore;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private String userChoosenTask="";
    private final int CHOOSE_IMAGE = 2;
    private GridView gridView;
    private Uri imageUri;
    private Button clearButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView =(ImageView)findViewById(R.id.iv);
        PhotoViewAttacher photo = new PhotoViewAttacher(imageView);
        photo.update();

        //Makes button unclickable if there is no image
        if (imageView.getDrawable().isVisible())
        {
            clearButton = (Button) findViewById(R.id.clearimage);
            clearButton.setClickable(false);
             Toast.makeText(getApplicationContext(),"INVISIBLE",Toast.LENGTH_LONG).show();
        }
        //makes button clickable
        else
        {
            clearButton = (Button) findViewById(R.id.clearimage);
            clearButton.setClickable(true);
            Toast.makeText(getApplicationContext(),"VISIBLE",Toast.LENGTH_LONG).show();
        }

        if(getIntent().getBooleanExtra("Exit me", false)){
            finish();
            return;
        }



    }

    public void chooseOption(View v)
    {
        selectImage();
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
                    }
                }

                else if(options[i].equals("Library")){
                    userChoosenTask="Library";

                    if(result){
                        galleryIntent();
                    }
                }
                else if (options[i].equals("Cancel")){
                    dialogInterface.dismiss();

                }


            }
        });
        build.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults)
    {
        switch (requestCode)
        {
            case Utility.READ_EXTERNAL_STORAGE:  //request memory reading but not know why needed to implement.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(userChoosenTask.equals("Take Photo"))
                    {
                        cameraIntent();
                    }
                    else if(userChoosenTask.equals("Library"))
                    {
                        galleryIntent();
                    }
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
        startActivityForResult(intent, 0);//call
    }

    private void galleryIntent() //runs on version 3.0 and above (honeycomb)
    {
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //call from startacitivityforresult
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == 1) //SELECT_FILE is 1
            {
                onSelectFromGalleryResult(data);
            }
            else if (requestCode == 0)// REQUEST_CAMERA  is 0
            {
                onCaptureImageResult(data);
            }
        }

        if(resultCode == RESULT_OK && requestCode == CHOOSE_IMAGE)
        {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    // disable code warnings. deprecated code & unused methods/variables
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data)//create bitmap which handles image
    {
        Bitmap bit = null;
        if (data != null)
        {
            try
            {
                bit = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),data.getData());//Android saves the images in its own database. Get the image from database
            }
            catch (IOException e)
            {
                e.printStackTrace(); //print error
            }
        }
        imageView.setImageBitmap(bit);
    }

    private void onCaptureImageResult(Intent data)
    {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG,100,bytes);//compresses the image into thumbnail
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis()+".jpg");

        FileOutputStream f;
        try
        {
            destination.createNewFile();
            f = new FileOutputStream(destination);  //writes the image into the destination(file)
            f.write(bytes.toByteArray());
            f.close();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace(); //print error if found
        }
        catch (IOException e)
        {
            e.printStackTrace(); //print error if found
        }
        imageView.setImageBitmap(thumbnail);


    }
}




//API LEVEL 23

//http://stackoverflow.com/questions/3226495/how-to-exit-from-the-application-and-show-the-home-screen#
//http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
//Github rep name jamols/CAMERA_API_23
//http://www.androidinterview.com/android-gallery-view-example-displaying-a-list-of-images/
//http://stacktips.com/tutorials/android/android-gridview-example-building-image-gallery-in-android