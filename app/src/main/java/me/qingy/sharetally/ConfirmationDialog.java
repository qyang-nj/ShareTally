package me.qingy.sharetally;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by YangQ on 10/2/2014.
 *
 * A Dialog showing a prompt message and two buttons (OK/Cancel).
 */
public class ConfirmationDialog extends DialogFragment {
    private CharSequence mMessage;
    private DialogInterface.OnClickListener mOnOK;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setPositiveButton(android.R.string.ok, mOnOK)
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    /* Has to be invoked before calling show(). */
    public ConfirmationDialog setArguments(CharSequence message, DialogInterface.OnClickListener onOK) {
        mMessage = message;
        mOnOK = onOK;
        return this;
    }
}
