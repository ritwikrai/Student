package com.nucleustech.mohanoverseas.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.nucleustech.mohanoverseas.student.R;
import com.nucleustech.mohanoverseas.adapter.UniversitiesAdapter;
import com.nucleustech.mohanoverseas.model.CertificateCourse;
import com.nucleustech.mohanoverseas.model.Course;
import com.nucleustech.mohanoverseas.model.University;
import com.nucleustech.mohanoverseas.util.AlertDialogCallBack;
import com.nucleustech.mohanoverseas.util.Util;
import com.nucleustech.mohanoverseas.volley.ServerResponseCallback;
import com.nucleustech.mohanoverseas.volley.VolleyTaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by knowalladmin on 23/12/17.
 */

public class AllUniversitiesActivity extends AppCompatActivity implements ServerResponseCallback {

    private Context mContext;
    private VolleyTaskManager volleyTaskManager;
    private ArrayList<University> universities;
    private RecyclerView recyclerUniversities;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_universities);
        mContext = AllUniversitiesActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("University");
        recyclerUniversities = (RecyclerView) findViewById(R.id.recyclerUniversities);
        volleyTaskManager = new VolleyTaskManager(mContext);

        volleyTaskManager.doPostFetchAllUniversities(new HashMap<String, String>(), true);

    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
            universities = new ArrayList<University>();
            JSONArray jsonArray = resultJsonObject.optJSONArray("universities");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                University university = new University();
                university.universityId = jsonObject.optString("universityID");
                university.address = jsonObject.optString("universityAddress");
                university.universityName = jsonObject.optString("universityName");
                university.about = jsonObject.optString("universityAbout");

                ArrayList<CertificateCourse> certificateCourses = new ArrayList<>();
                JSONArray courseCertificates = jsonObject.optJSONArray("degreesOffered");

                for (int j = 0; j < courseCertificates.length(); j++) {
                    JSONObject certificateCoursesJson = jsonArray.optJSONObject(j);
                    CertificateCourse certificateCourse = new CertificateCourse();
                    certificateCourse.certificateName = certificateCoursesJson.optString("degreeType");
                    certificateCourse.certificateDetails = certificateCoursesJson.optString("degreeDetail");
                    ArrayList<Course> courses = new ArrayList<>();
                    JSONArray coursesArray = certificateCoursesJson.optJSONArray("coursesOffered");
                    if (coursesArray != null && coursesArray.length() > 0) {
                        for (int k = 0; k < coursesArray.length(); k++) {
                            JSONObject courseObject = coursesArray.optJSONObject(k);
                            Course course = new Course();
                            //course.courseId=courseObject.optString("");
                            course.courseName = courseObject.optString("courseName");
                            course.courseDetails = courseObject.optString("courseDetails");
                            courses.add(course);
                        }
                    }
                    certificateCourse.courses = courses;
                    certificateCourses.add(certificateCourse);

                }

                university.certificateCourses = certificateCourses;
                universities.add(university);

            }

            //POPULATE UNIVERSITIES
            if (universities.size() > 0) {
                UniversitiesAdapter universitiesAdapter = new UniversitiesAdapter(mContext, universities);
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerUniversities.setLayoutManager(llm);
                //list.setAdapter( adapter );
                recyclerUniversities.setAdapter(universitiesAdapter);

                universitiesAdapter.setOnItemClickListener(new UniversitiesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, University obj, int position) {
                        //Toast.makeText(mContext, "Position: " + position, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, UniversityMenuActivity.class);
                        intent.putExtra("universityID", obj.universityId);
                        startActivity(intent);
                    }
                });
            } else {
                Util.showCallBackMessageWithOkCallback(mContext, "Could Not fetch Universities.", new AlertDialogCallBack() {
                    @Override
                    public void onSubmit() {
                        finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }


        }

    }

    @Override
    public void onError() {

    }
}
