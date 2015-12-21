package com.nscloud.android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nscloud.android.R;
import com.nscloud.android.crypto.CryptoManager;

/**
 * Created by intelligentsystems on 21/12/15.
 */
public class SetKey extends AppCompatActivity {

    private EditText editTextCurrent;
    private EditText editTextNew;
    private Button buttonSave;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_key);

        editTextCurrent = (EditText) findViewById(R.id.txt_current);
        editTextNew = (EditText) findViewById(R.id.txt_new);
        buttonSave = (Button) findViewById(R.id.save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentKey = editTextCurrent.getText().toString();
                String newKey = editTextNew.getText().toString();

                if (!CryptoManager.getClientKeyOriginal().equals(currentKey)) {
                    Toast.makeText(SetKey.this, "Wrong current key", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newKey.length() == 0) {
                    Toast.makeText(SetKey.this, "Please enter new key", Toast.LENGTH_SHORT).show();
                    return;
                }
                CryptoManager.setClientKey(newKey);

                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
