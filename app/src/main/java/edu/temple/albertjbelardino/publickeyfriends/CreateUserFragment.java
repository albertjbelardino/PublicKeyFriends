package edu.temple.albertjbelardino.publickeyfriends;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateUserFragment extends Fragment {
    static Partners myPartners;
    Button createUserButton;
    EditText userNameEditText;

    private OnFragmentInteractionListener mListener;

    public CreateUserFragment() {
        // Required empty public constructor
    }

    public static CreateUserFragment newInstance(Partners partners) {
        //put partners in seriazable
        CreateUserFragment fragment = new CreateUserFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("partners", partners);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_user, container, false);

        myPartners = (Partners) getArguments().getSerializable("partners");

        //create hash to test user names against
        final Hashtable<String, Boolean> userNamesHash = new Hashtable<String, Boolean>();

        //create usernames hash
        for(int i = 0; i < myPartners.size(); i++)
            userNamesHash.put(myPartners.partnersList.get(i).userName, true);

        //create error messages
        final String nonAlphaMessage, alreadyExistsMessage;

        nonAlphaMessage = "User Name Not AlphaNumeric. Try Again";
        alreadyExistsMessage = "User Name Already Exists. Try Again";

        //declare widgets and ui elements
        createUserButton = (Button) v.findViewById(R.id.enterUserNameButton);
        final EditText userNameEditText = (EditText) v.findViewById(R.id.userNameEditText);
        final TextView warningTextView = (TextView) v.findViewById(R.id.warningTextView);

        //if username is alphanumeric and username does not yet exist
            //post user to server
        //else
            //send a message to user telling them that they have to create a new name

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newUserName = userNameEditText.getText().toString();
                if(isAlphanumeric(newUserName) && !userNamesHash.containsKey(newUserName))
                    postUser(newUserName, warningTextView);
                else
                    warningTextView.setText(alreadyExistsMessage);
            }
        });

        return v;
    }

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

    public boolean isAlphanumeric(String s) {
        //create pattern to match against
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        //if it contains a non alphanumeric character, bool set to true
        boolean hasSpecialChar = p.matcher(s).find();
        //return the negation of the bool to make the method read correctly
        return !hasSpecialChar;
    }

    public void postUser(String userName, final TextView warningTextView) {
        //hashmap for post information
        Map<String, String> map = new HashMap<String, String>();

        //get location variables
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation;

        //if permission granted get users location
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        else
            myLocation = new Location(LocationManager.NETWORK_PROVIDER);

        //put params into hashmap for upload
        map.put("username", userName);
        map.put("latitude", String.valueOf(myLocation.getLatitude()));
        map.put("longitude", String.valueOf(myLocation.getLongitude()));

        //create a json post request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, Contract.POST_REQUEST_URL,
                new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //for testing, sets the text response
                    warningTextView.setText(response.toString());
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(req);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
