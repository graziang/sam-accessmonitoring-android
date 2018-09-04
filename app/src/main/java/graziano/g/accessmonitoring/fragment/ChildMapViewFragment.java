package graziano.g.accessmonitoring.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;

import graziano.g.accessmonitoring.data.DataUtils;
import graziano.g.accessmonitoring.data.ImageHelper;
import graziano.g.accessmonitoring.data.ImagesDatabaseHelper;
import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Session;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class ChildMapViewFragment extends Fragment implements OnMapReadyCallback {


    private MapView mapView;
    private LatLng location;
    private String name;
    private String address;
    private String date;
    private String familyName;
    private boolean active;

    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_DATE = "date";
    private static final String ARG_NAME = "name";
    private static final String ARG_ACTIVE = "active";
    private static final String ARG_FAMILY_NAME = "family_name";

    private int GALLERY = 1, CAMERA = 2;

    private ImageView childImageView;
    private ImagesDatabaseHelper imagesDatabaseHelper;

    public ChildMapViewFragment() {
    }

    public static ChildMapViewFragment newInstance(Child child) {
        ChildMapViewFragment fragment = new ChildMapViewFragment();
        Bundle args = new Bundle();


        Session lastSession = child.getSessions().iterator().next();

        if(lastSession.getAddressString() != null && !lastSession.getAddressString().isEmpty()) {
            args.putDouble(ARG_LATITUDE, lastSession.getLatitude());
            args.putDouble(ARG_LONGITUDE, lastSession.getLongitude());
            args.putString(ARG_ADDRESS, lastSession.getAddressString());
            args.putLong(ARG_DATE, lastSession.getDate().getTime());
        }


        args.putString(ARG_NAME, child.getName());
        args.putString(ARG_FAMILY_NAME, child.getFamilyName());
        args.putBoolean(ARG_ACTIVE, child.isActive());

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.address = getString(R.string.no_location_aviable);
        if (getArguments() != null) {

            if(getArguments().getString(ARG_ADDRESS) != null) {
                this.location = new LatLng(getArguments().getDouble(ARG_LATITUDE), getArguments().getDouble(ARG_LONGITUDE));
                this.address = getArguments().getString(ARG_ADDRESS);
                this.date = DataUtils.printDifference(new Date(getArguments().getLong(ARG_DATE)), new Date());
            }

            this.name = getArguments().getString(ARG_NAME);
            this.familyName = getArguments().getString(ARG_FAMILY_NAME);
            this.active = getArguments().getBoolean(ARG_ACTIVE);
        }


        imagesDatabaseHelper = new ImagesDatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_child_map_view, container, false);


        mapView = (MapView) v.findViewById(R.id.map_view);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        TextView childNameTextView =  v.findViewById(R.id.child_name);
        TextView childFamilyNameTextView =  v.findViewById(R.id.child_family_name);
        TextView addressNameTextView =  v.findViewById(R.id.child_sessin_address);
        TextView dateNameTextView =  v.findViewById(R.id.child_session_date);
        PulsatorLayout pulsatorLayout = v.findViewById(R.id.pulsator);
        View button = pulsatorLayout.findViewById(R.id.status);
        childImageView = v.findViewById(R.id.child_image);
        Drawable background = button.getBackground();

        childNameTextView.setText(this.name);
        childFamilyNameTextView.setText(this.familyName);
        addressNameTextView.setText(this.address);
        dateNameTextView.setText(this.date);

        if(!this.active) {
            pulsatorLayout.setColor(getContext().getResources().getColor(R.color.colorNotActive));
            ((GradientDrawable) background).setColor(getContext().getResources().getColor(R.color.colorNotActive));
        }
        else {
            pulsatorLayout.start();
            pulsatorLayout.setColor(getContext().getResources().getColor(R.color.colorActive));
            ((GradientDrawable) background).setColor(getContext().getResources().getColor(R.color.colorActive));
        }

        //loadimage
        childImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_face_24px));
        updateChildImageView();

        childImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        FloatingActionButton actionButtonChildMode = getActivity().findViewById(R.id.actiln_button_child_mode);
        actionButtonChildMode.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap gm) {
        gm.setMinZoomPreference(12);
        if(this.location != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            markerOptions.title(getString(R.string.location_pin_text));
            markerOptions.snippet(this.name);
            gm.addMarker(markerOptions);
            gm.moveCamera(CameraUpdateFactory.newLatLng(this.location));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    imagesDatabaseHelper.insetImage(bitmap, familyName, name);
                    ImagesDatabaseHelper.toUpdate = true;
                    updateChildImageView();
                } catch (IOException r) {
                    Toast.makeText(getContext(), R.string.failed_message, Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imagesDatabaseHelper.insetImage(thumbnail, familyName, name);
                ImagesDatabaseHelper.toUpdate = true;
                updateChildImageView();
                Toast.makeText(getContext(), R.string.image_saved, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateChildImageView(){
        Child child = new Child();
        child.setName(name);
        child.setFamilyName(familyName);
        new ImageLoaderTask().execute(child);
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle(R.string.image_picker);
        String[] pictureDialogItems = {
                getString(R.string.select_from_gallery),
                getString(R.string.select_from_camera)
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GALLERY);
        }
        else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY);
        }
    }

    private void takePhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA);
        }
        else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        }
    }

    private class ImageLoaderTask extends AsyncTask<Child, Integer, ImageHelper> {

        private Child child;
        protected void onPreExecute() {
        }

        @Override
        protected ImageHelper doInBackground(Child... children) {
            child = children[0];
            return imagesDatabaseHelper.getImage(child.getFamilyName(), child.getName());
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(ImageHelper imageHelper) {
            if(imageHelper != null && imageHelper.getImageByteArray() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageHelper.getImageByteArray(), 0, imageHelper.getImageByteArray().length);
                childImageView.setImageBitmap(bitmap);
            }
        }
    }
}
