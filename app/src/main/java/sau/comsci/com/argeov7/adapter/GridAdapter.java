package sau.comsci.com.argeov7.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import sau.comsci.com.argeov7.R;

/**
 * Created by KorPai on 28/3/2560.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private Context mContext;
    private List<EndangeredItem> endangeredItems;
    private MyClickListener mCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view)
        {
            super(view);
            title = (TextView) view.findViewById(R.id.cardview_txt_title);
            thumbnail = (ImageView) view.findViewById(R.id.cardview_img_thumbnail);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view)
        {
            mCallback.onItemClick(getAdapterPosition(),view);
        }
    }
    public GridAdapter(Context mContext, List<EndangeredItem> endangeredItems)
    {
        this.mContext = mContext;
        this.endangeredItems = endangeredItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_main,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        EndangeredItem m_endangeredItem = endangeredItems.get(position);
        holder.title.setText(m_endangeredItem.getnName());

        Glide.with(mContext).load(m_endangeredItem.getmThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount()
    {
        return endangeredItems.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener)
    {
        this.mCallback = myClickListener;
    }

    public interface MyClickListener
    {
        public void onItemClick(int position, View v);
    }
}
