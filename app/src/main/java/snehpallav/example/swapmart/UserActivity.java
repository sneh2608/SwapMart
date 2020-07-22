package snehpallav.example.swapmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import snehpallav.example.memophile.R;

public class UserActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private UserItemAdapter mAdapter;
    private List<Upload> mUploads;
    private Toolbar mToolbar;
    private CircleImageView hImage;
    SharedPreferences prefs;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = this.getSharedPreferences("com.example.lazybazaar",MODE_PRIVATE);

        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        // getSupportActionBar().setTitle("Find Friends");
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Activity");
        String headerText = sharedPreferences.getString("username","No name");
        String imggUrl = prefs.getString("imgUrl", null);

        TextView hText = findViewById(R.id.profileName);
        hText.setText(headerText);
        hImage = findViewById(R.id.profilePic);
        Picasso.get().load(imggUrl)
                .placeholder(R.drawable.profile_image)
                .fit()
                .centerCrop()
                .into(hImage);

        mRecyclerView=findViewById(R.id.container);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUploads = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("uploads");
        mDatabase.orderByChild("emailId").equalTo(sharedPreferences.getString("email","No data")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                mAdapter = new UserItemAdapter(UserActivity.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
