package com.example.qrcode;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcode.model.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageView qrCode;
    private EditText userName, userPass;
    private Button scanQrCode, generateQr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        generateQr.setOnClickListener(view -> generateQr());
        scanQrCode.setOnClickListener(view -> scanQrCode());
    }

    private void initViews() {
        qrCode = findViewById(R.id.image_view);
        scanQrCode = findViewById(R.id.scanQrCode);
        generateQr = findViewById(R.id.generate);
        userName = findViewById(R.id.userName);
        userPass = findViewById(R.id.userPass);
    }

    private void scanQrCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult scamResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scamResult != null) {
            String authHeader = scamResult.getContents();
            RetrofitBuilder.getService().getUser(authHeader).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Toast.makeText(MainActivity.this, response.body().getLogin(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("tag", "response code: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    Log.d("tag", "Error");
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generateQr() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix;
            if (!userName.getText().toString().isEmpty() && !userPass.getText().toString().isEmpty()) {

                String userName = this.userName.getText().toString().trim();
                String userPass = this.userPass.getText().toString().trim();

                String authHeader = Credentials.basic(userName, userPass);

                matrix = writer.encode(authHeader, BarcodeFormat.QR_CODE, 300, 300);
            } else {
                matrix = writer.encode("Error", BarcodeFormat.QR_CODE, 300, 300);
            }
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    int color;
                    if (matrix.get(x, y)) {
                        color = Color.BLACK;
                    } else {
                        color = Color.WHITE;
                    }
                    bitmap.setPixel(x, y, color);
                }
            }
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}