package com.juanjo.udl.geotracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.juanjo.udl.geotracker.Utilities.Constants.FieldTypes;


public class AddFieldActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private int inputType = InputType.TYPE_CLASS_TEXT;
    private EditText editText;
    private FieldTypes type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_field);

        String[] typesString = getResources().getStringArray(R.array.field_types);

        Spinner spinner = findViewById(R.id.fieldtype);
        ArrayAdapter<String>adapter = new ArrayAdapter<>(AddFieldActivity.this,
                android.R.layout.simple_spinner_dropdown_item, typesString);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        editText = findViewById(R.id.fildetitle);

        Button btnCreateField = findViewById(R.id.btncreatefield);
        btnCreateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", editText.getText().toString());
                intent.putExtra("inputType", inputType);
                intent.putExtra("type", type);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }//onCreate

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        inputType = InputType.TYPE_CLASS_TEXT;
        switch (position) {
            case 0: //Plain text
                type = FieldTypes.TEXT;
                break;
            case 1: //Numeric
                type = FieldTypes.NUMERIC;
                inputType = InputType.TYPE_CLASS_NUMBER;
                break;
            case 2: //Temperature
                type = FieldTypes.TEMPERATURE;
                editText.setText(getResources().getString(R.string.temperature));
                break;
            case 3: //Humidity
                type = FieldTypes.HUMIDITY;
                editText.setText(getResources().getString(R.string.humidity));
                break;
            case 4: //Pressure
                type = FieldTypes.PRESSURE;
                editText.setText(getResources().getString(R.string.pressure));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        inputType = InputType.TYPE_CLASS_TEXT;
    }

}
