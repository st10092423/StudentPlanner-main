package com.studentplanner.studentplanner.utils;

import static com.studentplanner.studentplanner.utils.Helper.readStream;

import android.content.Context;
import android.text.Html;
import androidx.appcompat.app.AlertDialog;
import com.studentplanner.studentplanner.R;
import org.apache.commons.text.WordUtils;
public final class AlertDialogFragment {
    private final Context context;

    public AlertDialogFragment(Context context) {
        this.context = context;
    }

    public void showTermsPolicyError(){
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.accept_terms_and_conditions))
                .setPositiveButton(context.getString(R.string.ok), (dialog, which) -> dialog.cancel())
                .create()
                .show();
    }


}
