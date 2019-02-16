package net.itlogics.drawroute.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.itlogics.drawroute.R;

import static net.itlogics.drawroute.remote.RemoteRequest.ConfigValues.RequestTimeOut;

public class Common {

    public static Handler handler;
    public static Runnable runnable;
    public static CountDownTimer countDownTimer;
    public static boolean countDownTimerFinished = true;
    public static ProgressDialog pd;
    public static Context mContext;
    public static final long HandlerTimeout = 1000;

    public static class Messages {
        public static String
                SomeThingWentWrong = "Something went wrong, please try again later.",
                UsernameMandatory = "Username is mandatory!",
                UsernameInvalid = "Username is invalid!",
                PasswordInvalid = "Password is invalid!";

    }

    public static class AlertTitles {
        public static String
                mError = "Error",
                mSuccess = "Success";

    }

    public static void ShowErrorDialog(final String Title, final String Msg,
                                       final String btnTxt, Activity mActivity) {

        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert, null);

        // Set the custom layout as alert dialog view
        builder.setView(dialogView);

        // Get the custom alert dialog view widgets reference
        TextView tvAlertTitle = (TextView) dialogView.findViewById(R.id.tvAlertTitle);
        tvAlertTitle.setText(Title);
        ImageView icClose = (ImageView) dialogView.findViewById(R.id.icClose);
        TextView tvAlertMsg = (TextView) dialogView.findViewById(R.id.tvAlertMsg);
        tvAlertMsg.setText(Msg);
        Button btnDone = (Button) dialogView.findViewById(R.id.btnDone);
        btnDone.setText(btnTxt);


        // Create the alert dialog
        final AlertDialog dialog = builder.create();

        // Set positive/yes button click listener
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
            }
        });

        // Set negative/no button click listener
        icClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the alert dialog
                dialog.cancel();
            }
        });

        // Display the custom alert dialog on interface
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    // Method to convert the string
    public static String capitailizeWord(String str) {
        StringBuffer s = new StringBuffer();

        // Declare a character of space
        // To identify that the next character is the starting
        // of a new word
        char ch = ' ';
        for (int i = 0; i < str.length(); i++) {

            // If previous character is space and current
            // character is not space then it shows that
            // current letter is the starting of the word
            if (ch == ' ' && str.charAt(i) != ' ')
                s.append(Character.toUpperCase(str.charAt(i)));
            else
                s.append(str.charAt(i));
            ch = str.charAt(i);
        }

        // Return the string with trimming
        return s.toString().trim();
    }

    public static void startCountDown() {

        countDownTimer = new CountDownTimer(RequestTimeOut, 1000) {
            public void onTick(long millisUntilFinished) {
                countDownTimerFinished = false;
            }

            public void onFinish() {
                countDownTimerFinished = true;
            }

        }.start();
    }
}
