package cl.cadcc.folio.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cl.cadcc.folio.R;

import static cl.cadcc.folio.R.id.rutField;
import static cl.cadcc.folio.R.id.sendRut;


public class WelcomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public WelcomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        EditText edit_txt = (EditText) v.findViewById(rutField);
        final Button submit_btn = (Button) v.findViewById(R.id.sendRut);
        edit_txt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit_btn.performClick();
                    return true;
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.startNfcReader();
            mListener.setStartFragment(true);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.startNfcReader();
        mListener.onNfcDetectorChange();
    }

    public void onPause() {
        super.onResume();
        mListener.stopNfcReader();
        mListener.onNfcDetectorChange();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.setStartFragment(false);
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void startNfcReader();
        public void stopNfcReader();
        public void onNfcDetectorChange();
        public void setStartFragment(boolean value);
    }
}
