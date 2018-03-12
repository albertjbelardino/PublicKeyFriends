package edu.temple.albertjbelardino.publickeyfriends;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PartnerMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PartnerMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartnerMapFragment extends Fragment implements OnMapReadyCallback {

    ArrayList<MarkerOptions> markerList;
    private OnFragmentInteractionListener mListener;

    GoogleMap googleMap;
    MapView mapView;
    Partner me;

    public PartnerMapFragment() {
        // Required empty public constructor
    }

    public static PartnerMapFragment newInstance(ArrayList<MarkerOptions> markerList, Partner me) {
        PartnerMapFragment fragment = new PartnerMapFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("markerList", markerList);
        args.putSerializable("me", me);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            markerList = getArguments().getParcelableArrayList("markerList");
            me = (Partner) getArguments().getSerializable("me");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_partner_map, container, false);

        mapView = (MapView) v.findViewById(R.id.mapView);
        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        return v;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initialize google maps
        MapsInitializer.initialize(getContext());

        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //create marker for user and zoom camera in on user initially
        MarkerOptions marker = new MarkerOptions()
                                            .title(me.getUserName())
                                            .position(new LatLng(me.getLatitude(), me.getLongitude()));
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(me.getLatitude(), me.getLongitude()), 13));

        //add rest of markers
        for(MarkerOptions m : markerList)
            googleMap.addMarker(m);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
