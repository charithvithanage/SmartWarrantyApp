package com.info.charith.smartwarrantyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Utils;

import org.apache.commons.lang3.StringUtils;

public class MessageActivity extends AppCompatActivity {

    String previous_activity, warrantyString, dealerString;
    String type;
    Button btnSubmit;
    TextView tvEnglish, tvSinhala, tvTamil, tvHotline;
    TextView titleView;
    ImageButton backBtn;
    ImageView imageView;
    ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Utils.changeStatusBarColor(MessageActivity.this, getWindow());

        type = getIntent().getStringExtra("type");
        previous_activity = getIntent().getStringExtra("previous_activity");
        warrantyString = getIntent().getStringExtra("warrantyString");
        dealerString = getIntent().getStringExtra("dealerString");

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
                if (type.equals("activated device") && previous_activity.equals("scan_activity")) {
                    Intent intent = new Intent(MessageActivity.this, NewDeiveActivity.class);
                    intent.putExtra("type", type);
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
                Utils.navigateWithoutHistory(MessageActivity.this,MainActivity.class);
            }
        });

        /**
         * Check type and previous activity
         * According to that display relevant message in the body
         */
        if (type.equals("activated device") && previous_activity.equals("scan_activity")) {
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.tick));
            tvEnglish.setText(Html.fromHtml("This device is already activated.<br/>Please enter customer information."));
            tvSinhala.setText(Html.fromHtml("මෙම උපාංගය දැනටමත් සක්රිය කර ඇත. <br/>කරුණාකර පාරිභෝගික තොරතුරු ඇතුළත් කරන්න."));
            tvTamil.setText("இந்த சாதனம் ஏற்கனவே செயல்படுத்தப்பட்டுள்ளது. வாடிக்கையாளர் தகவலை உள்ளிடவும்.");

        } else if (type.equals("unauthorized device") && previous_activity.equals("scan_activity")) {
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.cross));

            tvEnglish.setText(Html.fromHtml("Sorry, this device is not supported <br/>due to unauthorized importation."));
            tvSinhala.setText("කණගාටුයි, අනවසර ආනයනය හේතුවෙන් මෙම උපාංගයට සහය නොදක්වයි.");
            tvTamil.setText("மன்னிக்கவும், அங்கீகரிக்கப்படாத இறக்குமதி காரணமாக இந்த சாதனம் ஆதரிக்கப்படவில்லை.");

        } else if (type.equals("disabled device") && previous_activity.equals("scan_activity")) {
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.cross));
            tvEnglish.setText(Html.fromHtml("Sorry, this device is not supported, <br/>please contact SmartWarranty."));
            tvSinhala.setText(Html.fromHtml("කණගාටුයි, මෙම උපාංගයට සහය නොදක්වයි, <br/>කරුණාකර SmartWarranty අමතන්න."));
            tvHotline.setVisibility(View.VISIBLE);

            tvTamil.setText("மன்னிக்கவும், இந்த சாதனம் ஆதரிக்கப்படவில்லை, தயவுசெய்து ஸ்மார்ட் வாரண்டியைத் தொடர்பு கொள்ளவும்.");
        }

    }

    private void init() {
        titleView = findViewById(R.id.title_view);
        imageView = findViewById(R.id.imageView3);
        titleView.setText(Utils.stringCapitalize(type));
        backBtn = findViewById(R.id.backBtn);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvEnglish = findViewById(R.id.tvEnglish);
        tvSinhala = findViewById(R.id.tvSinhala);
        tvTamil = findViewById(R.id.tvTamil);
        tvHotline = findViewById(R.id.tvHotline);
        tvHotline.setVisibility(View.GONE);
        homeBtn = findViewById(R.id.btnHome);
        homeBtn.setVisibility(View.VISIBLE);
    }
}
