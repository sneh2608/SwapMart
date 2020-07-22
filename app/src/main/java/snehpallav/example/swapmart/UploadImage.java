package snehpallav.example.swapmart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import snehpallav.example.memophile.R;

public class UploadImage extends AppCompatActivity{
    private static final int image_upload_request = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextContatNo;
    private EditText mDescription;
    private EditText mPrice;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    SharedPreferences sharedPreferences;
    SharedPreferences prefs;
    private String imageUri;
    private Toolbar mToolbar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        Window window = this.getWindow();
        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.nameColor));

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_view_show_uploads);
        mEditTextContatNo = findViewById(R.id.edit_contact_number);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mDescription=findViewById(R.id.descriptionTextView);
        mPrice=findViewById(R.id.price);

        sharedPreferences = this.getSharedPreferences("com.example.lazybazaar",MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProductsActivity();
            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,image_upload_request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==image_upload_request
                && resultCode==RESULT_OK
                && data!=null
                && data.getData()!=null){
            mImageUri=data.getData();
            // imageUri=mImageUri.toString();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null&&mEditTextContatNo.getText().toString().trim().length()<=13&&mEditTextContatNo.getText().toString().trim().length()>=10){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+
                    "."+
                    getFileExtension(mImageUri));

            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                            final String formattedDate = df.format(c);
                            mButtonUpload.setEnabled(true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            },500);
                            String description = mDescription.getText().toString();
                            if(description.equals(""))
                                description="No details available";
                            final String shortDescription;
                            if(description.length()>40){
                                shortDescription = description.substring(0,40)+"...";
                            }else{
                                shortDescription=description;
                            }
                            String price=mPrice.getText().toString();
                            if(price.equals("")) price="Not Fixed";
                            String whatsappNo = mEditTextContatNo.getText().toString().trim();
                            if(whatsappNo.startsWith("+91")){
                                whatsappNo=whatsappNo;
                            }else{
                                whatsappNo="+91"+whatsappNo;
                            }
                            String[] words = sharedPreferences.getString("username","No Name").split(" ");
                            final StringBuilder sb = new StringBuilder();
                            if (words[0].length() > 0) {
                                sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
                                for (int i = 1; i < words.length; i++) {
                                    sb.append(" ");
                                    sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
                                }
                            }

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    final String finalWhatsappNo = whatsappNo;
                                    final String finalPrice = price;
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUri = uri.toString();
                                            //createNewPost(imageUrl);
                                            String titleCaseValue = sb.toString();
                                            Toast.makeText(UploadImage.this, "Upload Successful!", Toast.LENGTH_LONG).show();
                                            Upload upload = new Upload(finalWhatsappNo,
                                                    imageUri,
                                                    sharedPreferences.getString("email","No Email"),
                                                    prefs.getString("imgUrl", null),
                                                    shortDescription,
                                                    formattedDate,
                                                    titleCaseValue,
                                                    finalPrice);
                                            String uploadId = mDatabaseRef.push().getKey();
                                            mDatabaseRef.child(uploadId).setValue(upload);
                                        }
                                    });
                                }
                            }

//                            String titleCaseValue = sb.toString();
//                            Toast.makeText(UploadImage.this, "Upload Successful!", Toast.LENGTH_LONG).show();
//                            Upload upload = new Upload(whatsappNo,
//                                    imageUri,
//                                    sharedPreferences.getString("email","No Email"),
//                                    prefs.getString("imgUrl", null),
//                                    shortDescription,
//                                    formattedDate,
//                                    titleCaseValue,
//                                    price);
//                            String uploadId = mDatabaseRef.push().getKey();
//                            mDatabaseRef.child(uploadId).setValue(upload);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                            mButtonUpload.setEnabled(false);
                        }
                    });
        }
        else
        if(mEditTextContatNo.getText().toString().trim().length()>13||mEditTextContatNo.getText().toString().trim().length()<10){
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openProductsActivity(){
        Intent intent = new Intent(this,Product.class);
        startActivity(intent);
    }
}
