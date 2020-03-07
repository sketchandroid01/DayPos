package com.daypos.fragments.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daypos.R;
import com.daypos.localdb.DatabaseHelper;
import com.daypos.localdb.PrinterData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrinterListActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_list);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Printers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon_back);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cat, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.add_cat:

                Intent intent = new Intent(PrinterListActivity.this, PrinterAddActivity.class);
                startActivity(intent);

                break;

        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    protected void onResume() {
        setPrinterData();
        super.onResume();
    }

    private void setPrinterData(){

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList<PrinterData> list = databaseHelper.getAllPrinters();

        PrinterListAdapter printerListAdapter =
                new PrinterListAdapter(PrinterListActivity.this, list);
        recyclerview.setAdapter(printerListAdapter);

    }


}
