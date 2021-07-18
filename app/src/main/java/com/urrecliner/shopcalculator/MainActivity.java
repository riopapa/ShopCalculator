package com.urrecliner.shopcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout_views_list;
    String [] items;
    ArrayAdapter<String> itemAdapter;
    ArrayList<String> itemArray;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout_views_list = findViewById(R.id.linearLayout_views_list);
        sp = this.getSharedPreferences("items",MODE_PRIVATE);
        String itemList = sp.getString("items","감자;건빵;견과;계란;고추장;깐마늘;나또;납작귀리;납작만두;누룽지;도시락김;두부;등갈비;땅콩;라텍스 장갑;락스;로션;마그네슙;매운고추;메밀국수;멸치;모닝롤;몽고간장;무;물;물만두;물만두;물비누;미역줄기;발사믹;비타민 디;빠다;새송이버섯;새우;새우완탕;스킨;스킨;시리얼;식초;쌀;쌀국수;아르헨티나 새우;아보카도;아보카도 오일;양배추;양파;어묵;오메가3;오분도미;오이고추;올리브유;우루오스;우유;인절미 과자;장조림고기;정수기필터;차돌박이;청양고추;카무트;커피캡슐;콩나물;포도;해물쌀국수;해초샐러드;햄프시드;황태채;휴지"
);
        items = itemList.split(";");
        itemArray = new ArrayList<String>();
        Collections.addAll(itemArray, items);
        itemAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1
                , itemArray);
        for (int i = 0; i < 15; i++) addMoreLine();
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
        for (int i=0; i<linearLayout_views_list.getChildCount(); i++) {
            View view = linearLayout_views_list.getChildAt(i);
            ImageView iv = view.findViewById(R.id.addOrNot);
            EditText tvPrice = view.findViewById(R.id.itemPrice);
            String tag = iv.getTag().toString();
//            Log.w("i="+i, "name="+tvName.getText().toString()+" price="+tvPrice.getText().toString()+" flag="+tag);
            if (tag.equals("+")) {
                int price = Integer.parseInt("0" + tvPrice.getText().toString());
                totPrice += price;
            }
        }
        TextView tv = findViewById(R.id.sumValue);
        tv.setText(totPrice+" 원");
    }

    @Override
    public void onBackPressed() {
        for (int i=0; i<linearLayout_views_list.getChildCount(); i++) {
            View view = linearLayout_views_list.getChildAt(i);
            AutoCompleteTextView tvName = (AutoCompleteTextView) view.findViewById(R.id.itemName);
            String item = tvName.getText().toString();
            if (checkAnyNew(item)) {
                itemArray.add(item);
                Collections.sort(itemArray);
                StringBuilder sb = new StringBuilder();
                for (String s: itemArray)
                    sb.append(s).append(";");
                SharedPreferences.Editor se = sp.edit();
                se.putString("items",sb.toString());
                se.apply();
                se.commit();
            }
        }
        finish();
        finishAffinity();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    boolean checkAnyNew(String newItem) {
        if (newItem.length() < 1)
            return false;
        for (String s : itemArray) {
            if (newItem.equals(s))
                return false;
        }
        return true;
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

        switch (item.getItemId()) {
            case R.id.add_Item:
                addMoreLine();
                addMoreLine();
                break;
            case R.id.check_Item:
                finish();
                Intent intent = new Intent(this, ItemDeleteActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void addMoreLine() {
        int lnPos = linearLayout_views_list.getChildCount() % 3;
        lnPos = 0xA4A4A4 + lnPos * 0xF0000 + lnPos * 0xF00 + lnPos * 15;
        lnPos |= 0xff000000;
//        Log.w("color",Integer.toHexString(lnPos));
        View shopView = getLayoutInflater().inflate(R.layout.shopping_list, null, false);
        shopView.setBackgroundColor(lnPos);
//        ImageView ivSearch = (ImageView) shopView.findViewById(R.id.search);
//        ivSearch.setTag("-");
//        EditText evName = shopView.findViewById(R.id.itemName);
        linearLayout_views_list.addView(shopView);

        AutoCompleteTextView evName = (AutoCompleteTextView) shopView.findViewById(R.id.itemName);
//        evName.setBackgroundColor(lnPos);
        evName.setAdapter(itemAdapter);
        evName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    calculateSum();
            }
        });

        EditText evPrice = shopView.findViewById(R.id.itemPrice);
//        evPrice.setBackgroundColor(lnPos);
        evPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    calculateSum();
            }
        });

        ImageView ivAdd = (ImageView) shopView.findViewById(R.id.addOrNot);
        ivAdd.setTag("+");
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