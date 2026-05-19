package com.system.pro;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private EditText newItemEditText;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        listView = findViewById(R.id.listView);
        newItemEditText = findViewById(R.id.newItemEditText);
        addButton = findViewById(R.id.addButton);

        // Initialize data source
        items = new ArrayList<>();
        // Add some initial items for demonstration
        items.add("Buy groceries");
        items.add("Finish System Pro project");
        items.add("Call mom");

        // Initialize ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        // Set OnClickListener for the Add Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = newItemEditText.getText().toString().trim();
                if (!newItem.isEmpty()) {
                    items.add(newItem);
                    adapter.notifyDataSetChanged(); // Notify adapter that data has changed
                    newItemEditText.setText(""); // Clear the input field
                    Toast.makeText(MainActivity.this, "Item added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter an item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnItemLongClickListener for the ListView to remove items
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String removedItem = items.get(position);
            items.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, removedItem + " removed!", Toast.LENGTH_SHORT).show();
            return true; // Indicate that the long click was consumed
        });
    }
}