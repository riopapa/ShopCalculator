package com.urrecliner.shopcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout_views_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout_views_list = findViewById(R.id.linearLayout_views_list);
        for (int i = 0; i < 10; i++) addMoreLine();
        calculateSum();

    }

//    public void onSubmitButtonClicked(View view) {
//        if (validation()) {
//            // Show data to recyclerView
//            RecyclerView recyclerView = findViewById(R.id.recyclerView);
//            CricketerAdapter adapter = new CricketerAdapter(MainActivity.this, shopItems);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//            recyclerView.setLayoutManager(linearLayoutManager);
//            recyclerView.setAdapter(adapter);
//        }
//    }

    private void calculateSum() {
        int totPrice = 0;
        if (linearLayout_views_list.getChildCount() == 0) {
            Toast.makeText(this, "Add Cricketers First!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i=0; i<linearLayout_views_list.getChildCount(); i++) {
            View view = linearLayout_views_list.getChildAt(i);
//            AutoCompleteTextView editTextCricketerName = (AutoCompleteTextView) view.findViewById(R.id.itemName);
            ImageView iv = view.findViewById(R.id.addOrNot);
            String tag = iv.getTag().toString();
            if (tag.equals("+")) {
                EditText tvPrice = view.findViewById(R.id.itemPrice);
                int price = Integer.parseInt("0" + tvPrice.getText().toString());
                totPrice += price;
            }
        }
        TextView tv = findViewById(R.id.sumValue);
        tv.setText(totPrice+" 원");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
//        MenuItem item = menu.findItem(R.id.showNowMap);
//        item.setVisible(modeStarted);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_Item:
                addMoreLine();
                addMoreLine();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void addMoreLine() {
        View shopView = getLayoutInflater().inflate(R.layout.shopping_list, null, false);

        // Init
        EditText evName = shopView.findViewById(R.id.itemName);
        EditText evPrice = shopView.findViewById(R.id.itemPrice);
//        AutoCompleteTextView evName = (AutoCompleteTextView) shopView.findViewById(R.id.itemName);
//        AutoCompleteTextView evPrice = (AutoCompleteTextView) shopView.findViewById(R.id.itemPrice);
        ImageView ivAdd = (ImageView) shopView.findViewById(R.id.addOrNot);
        ivAdd.setTag("+");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(shopView.getWidth(), shopView.getHeight());
        lp.setMargins(0,0,0,0);
//        shopView.setLayoutParams(lp);
        // Set the view
        linearLayout_views_list.addView(shopView);
        evPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    calculateSum();
            }
        });
        // Remove the view when click the delete image
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                if (tag.equals("+")) {
                    v.setTag("-");
                    ivAdd.setImageResource(R.mipmap.check_off);
                } else {
                    v.setTag("+");
                    ivAdd.setImageResource(R.mipmap.check_on);
                }
                calculateSum();
//              inearLayout_views_list.removeView(shopView);
            }
        });
    }

}