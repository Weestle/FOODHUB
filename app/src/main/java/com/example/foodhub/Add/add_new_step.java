package com.example.foodhub.Add;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.foodhub.R;

import java.util.ArrayList;

public class add_new_step extends Fragment {
    private EditText     addDesc;
    private Button       confirmAdd, canselAdd;
    private NumberPicker secPicker, minPicker, hourPicker;
    private CheckBox     checkBox;
    private String       imageUri;
    private Bundle       bundle, loadBundle;


    public add_new_step() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Темная тема
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        bundle     = new Bundle();
        loadBundle = new Bundle();
        loadBundle = this.getArguments();

        View view = inflater.inflate(R.layout.fragment_add_new_step, container, false);

        addDesc     = view.findViewById(R.id.editAddDesc);
        confirmAdd  = view.findViewById(R.id.confirmAddingStep);
        canselAdd   = view.findViewById(R.id.canselAddingStep);
        secPicker   = view.findViewById(R.id.addStepSecPicker);
        minPicker   = view.findViewById(R.id.addStepMinPicker);
        hourPicker  = view.findViewById(R.id.addStepHourPicker);
        checkBox    = view.findViewById(R.id.addRecipeCheckBox);

        secPicker.setMaxValue(59);
        secPicker.setMinValue(0);
        secPicker.setValue(0);

        minPicker.setMaxValue(59);
        minPicker.setMinValue(0);
        minPicker.setValue(0);

        hourPicker.setMaxValue(24);
        hourPicker.setMinValue(0);
        minPicker.setValue(0);

        Bundle finalLoadBundle = loadBundle;

        // Показивать ползунки со временем
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    secPicker.setVisibility(View.VISIBLE);
                    minPicker.setVisibility(View.VISIBLE);
                    hourPicker.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                else {
                    secPicker.setVisibility(View.GONE);
                    minPicker.setVisibility(View.GONE);
                    hourPicker.setVisibility(View.GONE);
                }
            }
        });

        // Забрать данные из бандла
        imageUri = loadBundle.getString("main_image_uri");
        ArrayList<String>  step_desc = loadBundle.getStringArrayList("step_desc_list");
        ArrayList<Integer> step_sec  = loadBundle.getIntegerArrayList("step_sec_list");
        ArrayList<Integer> step_min  = loadBundle.getIntegerArrayList("step_min_list");
        ArrayList<Integer> step_hour = loadBundle.getIntegerArrayList("step_hour_list");

        // Кнопка добавить
        assert loadBundle != null;
        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (addDesc.getText().toString().trim().length() == 0) {
                    addDesc.setError("Это поле не должно быть пустым");
                    addDesc.requestFocus();
                    return;
                }

                step_desc.add(addDesc.getText().toString().trim());
                step_sec .add(secPicker.getValue());
                step_min .add(minPicker.getValue());
                step_hour.add(hourPicker.getValue());

                // Заполненеи бандла
                bundle.putStringArrayList("step_desc_list", step_desc);
                bundle.putIntegerArrayList("step_min_list", step_min);
                bundle.putIntegerArrayList("step_sec_list", step_sec);
                bundle.putIntegerArrayList("step_hour_list", step_hour);
                bundle.putString("recipe_name", finalLoadBundle.getString("recipe_name"));
                bundle.putString("recipe_desc", finalLoadBundle.getString("recipe_desc"));
                bundle.putString("main_image_uri", imageUri);

                // Переход на новый фрагмент
                Fragment addrecipe = new AddRecipeFragmentLayout();
                addrecipe.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                fragmentTransaction.replace(R.id.addNewRecipeHostLayout, addrecipe);
                fragmentTransaction.commit();
            }
        });

        // Кнопка отмены
        canselAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Заполнение бандла
                bundle.putStringArrayList("step_desc_list", step_desc);
                bundle.putIntegerArrayList("step_min_list", step_min);
                bundle.putIntegerArrayList("step_sec_list", step_sec);
                bundle.putIntegerArrayList("step_hour_list", step_hour);
                bundle.putString("recipe_name", finalLoadBundle.getString("recipe_name"));
                bundle.putString("recipe_desc", finalLoadBundle.getString("recipe_desc"));
                bundle.putString("main_image_uri", imageUri);

                // Переход на новый фрагмент
                Fragment addrecipe = new AddRecipeFragmentLayout();
                addrecipe.setArguments(bundle);
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.addNewRecipeHostLayout, addrecipe);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}