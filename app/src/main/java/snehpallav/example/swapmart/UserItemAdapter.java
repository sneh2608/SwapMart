package snehpallav.example.swapmart;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.List;

import snehpallav.example.memophile.R;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.UserItemViewHolder>{
    private Context mContext;
    private List<Upload> mUploads;

    public UserItemAdapter(Context context, List<Upload> uploads){
        mContext=context;
        mUploads=uploads;
    }
    @NonNull
    @Override
    public UserItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.useritem,parent,false);
        return new UserItemViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull UserItemViewHolder holder, int position) {
        Upload uploadCurr = mUploads.get(position);
        holder.itemName.setText(uploadCurr.getDescription());
        holder.uploadDate.setText(uploadCurr.getDate());
        holder.price.setText("INR "+uploadCurr.getPrice()+"/-");
        Picasso.get().load(uploadCurr.getImageUrl())
                .placeholder(R.drawable.defaultcart)
                .fit()
                .centerCrop()
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class UserItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView uploadDate;
        public ImageView itemImage;
        public TextView price;

        public UserItemViewHolder(View itemView) {
            super(itemView);

            itemName=itemView.findViewById(R.id.itemName);
            uploadDate=itemView.findViewById(R.id.text_view_date);
            itemImage=itemView.findViewById(R.id.uploaderItemPic);
            price=itemView.findViewById(R.id.menu_opener);
        }
    }
}
