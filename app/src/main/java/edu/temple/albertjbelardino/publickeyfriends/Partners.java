package edu.temple.albertjbelardino.publickeyfriends;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by albertjbelardino on 3/4/2018.
 */

public class Partners implements Serializable {
    ArrayList<Partner> partnersList;

    public Partners() {
        partnersList = new ArrayList<Partner>();
        //update();
    }

    public void add(Partner partner) {
        partnersList.add(partner);
    }

    public int size() {
        return partnersList.size();
    }

    public String displayAtIndex(int i) {
        return partnersList.get(i).getUserName()
                + " " + partnersList.get(i).getLatitude()
                + " " + partnersList.get(i).getLongitude();
    }

    public void update(){
        Collections.sort(partnersList);
    }

}
