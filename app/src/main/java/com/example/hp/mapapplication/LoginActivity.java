package com.example.hp.mapapplication;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.gson.JsonObject;
import java.net.URISyntaxException;
import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamFactory;
import io.deepstream.DeepstreamRuntimeErrorHandler;
import io.deepstream.Event;
import io.deepstream.LoginResult;
import io.deepstream.Topic;

/**
 * Created by hp on 13-10-2017.
 */

public class LoginActivity extends AppCompatActivity
 {

    private AutoCompleteTextView EmailView;
    private EditText PasswordView;
    private UserLoginTask LoginTask = null;
    private Context ctx;
    private State state;
    private View ProgressView;
    private View LoginFormView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ctx = this;
        EmailView = (AutoCompleteTextView) findViewById(R.id.Tusername);
        PasswordView = (EditText) findViewById(R.id.Tpassword);
        PasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    Login();
                    return true;
                }
                return false;
            }


        });
        Button SignInButton = (Button) findViewById(R.id.TLogin);
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
        LoginFormView = findViewById(R.id.login_form);
        ProgressView = findViewById(R.id.login_progress);
        state = State.getInstance();


    }


    private void Login() {
        if (LoginTask != null) {
            return;
        }


        EmailView.setError(null);
        PasswordView.setError(null);

        String email = EmailView.getText().toString();
        String password = PasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            PasswordView.setError(getString(R.string.error_invalid_password));
            focusView = PasswordView;
            cancel = true;
        }


        if (TextUtils.isEmpty(email)) {
            EmailView.setError(getString(R.string.error_field_required));
            focusView = EmailView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        } else {

            LoginTask = new UserLoginTask(email, password);
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB)
                LoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                LoginTask.execute();


        }
    }


     public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String Email;
        private final String Password;


        UserLoginTask(String email, String password) {

            Email = email;
            Password = password;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            DeepstreamFactory factory = DeepstreamFactory.getInstance();
            DeepstreamClient client = null;
            try {
                client = factory.getClient(ctx.getString(R.string.app_url));

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String s) {
                    Log.w("dsh", "Error:" + event.toString() + ":" + s);
                }
            });

            JsonObject userData = new JsonObject();
            userData.addProperty("email",Email);
            userData.addProperty("password",Password);

            LoginResult result = client.login(userData);
            return result.loggedIn();
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            LoginTask = null;
            if (success) {
                state.setEmail(Email);
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            } else {
                PasswordView.setError(getString(R.string.error_incorrect_password));
                PasswordView.requestFocus();
            }
        }


    }
 }



