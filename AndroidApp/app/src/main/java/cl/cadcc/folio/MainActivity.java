package cl.cadcc.folio;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cl.cadcc.folio.api.ApiHttps;
import cl.cadcc.folio.fragments.DoneFragment;
import cl.cadcc.folio.fragments.ErrorFragment;
import cl.cadcc.folio.fragments.IdentityFragment;
import cl.cadcc.folio.fragments.ServerConnectFragment;
import cl.cadcc.folio.fragments.WelcomeFragment;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener {

    private BroadcastReceiver nfcChangedReceiver;
    boolean startFragment = true;

    private JsonHttpResponseHandler folioHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Log.d("testAPISUCCESS",response.toString());
            Fragment identityFragment = new IdentityFragment();
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.replace(R.id.folio_fragment,identityFragment , "identify");
            t.commit();
            getSupportFragmentManager().executePendingTransactions();
            try {
                //int code = response.getInt("code");
                //if (code!=200) {
                //    onFailure(code,headers,new Throwable(),response);
                //}
                JSONObject entity = response;
                //int id = entity.getInt("id");
                String lastname = entity.getString("lastName");
                String name = entity.getString("fullName");
                String rut = entity.getString("rut");
                String career = entity.getString("department");
                TextView nameView = (TextView)findViewById(R.id.name);
                TextView rutView = (TextView)findViewById(R.id.rut);
                TextView careerView = (TextView)findViewById(R.id.career);
                nameView.setText(name+" "+lastname);
                rutView.setText(rut);
                careerView.setText(career);
            } catch (Exception e) {
                e.printStackTrace();
                //onFailure(400,headers,e,response);
            }
        };

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
            // Pull out the first event on the public timeline
            JSONObject firstEvent = null;
            String tweetText = null;
            try {
                firstEvent = (JSONObject) timeline.get(0);
                tweetText = "Votante ya participó de esta votación";
            } catch (JSONException e) {
                e.printStackTrace();
            }


            // Do something with the response
            Log.d("testAPI","array");
            Fragment errorFragment = new ErrorFragment();
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.replace(R.id.folio_fragment,errorFragment , "error");
            t.commit();
            getSupportFragmentManager().executePendingTransactions();
            TextView error = (TextView)findViewById(R.id.error_data);
            String errorText = tweetText;
            error.setText("Error: " + errorText);
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("testAPI","Error");
            Fragment errorFragment = new ErrorFragment();
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            t.replace(R.id.folio_fragment,errorFragment , "error");
            t.commit();
            getSupportFragmentManager().executePendingTransactions();
            TextView error = (TextView)findViewById(R.id.error_data);
            String errorText;
            try {
                errorText = errorResponse.toString();
            } catch (Exception e) {
                // La respuesta era nula
                e.printStackTrace();
                errorText = "No es posible conectarse al servidor";
            }
            error.setText("Error: " + errorText);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment welcomeFragment = new WelcomeFragment();
        setContentView(R.layout.activity_main);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        t.replace(R.id.folio_fragment,welcomeFragment , "welcome");
        t.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onNfcDetectorChange() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
        TextView tuiTextView = (TextView) findViewById(R.id.textView_tui);

        if (tuiTextView == null) return;

        if (nfcAdapter == null) {
            tuiTextView.setText("Lo sentimos, tu celular no tiene NFC.");
        } else if (!nfcAdapter.isEnabled()) {
            tuiTextView.setText("NFC está deshabilitado, habilítalo, por favor.");
        } else {
            tuiTextView.setText("Acerca la tarjeta, por favor.");
        }
    }

    public void startNfcReader() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) return;

        int flags = NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_A;

        nfcAdapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                final String cardId = bytesToHexString(tag.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String tuiId=cardId.substring(2, cardId.length()).toUpperCase();
                        try {
                            Log.d("testTUID",tuiId);
                            findUser(tuiId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, flags, null);
    }

    public void stopNfcReader() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) return;
        nfcAdapter.disableReaderMode(this);
    }

    private String bytesToHexString(byte[] src) {

        if (src == null || src.length <= 0) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder("0x");

        char[] buffer = new char[2];
        for (byte aSrc : src) {
            buffer[0] = Character.forDigit((aSrc >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(aSrc & 0x0F, 16);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }


    public void findUser(String tuiId) throws JSONException {
        changeToConnectingFragment();
        tuiId = tuiId.length() == 0 ? "null" : tuiId;
        ApiHttps.get("/voteTui/1/1/"+tuiId+"/", null, folioHandler);
    }
    public void voteRut(String rut) {
        changeToConnectingFragment();
        rut = rut.length() == 0 ? "null" : rut;
        ApiHttps.get("/voteRut/1/1/"+rut+"/", null, folioHandler);
    };

    public void changeToConnectingFragment() {
        stopNfcReader();
        Fragment serverConnectFragment = new ServerConnectFragment();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        t.replace(R.id.folio_fragment,serverConnectFragment , "connect");
        t.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void manuallySendId(View v) {
        EditText mEdit = (EditText)findViewById(R.id.rutField);
        String rut = mEdit.getText().toString();
        try {
            voteRut(rut);
        } catch (Exception e) {
            Toast.makeText(this,"No se pudo encontrar este rut!",Toast.LENGTH_SHORT);
        }
    }

    public void sendFolio(View v) throws JSONException {
        TextView rutView = (TextView)findViewById(R.id.rut);
        String rut = rutView.getText().toString();
        EditText mEdit = (EditText)findViewById(R.id.folioField);
        String folio = mEdit.getText().toString();
        String url = "/voteRut/1/1/"+rut+"/"+folio+"/";
        Log.d("Adderou",url);
        Fragment serverConnectFragment = new ServerConnectFragment();
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        t.replace(R.id.folio_fragment,serverConnectFragment , "connect");
        t.commit();
        getSupportFragmentManager().executePendingTransactions();
        ApiHttps.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("testAPISUCCESS",response.toString());
                Fragment identityFragment = new DoneFragment();
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                t.replace(R.id.folio_fragment,identityFragment , "done");
                t.commit();
                getSupportFragmentManager().executePendingTransactions();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = null;
                String tweetText = null;
                try {
                    firstEvent = (JSONObject) timeline.get(0);
                    tweetText = firstEvent.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // Do something with the response
                Log.d("testAPI","array");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("testAPI","Error");
                Fragment errorFragment = new ErrorFragment();
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                t.replace(R.id.folio_fragment,errorFragment , "connect");
                t.commit();
                getSupportFragmentManager().executePendingTransactions();
                TextView error = (TextView)findViewById(R.id.error_data);
                error.setText("Error: No se pudo registrar el voto.");
            }
        });
    }

    public void setStartFragment(boolean value) {
        startFragment = value;
    }

    @Override
    public void onBackPressed() {
        if (!startFragment) {
            this.recreate();
        } else {
            super.onBackPressed();
        }
    }
}
