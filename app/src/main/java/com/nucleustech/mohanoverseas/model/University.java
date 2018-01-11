package com.nucleustech.mohanoverseas.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by raisahab on 22/12/17.
 */

public class University implements Serializable {

    public String universityId;

    public String universityName;

    public String address;

    public String about;

    public ArrayList<CertificateCourse> certificateCourses= new ArrayList<>();
}
