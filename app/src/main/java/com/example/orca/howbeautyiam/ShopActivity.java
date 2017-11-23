package com.example.orca.howbeautyiam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ShopActivity extends AppCompatActivity {

    ListView list;
    String[] itemname ={
            "Anew Ultimate Day Cream",
            "Anew Vital Skin Care Kit",
            "Avon 3D Lipstick",
            "Avon Attraction Set",
            "Avon Ideal Blusher",
            "Avon Ultra Color Lipstick",
            "Spectra Lash Mascara"
    };
    Integer[] imgid={
            R.drawable.shop1,
            R.drawable.shop2,
            R.drawable.shop3,
            R.drawable.shop4,
            R.drawable.shop5,
            R.drawable.shop6,
            R.drawable.shop7,
    };

    String[] points={
            "450 | 15$ for 15% discount.",
            "470 | 20$ for 15% discount.",
            "450 | 30$ for 15% discount.",
            "550 | 30$ for 15% discount.",
            "490 | 25$ for 15% discount.",
            "500 | 27$ for 15% discount.",
            "350 | 13$ for 15% discount.",
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        CustomListAdapter_shop adapter=new CustomListAdapter_shop(this, itemname, imgid, points);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= itemname[+position];
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
