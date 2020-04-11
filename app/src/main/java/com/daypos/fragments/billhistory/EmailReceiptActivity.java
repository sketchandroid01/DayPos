package com.daypos.fragments.billhistory;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.daypos.R;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class EmailReceiptActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.edt_emailid)
    EditText edt_emailid;
    @BindView(R.id.iv_send)
    ImageView iv_send;

    private GlobalClass globalClass;
    String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_receipt);
        ButterKnife.bind(this);
        actionViews();

    }


    private void actionViews(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x);

        globalClass = (GlobalClass) getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            order_id = bundle.getString("order_id");
        }


        iv_send.setOnClickListener(v -> {
            if (edt_emailid.getText().toString().trim().length() > 0){
                sendEmailReceipt(edt_emailid.getText().toString());
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void sendEmailReceipt(String email) {

        String url = ApiConstant.email_invoice;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());
        params.put("order_id", order_id);
        params.put("email", email);
        params.put("store_id", "");

        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {

                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        Toasty.success(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();

                        finish();

                    }else {
                        Toasty.error(getApplicationContext(),
                                message, Toast.LENGTH_LONG, true).show();
                    }

                    Commons.hideSoftKeyboard(EmailReceiptActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

}
