package com.example.orca.howbeautyiam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MyRankActivity extends AppCompatActivity {

    ListView list;
    String[] itemname ={
            "Safari",
            "Camera",
            "Global",
            "FireFox",
            "UC Browser",
            "Android Folder",
            "VLC Player",
            "Cold War",
            "asdasfa",
            "asdasf",
            "asdasertrt",
            "asdaseasfafs",
            "asdasdasfasde",
            "asdawreafasd",
    };

    Integer[] imgid={
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
    };

    String[] points={
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123",
            "123"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rank);

        CustomListAdapterRank adapter=new CustomListAdapterRank(this, itemname, imgid, points);
        list=(ListView)findViewById(R.id.list2);
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
