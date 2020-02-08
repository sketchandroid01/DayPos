package com.daypos.fragments.products;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daypos.R;
import com.daypos.fragments.category.CategoryData;
import com.daypos.fragments.category.ColorAdapter;
import com.daypos.fragments.home.CategorySpinnerAdapter;
import com.daypos.network.ApiConstant;
import com.daypos.network.PostDataParser;
import com.daypos.utils.Commons;
import com.daypos.utils.GlobalClass;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class AddProduct extends AppCompatActivity implements
        View.OnClickListener,
        ColorAdapter.ItemClickListener{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_colors) RecyclerView recycler_colors;
    @BindView(R.id.tv_save_cat) TextView tv_save_cat;
    @BindView(R.id.radio_color) RadioButton radio_color;
    @BindView(R.id.radio_image) RadioButton radio_image;
    @BindView(R.id.linear_select_image) LinearLayout linear_select_image;
    @BindView(R.id.edt_product_name) EditText edt_product_name;
    @BindView(R.id.edt_selling_price) EditText edt_selling_price;
    @BindView(R.id.edt_sku) EditText edt_sku;
    @BindView(R.id.edt_barcode) EditText edt_barcode;
    @BindView(R.id.spinner_category) Spinner spinner_category;
    @BindView(R.id.spinner_unit) Spinner spinner_unit;
    @BindView(R.id.ll_gallery) LinearLayout ll_gallery;
    @BindView(R.id.ll_camera) LinearLayout ll_camera;
    @BindView(R.id.iv_image) ImageView iv_image;

    private GlobalClass globalClass;

    private String category_id, unit_type, mCurrentPhotoPath;
    private String selected_color_code;
    private ArrayList<CategoryData> categoryDataArrayList;
    File p_image = null;

    String type_of_color_file = "1";


    private static final int CAMERA_REQUEST = 333;
    public static final int GALLERY_REQUEST = 222;
    public static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 342;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        initViews();
    }



    private void initViews() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        globalClass = (GlobalClass) getApplicationContext();

        recycler_colors.setVisibility(View.VISIBLE);
        linear_select_image.setVisibility(View.GONE);

        tv_save_cat.setOnClickListener(this);
        ll_gallery.setOnClickListener(this);
        ll_camera.setOnClickListener(this);

        selected_color_code = "#E2E2E2";
        ArrayList<String> colorList = new ArrayList<>();
        colorList.add("#E2E2E2");
        colorList.add("#F44336");
        colorList.add("#E91E63");
        colorList.add("#f5a108");
        colorList.add("#bdf158");
        colorList.add("#39b939");
        colorList.add("#448AFF");
        colorList.add("#9C27B0");

        recycler_colors.setLayoutManager(new GridLayoutManager(this, 4));

        ColorAdapter colorAdapter = new ColorAdapter(AddProduct.this,
                colorList, selected_color_code);
        recycler_colors.setAdapter(colorAdapter);
        colorAdapter.setClickListener(this);


        radio_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    type_of_color_file = "1";
                    recycler_colors.setVisibility(View.VISIBLE);
                    linear_select_image.setVisibility(View.GONE);
                }

            }
        });

        radio_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    type_of_color_file = "2";
                    recycler_colors.setVisibility(View.GONE);
                    linear_select_image.setVisibility(View.VISIBLE);
                }

            }
        });


        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Each");
        spinnerArray.add("Weight");
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                spinnerArray);
        spinner_unit.setAdapter(spinnerArrayAdapter);
        spinner_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    unit_type = "1";
                }else {
                    unit_type = "2";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        categoryDataArrayList = new ArrayList<>();
        getCategoryList();

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


    @Override
    public void onClick(View v) {

        if (v == tv_save_cat){

            if (edt_product_name.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter product name",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }

            if (edt_selling_price.getText().toString().trim().length() == 0){
                Toasty.info(getApplicationContext(),
                        "Enter product price",
                        Toast.LENGTH_SHORT, true).show();
                return;
            }


            addProduct();

        } else if (v == ll_gallery){

            List<String> permissionsList = new ArrayList<String>();

            if (ContextCompat.checkSelfPermission(AddProduct.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (permissionsList.size() > 0) {
                ActivityCompat.requestPermissions((Activity) AddProduct.this,
                        permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            } else {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, GALLERY_REQUEST);

            }


        } else if (v == ll_camera){

            checkPermission();
        }

    }


    @Override
    public void onItemClick(String color_code) {
        selected_color_code = color_code;
    }

    private void getCategoryList() {
        categoryDataArrayList = new ArrayList<>();

        String url = ApiConstant.category_list;

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", globalClass.getUserId());


        new PostDataParser(this, url, params, true, response -> {

            if (response != null) {
                try {
                    int status = response.optInt("status");
                    String message = response.optString("message");
                    if (status == 1) {

                        CategoryData categoryData = new CategoryData();

                        categoryData.setId("");
                        categoryData.setName("Select Category");
                        categoryData.setColor("");
                        categoryData.setItem_no("");
                        categoryDataArrayList.add(categoryData);

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++){
                            JSONObject object = data.getJSONObject(i);

                            categoryData = new CategoryData();

                            categoryData.setId(object.optString("id"));
                            categoryData.setName(object.optString("category_name"));
                            categoryData.setColor(object.optString("category_colour"));
                            categoryData.setItem_no(object.optString("items"));

                            categoryDataArrayList.add(categoryData);

                        }

                        CategorySpinnerAdapter categorySpinnerAdapter
                                = new CategorySpinnerAdapter(AddProduct.this, categoryDataArrayList);
                        spinner_category.setAdapter(categorySpinnerAdapter);

                    }

                    spinnerAction();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void spinnerAction(){

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0){
                    category_id = categoryDataArrayList.get(position).getId();
                }else {
                    category_id = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void addProduct(){

        ProgressDialog progressDialog = new ProgressDialog(AddProduct.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String url = ApiConstant.add_item;
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put("user_id", globalClass.getUserId());
        params.put("name", edt_product_name.getText().toString());
        params.put("category_id", category_id);
        params.put("sold_option", unit_type);
        params.put("price", edt_selling_price.getText().toString());
        params.put("cost", edt_selling_price.getText().toString());
        params.put("sku", edt_sku.getText().toString());
        params.put("bar_code", edt_barcode.getText().toString());
        params.put("item_color", selected_color_code);
        params.put("is_track", "0");
        params.put("is_composite", "0");
        params.put("store_availability", "1");
        params.put("item_attribute", type_of_color_file);  // 1 for color, 2 for image file

        try{

            params.put("content_image", p_image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

         Log.d(Commons.TAG , "URL "+url);
         Log.d(Commons.TAG , "params "+params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        cl.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        cl.post(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response != null) {
                    Log.d(Commons.TAG, "add_item- " + response.toString());
                    try {

                        progressDialog.dismiss();

                        int status = response.getInt("status");
                        String message = response.getString("message");

                        if (status == 1) {

                            Toasty.success(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();

                            Commons.hideSoftKeyboard(AddProduct.this);

                            finish();

                        }else {

                            Toasty.error(getApplicationContext(),
                                    message,
                                    Toast.LENGTH_SHORT, true).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toasty.error(getApplicationContext(),
                        "Server error",
                        Toast.LENGTH_LONG, true).show();
                progressDialog.dismiss();
            }
        });


    }


    ////
    private boolean checkPermission() {

        List<String> permissionsList = new ArrayList<String>();

        if (ContextCompat.checkSelfPermission(AddProduct.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(AddProduct.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(AddProduct.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) AddProduct.this,
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        } else {

            captureImage();

        }


        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                                grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                } else {

                    checkPermission();
                }

        }
    }


    private void captureImage(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            dispatchTakePictureIntent();
        } else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST);
        }*/


    }

    private void dispatchTakePictureIntent() {

        try {

            final String dir = Commons.getFolderDirectory();

            File file = new File(dir);
            if (!file.exists())
                file.mkdir();


            String files = dir + "/picture_product" +".jpg";
            File newfile = new File(files);

            p_image = newfile;

            Uri photoURI = FileProvider.getUriForFile(AddProduct.this,
                    "com.daypos", newfile);
            mCurrentPhotoPath = photoURI.toString();


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_REQUEST);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            try {

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                iv_image.setImageBitmap(bitmap);
                writeBitmap(bitmap);

            }catch (Exception e){
                e.printStackTrace();
            }

        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {


            try {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                Log.d(Commons.TAG , "photo 2 - " + photo);

                Glide.with(AddProduct.this).clear(iv_image);

                writeBitmap(photo);

            }catch (Exception e){
                e.printStackTrace();
            }


            /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){

                Glide.with(AddProduct.this).clear(iv_image);

                Uri uri = Uri.parse(mCurrentPhotoPath);
                Log.d(Commons.TAG , "photo 1 - " + uri);
                try {
                    getContentResolver().notifyChange(uri, null);
                    ContentResolver cr = getContentResolver();

                    Bitmap photo = android.provider.MediaStore.Images.Media.getBitmap(cr, uri);
                    writeBitmap(photo);

                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    iv_image.setImageBitmap(photo);

                }catch (Exception e){
                    e.printStackTrace();
                }

            } else{

                try {

                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    Log.d(Commons.TAG , "photo 2 - " + photo);

                    Glide.with(AddProduct.this).clear(iv_image);

                    writeBitmap(photo);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }*/

        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_CANCELED) {
            p_image = null;

        }

    }

    private void writeBitmap(Bitmap bitmap){

        bitmap = Commons.getResizedBitmap(bitmap, 300, 400);

        final String dir = Commons.getFolderDirectory();

        File file = new File(dir);
        if (!file.exists())
            file.mkdir();


        String files = dir + "/picture_product" +".jpg";
        File newfile = new File(files);

        try {

            iv_image.setImageBitmap(bitmap);

            newfile.delete();
            OutputStream outFile = null;
            try {

                p_image = newfile;

                outFile = new FileOutputStream(newfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                outFile.flush();
                outFile.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
