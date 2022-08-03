package com.urrecliner.shopcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout_views_list;
    ArrayAdapter<String> itemAdapter;
    ArrayList<String> itemArray;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefsEditor;
    static ArrayList<ShopItem> shopItems = null;
    public static class ShopItem implements Comparable<ShopItem>{
        String shopName;
        String shopGrp;
        String shopAddTag;
        int shopCost;

        @Override
        public int compareTo(ShopItem o) {
            return ((shopGrp + shopName).compareTo(o.shopGrp + o.shopName));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout_views_list = findViewById(R.id.linearLayout_views_list);
        sharedPref = getApplicationContext().getSharedPreferences("shop", MODE_PRIVATE);
        prefsEditor = sharedPref.edit();
        reload_shopList();
    }

    private void reload_shopList() {
        String itemList = sharedPref.getString("items","감자;건빵;견과;계란;고추장;깐마늘;나또;납작귀리;납작만두;누룽지;도시락김;두부;등갈비;땅콩;라텍스 장갑;락스;로션;마그네슙;매운고추;메밀국수;멸치;모닝롤;몽고간장;무;물;물만두;물만두;물비누;미역줄기;발사믹;비타민 디;빠다;새송이버섯;새우;새우완탕;스킨;스킨;시리얼;식초;쌀;쌀국수;아르헨티나 새우;아보카도;아보카도 오일;양배추;양파;어묵;오메가3;오분도미;오이고추;올리브유;우루오스;우유;인절미 과자;장조림고기;정수기필터;차돌박이;청양고추;카무트;커피캡슐;콩나물;포도;해물쌀국수;해초샐러드;햄프시드;황태채;휴지"
);
        itemArray = new ArrayList<>();
        Collections.addAll(itemArray, itemList.split(";"));
        itemAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1
                , itemArray);
        for (int i = 0; i < 30; i++) addMoreLine();
        getShoppingItems();
        showShoppingItems();
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

    private void showShoppingItems() {
        if (shopItems.size() == 0)
            return;
        for (int i=0; i<shopItems.size(); i++) {
            View view = linearLayout_views_list.getChildAt(i);
            ImageView addOrNotIV = view.findViewById(R.id.addOrNot);
            ImageView groupIV = view.findViewById(R.id.itemGrp);
            AutoCompleteTextView txtV = view.findViewById(R.id.itemName);
            EditText costET = view.findViewById(R.id.itemCost);
            ShopItem shopItem = shopItems.get(i);

            groupIV.setImageResource((shopItem.shopGrp.equals("1")? R.drawable.one : R.drawable.two));
            groupIV.setTag(shopItem.shopGrp);
            txtV.setText(shopItem.shopName);
            costET.setText(""+shopItem.shopCost);
            if (shopItem.shopAddTag.equals("+")) {
                addOrNotIV.setTag("+");
                addOrNotIV.setImageResource(R.mipmap.check_on);
                costET.setTextColor(0xFFFFFF00);
            } else {
                addOrNotIV.setTag("-");
                addOrNotIV.setImageResource(R.mipmap.check_off);
                costET.setTextColor(0xCC92C49C);
            }
        }
    }

    private void calculateSum() {
        int grp1Price = 0, grp2Price = 0;
        shopItems = new ArrayList<>();
        for (int i=0; i<linearLayout_views_list.getChildCount(); i++) {
            View view = linearLayout_views_list.getChildAt(i);
            ImageView iv = view.findViewById(R.id.addOrNot);
            String addTag = iv.getTag().toString();
            ImageView grpV = view.findViewById(R.id.itemGrp);
            AutoCompleteTextView txtV = view.findViewById(R.id.itemName);
            String itemName = txtV.getText().toString();
            String grpTag = grpV.getTag().toString();
            EditText tvPrice = view.findViewById(R.id.itemCost);
            int cost = Integer.parseInt("0" + tvPrice.getText().toString());
            if (cost > 0) {
                ShopItem chronoLog = new ShopItem();
                chronoLog.shopName = itemName;
                chronoLog.shopGrp = grpTag;
                chronoLog.shopCost = cost;
                chronoLog.shopAddTag = addTag;
                shopItems.add(chronoLog);
            }
            if (addTag.equals("+")) {
                if (grpTag.equals("1"))
                    grp1Price += cost;
                else
                    grp2Price += cost;
            }
        }
        TextView tv1 = findViewById(R.id.sumValue1);tv1.setText(grp1Price+"원");
        TextView tv2 = findViewById(R.id.sumValue2);tv2.setText(grp2Price+"원");
        putShoppingItems();
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
                prefsEditor.putString("items",sb.toString());
                prefsEditor.apply();
                prefsEditor.commit();
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

        int itemId = item.getItemId();
        if (itemId == R.id.add_Item) {
            for (int i = 0; i < 5; i++)
                addMoreLine();

        } else if (itemId == R.id.check_Item) {
            Intent intent = new Intent(this, ItemDeleteActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.erase) {
            shopItems = new ArrayList<>();
            putShoppingItems();
            finish();
            Intent newStart = new Intent(this, MainActivity.class);
            startActivity(newStart);

        } else if (itemId == R.id.reload) {
            reload_shopList();

        } else if (itemId == R.id.sort) {
            Collections.sort(shopItems);
            putShoppingItems();
            finish();
            Intent sorted = new Intent(this, MainActivity.class);
            startActivity(sorted);
        }
        return super.onOptionsItemSelected(item);
    }

    void addMoreLine() {
        int lnPos = linearLayout_views_list.getChildCount() % 3;
        lnPos = 0x343454 + lnPos * 0x1F0000 + lnPos * 0x1F00 + lnPos * 0x2F;
        lnPos |= 0xff000000;
//        Log.w("color",Integer.toHexString(lnPos));
        View shopView = getLayoutInflater().inflate(R.layout.shopping_list, null, false);
        shopView.setBackgroundColor(lnPos);
//        ImageView ivSearch = (ImageView) shopView.findViewById(R.id.search);
//        ivSearch.setTag("-");
//        EditText evName = shopView.findViewById(R.id.itemName);
        linearLayout_views_list.addView(shopView);

        ImageView ivGrp = (ImageView) shopView.findViewById(R.id.itemGrp);
        ivGrp.setTag("1");
        ivGrp.setOnClickListener(v -> {
            String tag = v.getTag().toString();
            if (tag.equals("1")) {
                v.setTag("2");
                ivGrp.setImageResource(R.drawable.two);
            } else {
                v.setTag("1");
                ivGrp.setImageResource(R.drawable.one);
            }
            calculateSum();
//              inearLayout_views_list.removeView(shopView);
        });


        AutoCompleteTextView evName = (AutoCompleteTextView) shopView.findViewById(R.id.itemName);
//        evName.setBackgroundColor(lnPos);
        evName.setAdapter(itemAdapter);
        evName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                calculateSum();
        });

        EditText evCost = shopView.findViewById(R.id.itemCost);
//        evPrice.setBackgroundColor(lnPos);
        evCost.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                calculateSum();
        });

        ImageView ivAddorNot = (ImageView) shopView.findViewById(R.id.addOrNot);
        ivAddorNot.setTag("+");
        ivAddorNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = v.getTag().toString();
                if (tag.equals("+")) {
                    v.setTag("-");
                    evCost.setTextColor(0xCC92C49C);
                    ivAddorNot.setImageResource(R.mipmap.check_off);
                } else {
                    v.setTag("+");
                    evCost.setTextColor(0xFFFFFF00);
                    ivAddorNot.setImageResource(R.mipmap.check_on);
                }
                calculateSum();
//              inearLayout_views_list.removeView(shopView);
            }
        });
    }

    void getShoppingItems() {

//        Gson gson = new Gson();
//        String json = sharedPref.getString("shop", "");
//        if (json.isEmpty()) {
//            shopItems = new ArrayList<>();
//        } else {
//            Type type = new TypeToken<List<ShopItem>>() {
//            }.getType();
//            shopItems = gson.fromJson(json, type);
//        }
        Gson gson = new Gson();
        shopItems = gson.fromJson(sharedPref.getString("shop", ""),
                new TypeToken<List<ShopItem>>() {}.getType());
        if (shopItems == null)
            shopItems = new ArrayList<>();
    }

    void putShoppingItems() {

        Gson gson = new Gson();
        String json = gson.toJson(shopItems);
        prefsEditor.putString("shop", json);
        prefsEditor.apply();
    }

}