package com.harunkor.androidsslpinningjavasample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.result);


        //CertifatePinnerWay();

        //TrustManagerOldWay();

        // XML Network Security Configuration (Android 7.0 (API level 24 or higher))


    }

    private void TrustManagerOldWay(){
        InputStream resourceStream = getResources().openRawResource(R.raw.githubdemo);
        String keyStoreType= KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore= KeyStore.getInstance(keyStoreType);
            keyStore.load(resourceStream,null);
            String trustManagerAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(trustManagerAlgorithm);
            trustManagerFactory.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null,trustManagerFactory.getTrustManagers(),null);
            URL url = new URL("https://api.github.com/");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        }catch (Exception e){

        }



    }

    private void CertifatePinnerWay(){

        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add("api.github.com","sha256/uyPYgclc5Jt69vKu92vci6etcBDY8UNTyrHQZJpVoZY=").build();

        OkHttpClient client = httpBuilder.certificatePinner(certificatePinner).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService userService = retrofit.create(UserService.class);

        Call<User> call = userService.getUser("harunkor");

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                textView.setText(response.body().getId().toString());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });




    }
}