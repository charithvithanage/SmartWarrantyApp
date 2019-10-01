package com.info.charith.smartwarrantyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.info.charith.smartwarrantyapp.R;
import com.info.charith.smartwarrantyapp.Utils;

public class MessageActivity extends AppCompatActivity {

    String previous_activity;
    String type;
    Button btnSubmit;
    TextView tvEnglish, tvSinhala, tvTamil,tvHotline;
    TextView titleView;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Utils.changeStatusBarColor(MessageActivity.this,getWindow());

        type = getIntent().getStringExtra("type");
        previous_activity = getIntent().getStringExtra("previous_activity");

        init();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("activated device") && previous_activity.equals("scan_activity")) {
                    Intent intent=new Intent(MessageActivity.this, NewDeiveActivity.class);
                    intent.putExtra("type",type);
                    intent.putExtra("previous_activity","message_activity");
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(MessageActivity.this, MainActivity.class);
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

        if (type.equals("activated device") && previous_activity.equals("scan_activity")) {
            tvEnglish.setText("This device is already activated. Please enter customer information");
            tvSinhala.setText("මෙම උපාංගය දැනටමත් සක්රිය කර ඇත. කරුණාකර පාරිභෝගික තොරතුරු ඇතුළත් කරන්න");
            tvTamil.setText("இந்த சாதனம் ஏற்கனவே செயல்படுத்தப்பட்டுள்ளது. வாடிக்கையாளர் தகவலை உள்ளிடவும்");

        } else if (type.equals("unauthorized device") && previous_activity.equals("scan_activity")) {
            tvEnglish.setText("Sorry, this device is not supported due to unauthorized importation");
            tvSinhala.setText("කණගාටුයි, අනවසර ආනයනය හේතුවෙන් මෙම උපාංගයට සහය නොදක්වයි");
            tvTamil.setText("மன்னிக்கவும், அங்கீகரிக்கப்படாத இறக்குமதி காரணமாக இந்த சாதனம் ஆதரிக்கப்படவில்லை");

        }        else if (type.equals("disabled device") && previous_activity.equals("scan_activity")) {
            tvEnglish.setText("Sorry, this device is not supported,Please contact SmartWarranty");
            tvSinhala.setText("කණගාටුයි, මෙම උපාංගයට සහය නොදක්වයි, කරුණාකර SmartWarranty අමතන්න");
            tvHotline.setVisibility(View.VISIBLE);

            tvTamil.setText("மன்னிக்கவும், இந்த சாதனம் ஆதரிக்கப்படவில்லை, தயவுசெய்து ஸ்மார்ட் வாரண்டியைத் தொடர்பு கொள்ளவும்");
        }

    }

    private void init() {
        titleView = findViewById(R.id.title_view);
        titleView.setText(type);

        backBtn = findViewById(R.id.backBtn);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvEnglish = findViewById(R.id.tvEnglish);
        tvSinhala = findViewById(R.id.tvSinhala);
        tvTamil = findViewById(R.id.tvTamil);
        tvHotline = findViewById(R.id.tvHotline);
        tvHotline.setVisibility(View.GONE);

    }
}
