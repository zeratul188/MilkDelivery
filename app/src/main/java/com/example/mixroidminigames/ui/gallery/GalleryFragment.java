package com.example.mixroidminigames.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mixroidminigames.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private RadioGroup rgWeek;
    private RadioButton[] rdoWeek = new RadioButton[7];
    private TextView txtHouse, txtHouseMax;
    private ListView listView;
    private FloatingActionButton fabAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        rgWeek = root.findViewById(R.id.rgWeek);
        txtHouse = root.findViewById(R.id.txtHouse);
        txtHouseMax = root.findViewById(R.id.txtHouseMax);
        listView = root.findViewById(R.id.listView);
        fabAdd = root.findViewById(R.id.fabAdd);
        for (int i = 0; i < rdoWeek.length; i++) {
            int resource = getActivity().getResources().getIdentifier("rdoWeek"+(i+1), "id", getActivity().getPackageName());
            rdoWeek[i] = root.findViewById(resource);
        }

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MilkAddActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
