package com.bitlove.fetlife.view.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.PictureReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.PictureReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture_Table;
import com.bitlove.fetlife.util.PictureUtil;
import com.bitlove.fetlife.util.ServerIdUtil;
import com.bitlove.fetlife.util.ViewUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class PicturesRecyclerAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private final FetLifeApplication fetLifeApplication;

    private String memberId;
    private List<Picture> itemList;
    private ArrayList<String> displayLinks;
    private PictureUtil.OnPictureOverlayClickListener onPictureClickListener;

    public PicturesRecyclerAdapter(String memberId, PictureUtil.OnPictureOverlayClickListener onPictureClickListener, FetLifeApplication fetLifeApplication) {
        this.memberId = memberId;
        this.onPictureClickListener = onPictureClickListener;
        this.fetLifeApplication = fetLifeApplication;
        loadItems();
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //TODO: think of possibility of update only specific items instead of the whole list
                loadItems();
                notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            if (ServerIdUtil.isServerId(memberId)) {
                if (ServerIdUtil.containsServerId(memberId)) {
                    memberId = ServerIdUtil.getLocalId(memberId);
                } else {
                    return;
                }
            }
            List<PictureReference> pictureReferences = new Select().from(PictureReference.class).where(PictureReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(PictureReference_Table.date).descending()).queryList();
            List<String> pictureIds = new ArrayList<>();
            for (PictureReference pictureReference : pictureReferences) {
                pictureIds.add(pictureReference.getId());
            }
            itemList = new Select().from(Picture.class).where(Picture_Table.id.in(pictureIds)).orderBy(OrderBy.fromProperty(Picture_Table.date).descending()).queryList();
            displayLinks = new ArrayList<>();
            for (Picture picture : itemList) {
                displayLinks.add(picture.getDisplayUrl());
            }
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_picture, parent, false);
        return new PictureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, final int position) {
        Picture picture = itemList.get(position);
        holder.imageView.setImageURI(picture.getThumbUrl());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.itemView.getContext();

                LayoutInflater inflater = LayoutInflater.from(context);
                final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
                PictureUtil.setOverlayContent(overlay, itemList.get(position), onPictureClickListener);

                new ImageViewer.Builder(context, displayLinks).setStartPosition(position).setOverlayView(overlay).setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                    @Override
                    public void onImageChange(int position) {
                        try {
                            PictureUtil.setOverlayContent(overlay, itemList.get(position), onPictureClickListener);
                        } catch (IndexOutOfBoundsException ioobe) {
                            //Rare issue when user is browsing photos while Picture list is updated.
                            //TODO: return user to picture list screen in this case
                        }
                    }
                }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

class PictureViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView imageView;

    public PictureViewHolder(View itemView) {
        super(itemView);
        imageView = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
    }
}
