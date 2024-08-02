package io.xconn.excelfilereader;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class FileSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);

        Spinner fileSpinner = findViewById(R.id.fileSpinner);
        Button selectButton = findViewById(R.id.selectButton);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.file_names, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        fileSpinner.setAdapter(adapter);

        selectButton.setOnClickListener(v -> {
            String selectedFile = fileSpinner.getSelectedItem().toString();
            openMainActivity(selectedFile);
        });
    }

    private void openMainActivity(String fileName) {
        Intent intent = new Intent(FileSelectionActivity.this, MainActivity.class);
        intent.putExtra("selectedFile", fileName);
        startActivity(intent);
    }
}
