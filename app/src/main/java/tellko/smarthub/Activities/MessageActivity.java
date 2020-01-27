package tellko.smarthub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tellko.smarthub.R;
import tellko.smarthub.Utils;

public class MessageActivity extends AppCompatActivity {

    String previous_activity, warrantyString, dealerString;
    String type;
    Button btnSubmit;
    TextView titleView;
    ImageButton backBtn;
    ImageView imageView;
    ImageButton homeBtn;
    String waranntyRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Utils.changeStatusBarColor(MessageActivity.this, getWindow());

        type = getIntent().getStringExtra("type");
        previous_activity = getIntent().getStringExtra("previous_activity");
        warrantyString = getIntent().getStringExtra("warrantyString");
        dealerString = getIntent().getStringExtra("dealerString");
        waranntyRequest = getIntent().getStringExtra("waranntyRequest");

        init();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Check previous activity
                 * If the previous activity is scan activity and type is activated device
                 * Navigate to the new device activity to enter customer details
                 * Else navigate to home fragment
                 */

                btnSubmit.setEnabled(false);
                if (type.equals("activated device") && previous_activity.equals("scan_activity")) {
                    Intent intent = new Intent(MessageActivity.this, NewDeiveActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("waranntyRequest", waranntyRequest);
                    intent.putExtra("previous_activity", "message_activity");
                    intent.putExtra("warrantyString", warrantyString);
                    intent.putExtra("dealerString", dealerString);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.navigateWithoutHistory(MessageActivity.this, AppListActivity.class);
            }
        });

        /**
         * Check type and previous activity
         * According to that display relevant message in the body
         */
        if (type.equals("activated device") && previous_activity.equals("scan_activity")) {
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.already_activated));
        } else if (type.equals("unauthorized device") && previous_activity.equals("scan_activity")) {
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.invalid_importation));
        } else if (type.equals("disabled device") && previous_activity.equals("scan_activity")) {
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.not_supported)); }

    }

    private void init() {
        titleView = findViewById(R.id.title_view);
        imageView = findViewById(R.id.imageView3);
        titleView.setText(Utils.stringCapitalize(type));
        backBtn = findViewById(R.id.backBtn);
        btnSubmit = findViewById(R.id.btnSubmit);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setVisibility(View.VISIBLE);
    }
}
