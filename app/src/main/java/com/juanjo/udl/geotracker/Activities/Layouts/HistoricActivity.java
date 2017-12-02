package com.juanjo.udl.geotracker.Activities.Layouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.juanjo.udl.geotracker.Activities.GlobalActivity.GlobalAppCompatActivity;
import com.juanjo.udl.geotracker.Adapters.JSONRecordAdapter;
import com.juanjo.udl.geotracker.JSONObjects.JSONProject;
import com.juanjo.udl.geotracker.JSONObjects.JSONRecord;
import com.juanjo.udl.geotracker.JSONObjects.JSONUser;
import com.juanjo.udl.geotracker.R;
import com.juanjo.udl.geotracker.Utilities.Constants;
import com.juanjo.udl.geotracker.Utilities.SampleData;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistoricActivity extends GlobalAppCompatActivity {

    Spinner fUser;
    Spinner fProject;
    EditText fDateIni;
    EditText fDateFin;
    Button btSearch;
    ListView listView;

    ArrayList<JSONRecord> records;
    ArrayList<String> projects;
    ArrayList<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.hideActionBar();

        setContentView(R.layout.activity_historic);

        SampleData sample = new SampleData();
//        sample.deleteProjects(this);
//        sample.deleteUsers(this);
//        sample.deleteRecords(this);
        sample.createProjects(this);
//        sample.createUsers(this);
//        sample.createRecords(this);

        fUser = findViewById(R.id.fUser);
        fProject = findViewById(R.id.fProject);
        fDateIni = findViewById(R.id.fDateIni);
        fDateFin = findViewById(R.id.fDateFin);
        btSearch = findViewById(R.id.btSearch);

        defaultSearchValues();

        try {
            showDialog();
            records = readRecords();
            if(records.size() == 0) {
                showToast(getString(R.string.txtNoRecords), Toast.LENGTH_SHORT);
                finish();
            }//Must have some records
            users = readUsers();
            ArrayAdapter<String> userAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
            fUser.setAdapter(userAdapter);

            projects = readProjects();
            ArrayAdapter<String> projectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projects);
            fProject.setAdapter(projectAdapter);
            dismissDialog();
        } catch (Exception e){
            processException(e);
        }

        final JSONRecordAdapter itemsAdapter = new JSONRecordAdapter(this, records);
        listView = findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONRecord recordClicked = (JSONRecord) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), RecordViewActivity.class);
                intent.putExtra("JSONRecord", recordClicked);
                startActivity(intent);
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener()
            {
                 @Override
                 public void onClick(View v) {
                     try {
                         records = readRecords();
                     } catch (Exception e) {
                         processException(e);
                     }
                     JSONRecordAdapter itemsAdapter = new JSONRecordAdapter(getBaseContext(), records);
                 ListView listView = findViewById(R.id.list);
                 listView.setAdapter(itemsAdapter);
                 }
            }
        );

    }


    private ArrayList<JSONRecord> readRecords() throws IOException, JSONException {
        return (ArrayList<JSONRecord>) Constants.AuxiliarFunctions.getLocalSavedJsonRecords(this);
    }

    private ArrayList<String> readProjects() throws IOException, JSONException {
        JSONProject project = null;
        ArrayList<String> projects = new ArrayList<String>();

        projects.add(getResources().getString(R.string.txtAll));

        File dir = new File(getFilesDir().getCanonicalPath() + Constants.StaticFields.getFolderOfProjects());
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    project = new JSONProject(this, file);
                    projects.add(project.getDescription());
                }
            }
        }

        return projects;
    }

    private ArrayList<String> readUsers() throws IOException, JSONException {
        JSONUser user = null;
        ArrayList<String> users = new ArrayList<String>();
        users.add(getResources().getString(R.string.txtAll));

        File dir = new File(getFilesDir().getCanonicalPath() + Constants.StaticFields.getFolderOfUsers());
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    user = new JSONUser(this, file);
                    users.add(user.getDescription());
                }
            }
        }

        return users;
    }

    private boolean validate(JSONRecord record) {
        boolean valid;

        valid = true;
        if (!fUser.getSelectedItem().toString().equals(getResources().getString(R.string.txtAll))) {
            if (!record.getUserName().equals(fUser.getSelectedItem().toString()))
                valid = false;
        }
        if (!fProject.getSelectedItem().toString().equals(getResources().getString(R.string.txtAll))) {
            if (!record.getProjectName().equals(fProject.getSelectedItem().toString()))
                valid = false;
        }
/*
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dataIni = dateFormat.parse(fDateIni.getText().toString());
            Date dataFin = dateFormat.parse(fDateFin.getText().toString());

            SimpleDateFormat dateFormatRecord = new SimpleDateFormat("dd/MM/yyyy hh:nn:ss");
            Date dateRecord = dateFormatRecord.parse(record.getDate());

            if (dateRecord.compareTo(dataIni) < 0) {
                valid = false;
            }
            if (dateRecord.compareTo(dataIni) > 0) {
                valid = false;
            }
*/
        return valid;
    }

    private void defaultSearchValues() {
        Date dateFin = null;

        dateFin = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fDateFin.setText(dateFormat.format(dateFin));

        Calendar dateIni = Calendar.getInstance();
        dateIni.setTime(dateFin);
        dateIni.add(Calendar.MONTH, -3);
        fDateIni.setText(dateFormat.format(dateIni.getTime()));

    }
}
