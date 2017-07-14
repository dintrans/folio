package cl.cadcc.folio.fragments;


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

/**
 * A simple {@link Fragment} subclass.
 */
public class IdentityFragment extends Fragment {


    public IdentityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_identity, container, false);
        EditText edit_txt = (EditText) v.findViewById(R.id.folioField);
        final Button submit_btn = (Button) v.findViewById(R.id.sendFolio);
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

}
