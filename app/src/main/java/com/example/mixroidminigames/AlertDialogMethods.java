package com.example.mixroidminigames;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

public class AlertDialogMethods {
    private AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    private View view = null;
    private Context context = null;

    public AlertDialogMethods(Context context) {
        this.context = context;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void alertDismiss() {
        alertDialog.dismiss();
    }

    public void showDialog(boolean isCancelable) {
        if (view == null) return;
        builder = new AlertDialog.Builder(context);
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.setCancelable(isCancelable);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
}
