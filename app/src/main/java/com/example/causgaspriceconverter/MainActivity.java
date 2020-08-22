package com.example.causgaspriceconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button calculate;
    EditText usdPerGallonInput;
    Double usdPerGallon;
    Double cadPerLitre;
    Double GallonToLitre = 3.785;
    Double currentExchangeRate;
    boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculate = findViewById(R.id.calculate);
        usdPerGallonInput = findViewById(R.id.usd_input);

        OkHttpClient client = new OkHttpClient();
        String url = "http://data.fixer.io/api/latest" +
                "?access_key=b37aac56ea3fe57d6d68e47ca760c4d0" +
                "&symbols=USD,CAD" +
                "&format=1";
        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String data = response.body().string();

                    JSONObject json;
                    JSONObject rates;
                    Double usd;
                    Double cad;
                    try {
                        json = new JSONObject(data);
                    } catch (JSONException e) {
                        json = null;
                    }
                    try {
                        rates = json.getJSONObject("rates");
                        usd = rates.getDouble("USD");
                        cad = rates.getDouble("CAD");
                        currentExchangeRate = cad / usd;
                    } catch (Exception e) {
                        success = false;
                    }
                }
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usdPerGallon = Double.valueOf(usdPerGallonInput.getText().toString());
                cadPerLitre = usdPerGallon / GallonToLitre * currentExchangeRate;
                TextView result = findViewById(R.id.result);
                if (success) {
                    result.setText(cadPerLitre.toString());
                }
                else {
                    result.setText("Fail to request exchange rate");
                }
            }
        });
    }
}
