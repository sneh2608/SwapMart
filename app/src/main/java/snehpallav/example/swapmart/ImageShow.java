package snehpallav.example.swapmart;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.annotations.Nullable;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import jp.wasabeef.glide.transformations.BlurTransformation;
import snehpallav.example.memophile.R;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
public class ImageShow extends AppCompatActivity{

    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        ZoomageView imageShow = findViewById(R.id.imageShowLayouthow);
        final RelativeLayout layout =(RelativeLayout)findViewById(R.id.imageShowLayout);
//        layout.setBackground(ContextCompat.getDrawable(this, R.drawable.ready));

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUri");
        Glide.with(this).load(imageUrl)
                .apply(bitmapTransform(new BlurTransformation(22, 3))).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                layout.setBackground(resource);
            }
        });
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.lazybazaarlogo)
                .fit()
                .into(imageShow);
    }
}
