package org.researchstack.molemapper.ui;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import org.researchstack.molemapper.R;
import org.researchstack.molemapper.models.Mole;

/**
 * TODO Refactor into class
 */
public class MoleEditDialog
{

    public static Dialog newInstance(Context context, Mole mole, boolean allowEdit, MoleEditCallbacks callbacks)
    {
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(context,
                R.style.Dialog_Body));
        View layout = inflater.inflate(R.layout.dialog_edit, null);

        Dialog dialog = new AlertDialog.Builder(context, R.style.Dialog_Body).show();
        dialog.setCancelable(true);

        layout.findViewById(R.id.dialog_mole_cancel).setOnClickListener(v -> {
            dialog.dismiss();
            callbacks.onCancel();
        });

        View remove = layout.findViewById(R.id.dialog_edit_remove);
        if(allowEdit)
        {
            remove.setOnClickListener(v -> {
                dialog.dismiss();
                callbacks.onDeleteMole();
            });
        }
        else
        {
            remove.setVisibility(View.GONE);
        }

        EditText editText = (EditText) layout.findViewById(R.id.dialog_edit_edittext);
        editText.setText(mole.moleName);
        layout.findViewById(R.id.dialog_mole_update).setOnClickListener(v -> {
            dialog.dismiss();

            String newTitle = editText.getText().toString();

            // Update title if its not empty and not the same
            if(! TextUtils.isEmpty(newTitle) && ! newTitle.equals(mole.moleName))
            {
                callbacks.onUpdateMoleName(newTitle);
            }

            // Clear Focus
            editText.clearFocus();
        });

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.setContentView(layout);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        return dialog;
    }

    public interface MoleEditCallbacks
    {
        void onCancel();

        void onDeleteMole();

        void onUpdateMoleName(String newTitle);
    }
}
