package ashu.chatorigali.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ashu.chatorigali.R;

/**
 * Created by apple on 12/09/16.
 */

public class AddRecipe extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageCamera;
    private static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private Uri imageUri = null;
    private static int i;
    private File output = null;

    private EditText editTitle;
    private EditText editServings;
    private EditText editIngredients;

    private StringBuffer stringBuffer;
    private int lineNumber = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutAndVariables();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }

    private void setLayoutAndVariables(){
        setContentView(R.layout.add_recipe);
        imageCamera = (ImageView) findViewById(R.id.imgCamera);
        imageCamera.setOnClickListener(this);

        editTitle = (EditText) findViewById(R.id.editTitleRecipe);
        editServings = (EditText) findViewById(R.id.editServings);
        editIngredients = (EditText) findViewById(R.id.editIngredients);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.imgCamera:
//                File file=new File(Environment.getExternalStorageDirectory(),"");
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                output = new File(dir, "Content" + i++ + ".jpeg");
//                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"fname_" +
//                        String.valueOf(System.currentTimeMillis()) + ".jpg"));

                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(output), "image/jpeg");
                startActivity(intent);
                finish();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }
}
