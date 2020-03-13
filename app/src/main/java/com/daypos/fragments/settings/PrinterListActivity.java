package com.daypos.fragments.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class PrinterListActivity extends AppCompatActivity implements
        PrinterListAdapter.ItemLongClickListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private DatabaseHelper databaseHelper;

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

        databaseHelper = new DatabaseHelper(this);

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

        ArrayList<PrinterData> list = databaseHelper.getAllPrinters();

        PrinterListAdapter printerListAdapter =
                new PrinterListAdapter(PrinterListActivity.this, list);
        recyclerview.setAdapter(printerListAdapter);
        printerListAdapter.setLongClickListener(this);

    }

    @Override
    public void onItemLongClick(PrinterData printerData) {
        dialogLogout(printerData);
    }

    public void dialogLogout(PrinterData printerData){

        AlertDialog.Builder builder = new AlertDialog.Builder(PrinterListActivity.this);
        builder.setTitle("DayPos");
        builder.setMessage("Are you sure you want to delete "+printerData.getPrinter_name()+"?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseHelper.deletePrinter(printerData.getId());
                setPrinterData();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }





}
