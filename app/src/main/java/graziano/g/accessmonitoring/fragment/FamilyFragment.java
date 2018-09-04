package graziano.g.accessmonitoring.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import graziano.g.accessmonitoring.adapter.FamilyChildrenRecyclerViewAdapter;
import graziano.g.accessmonitoring.data.ImagesDatabaseHelper;
import graziano.g.accessmonitoring.R;
import graziano.g.accessmonitoring.activity.SettingsActivity;
import graziano.g.accessmonitoring.model.Child;
import graziano.g.accessmonitoring.model.Family;
import graziano.g.accessmonitoring.network.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FamilyFragment extends Fragment implements Response.Listener, Response.ErrorListener{

    private final int timerPeriod = 10000;
    private OnListFragmentInteractionListener childListener;
    private FamilyChildrenRecyclerViewAdapter familyChildrenRecyclerViewAdapter;
    private List<Child> children = new ArrayList<>();

    private Timer updateTimer;

    public FamilyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(SettingsActivity.family.isActive()){
            this.updateTimer = new Timer();
            this.updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateChildren();
                }
            }, 0, timerPeriod);
        }
        else {
            this.children.clear();
            this.familyChildrenRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.updateTimer != null) {
            this.updateTimer.cancel();
        }

    }

    public void updateChildren(){

        if(!SettingsActivity.family.isActive()){
            return;
        }

        Family family = SettingsActivity.family;
        HttpClient.getFamilyByName(family.getName(), family.getPassword(), this, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            this.familyChildrenRecyclerViewAdapter = new FamilyChildrenRecyclerViewAdapter(this.children, childListener, getContext());
            recyclerView.setAdapter(familyChildrenRecyclerViewAdapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            childListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        childListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Child child);
    }

    @Override
    public void onResponse(Object response) {


        if(response instanceof Family){
            Family family = (Family) response;

            if(ImagesDatabaseHelper.toUpdate) {
                ImagesDatabaseHelper.toUpdate = false;
                this.familyChildrenRecyclerViewAdapter.clearImagesCache();
            }

            this.children.clear();
            this.children.addAll(family.getChildren());

            this.familyChildrenRecyclerViewAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        this.children.clear();
        this.familyChildrenRecyclerViewAdapter.notifyDataSetChanged();
    }
}
