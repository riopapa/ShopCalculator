package com.urrecliner.shopcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.ArrayList;
import java.util.Collections;

public class ItemDeleteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefsEditor;
    ArrayList<String> itemArray;
    static CheckAdapter checkAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        recyclerView = findViewById(R.id.recycleView);
        context = this;
        StaggeredGridLayoutManager SGL = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(SGL);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, SGL.getOrientation()));
        recyclerView.setLayoutManager(SGL);
        sharedPref = getApplicationContext().getSharedPreferences("shop", MODE_PRIVATE);
        prefsEditor = sharedPref.edit();
        String itemList = sharedPref.getString("items","");
        String []items = itemList.split(";");
        itemArray = new ArrayList<>();
        Collections.addAll(itemArray, items);
        checkAdapter = new CheckAdapter();
        recyclerView.setAdapter(checkAdapter);
    }

    @Override
    public void onBackPressed() {
        StringBuilder sb = new StringBuilder();
        for (String s: itemArray)
            if (s.length() > 0) sb.append(s).append(";");
        prefsEditor.putString("items",sb.toString());
        prefsEditor.apply();
        prefsEditor.commit();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {

        @Override
        public int getItemCount() {
            return itemArray.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            ViewHolder(final View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.checkItem);
                tv.setOnLongClickListener(v -> {
                    int pos = getAbsoluteAdapterPosition();
                    Toast.makeText(context, "상품 ["+itemArray.get(pos)+"] 삭제 됨", Toast.LENGTH_LONG).show();
                    itemArray.remove(pos);
                    checkAdapter.notifyDataSetChanged();
//                    Intent intent = new Intent(getApplicationContext(), ItemDeleteActivity.class);
//                    startActivity(intent);
                    return true;
                });
            }
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.tv.setText(itemArray.get(position));
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_items, parent, false);
            return new ViewHolder(view);
        }
    }

}
