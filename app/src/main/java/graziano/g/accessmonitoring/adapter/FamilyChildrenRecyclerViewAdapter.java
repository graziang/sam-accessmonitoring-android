package graziano.g.accessmonitoring.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.data.DataUtils;
import graziano.g.accessmonitoring.data.ImageHelper;
import graziano.g.accessmonitoring.data.ImagesDatabaseHelper;
import graziano.g.accessmonitoring.fragment.FamilyFragment.OnListFragmentInteractionListener;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Session;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class FamilyChildrenRecyclerViewAdapter extends RecyclerView.Adapter<FamilyChildrenRecyclerViewAdapter.ChildViewHolder> {


    private final List<Child> children;
    private final OnListFragmentInteractionListener mListener;
    private final Context context;

    private ImagesDatabaseHelper imagesDatabaseHelper;
    private HashMap<Integer, Bitmap> images = new HashMap<>();


    public FamilyChildrenRecyclerViewAdapter(List<Child> items, OnListFragmentInteractionListener listener, Context context) {
        children = items;
        mListener = listener;
        this.context = context;
        imagesDatabaseHelper = new ImagesDatabaseHelper(context);
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_family, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChildViewHolder holder, int position) {

        Child child = children.get(position);
        holder.child = child;

        String childName = child.getName();
        String childSessionDate = "";
        String childSessionAddress = "";

        if(child.getSessions() != null && !child.getSessions().isEmpty()) {
            Session lastSession = child.getSessions().iterator().next();

            childSessionDate = DataUtils.printDifference(lastSession.getDate(), new Date());

            if (lastSession.getAddressString() != null && !lastSession.getAddressString().isEmpty()) {
                childSessionAddress = lastSession.getAddressString();
            }
        }

        if(child.isActive()) {
            holder.childActivePulsator.start();
        }
        else {
            holder.childActivePulsator.stop();
        }

        View button = holder.childActivePulsator.findViewById(R.id.status);
        Drawable background = button.getBackground();
        if(!child.isActive()) {
            holder.childActivePulsator.setColor(context.getResources().getColor(R.color.colorNotActive));
            ((GradientDrawable) background).setColor(context.getResources().getColor(R.color.colorNotActive));
        }
        else {
            holder.childActivePulsator.setColor(context.getResources().getColor(R.color.colorActive));
            ((GradientDrawable) background).setColor(context.getResources().getColor(R.color.colorActive));
        }

        holder.childNameView.setText(childName);
        holder.childLastSessionDateView.setText(childSessionDate);
        holder.childAddressView.setText(childSessionAddress);


        holder.childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks
                    mListener.onListFragmentInteraction(holder.child);
                }
            }
        });

        //load image
        Bitmap childImage = images.get(position);
        if (childImage == null) {
            holder.childImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_face_24px));
            child.setIndex(position);
            new ImageLoaderTask().execute(child);
        }
        else {
            holder.childImageView.setImageBitmap(childImage);
        }

    }

    public void clearImagesCache(){
        this.images = new HashMap<>();
    }

    @Override
    public int getItemCount() {
        return this.children.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {

        public final View childView;
        public final TextView childNameView;
        public final TextView childAddressView;
        public final TextView childLastSessionDateView;
        public final PulsatorLayout childActivePulsator;
        public final ImageView childImageView;
        public Child child;

        public ChildViewHolder(View view) {
            super(view);
            childView = view;
            childNameView = (TextView) view.findViewById(R.id.child_name);
            childAddressView = (TextView) view.findViewById(R.id.child_sessin_address);
            childLastSessionDateView = (TextView) view.findViewById(R.id.child_session_date);
            childActivePulsator = (PulsatorLayout) view.findViewById(R.id.pulsator);
            childImageView = (ImageView) view.findViewById(R.id.child_image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + childNameView.getText() + "'";
        }
    }



    public class ImageLoaderTask extends AsyncTask<Child, Integer, ImageHelper> {

        private int index = 0;

        protected void onPreExecute() {
        }

        @Override
        protected ImageHelper doInBackground(Child... children) {
            Child child = children[0];
            index = child.getIndex();
            return imagesDatabaseHelper.getImage(child.getFamilyName(), child.getName());
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(ImageHelper imageHelper) {
            if(imageHelper != null && imageHelper.getImageByteArray() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageHelper.getImageByteArray(), 0, imageHelper.getImageByteArray().length);
                FamilyChildrenRecyclerViewAdapter.this.notifyItemChanged(index);
                images.put(index, bitmap);
            }
        }
    }
}
