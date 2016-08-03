package games.chess;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;



@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OptionsFragment extends DialogFragment implements
        DialogInterface.OnClickListener {

    public static final String TAG = "OptionsFragment";

    public interface OptionsFragmentInterface {
        public void onOptionsFragmentResult(String level);
    }

    private String[] options = {"Tutorial", "About", "Reset"};
    private OptionsFragmentInterface optionsFragmentInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            optionsFragmentInterface = (OptionsFragmentInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OptionsFragmentInterface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please select");
        builder.setItems(options, this);
        AlertDialog alert = builder.create();

        return alert;
    }

    @Override
    public void onClick(DialogInterface dialog, int choice) {
        optionsFragmentInterface.onOptionsFragmentResult(options[choice]);
    }

}

