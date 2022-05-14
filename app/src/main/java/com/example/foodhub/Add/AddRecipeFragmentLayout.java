package com.example.foodhub.Add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodhub.R;
import com.example.foodhub.Recipe;
import com.example.foodhub.Step;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddRecipeFragmentLayout extends Fragment {

    private int step = 1;
    private ArrayList<EditText> descriptions;
    private ArrayList<EditText> times;

    private EditText name, description;
    private static final int RESULT_OK = 3;
    private Button gallery, send, addStep;
    private ImageView picture;

    private AddRecipeAdapter addRecipeAdapter;
    private Uri imageUri;

    private String mainImageUri;

    private StorageReference storageReference;

    ArrayList<String> step_desc = new ArrayList<String>();
    ArrayList<Integer> step_sec= new ArrayList<Integer>();
    ArrayList<Integer> step_min= new ArrayList<Integer>();
    ArrayList<Integer> step_hour= new ArrayList<Integer>();

    ArrayList<Step> steps = new ArrayList<Step>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageUri = selectedImage;
            gallery.setError(null);
            picture.setImageURI(selectedImage);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe_layout, container, false);

        send        = view.findViewById(R.id.sendRecipeBtn);
        name        = view.findViewById(R.id.addRecipeName);
        description = view.findViewById(R.id.addRecipeDesc);
        picture     = view.findViewById(R.id.addRecipeImage);
        gallery     = view.findViewById(R.id.addRecipeImageButton);

        Bundle bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle.getStringArrayList("step_desc_list") != null) {
            name       .setText(bundle.getString("recipe_name"));
            description.setText(bundle.getString("recipe_desc"));

            step_min  = bundle.getIntegerArrayList("step_min_list");
            step_sec  = bundle.getIntegerArrayList("step_sec_list");
            step_hour = bundle.getIntegerArrayList("step_hour_list");


            if (bundle.getString("main_image_uri") != null) {
                imageUri = Uri.parse(bundle.getString("main_image_uri"));
            }

            picture.setImageURI(imageUri);


            step_desc = bundle.getStringArrayList("step_desc_list");
            if (step_desc != null) {
                for (int i = 0; i < step_desc.size(); i++) {
                    Step step = new Step(step_desc.get(i), step_sec.get(i), step_min.get(i), step_hour.get(i));
                    steps.add(step);
                }
            }
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRecipe();
            }
        });



        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.addrecipeview);
        addRecipeAdapter = new AddRecipeAdapter(getContext(), steps);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(addRecipeAdapter);

        addStep = (Button) view.findViewById(R.id.addRecipeNewStep);
        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle addStepBundle = new Bundle();


                addStepBundle.putString("recipe_name", name.getText().toString().trim());
                addStepBundle.putString("recipe_desc", description.getText().toString().trim());
                if (imageUri != null) {
                    addStepBundle.putString("main_image_uri", imageUri.toString());
                }


                step_desc.clear();
                step_min .clear();
                step_hour.clear();
                step_sec .clear();

                for (int i = 0; i < steps.size(); i++) {
                    step_desc.add(steps.get(i).getDesc());
                    step_sec.add(steps.get(i).getSec());
                    step_min.add(steps.get(i).getMin());
                    step_hour.add(steps.get(i).getHour());
                }

                addStepBundle.putStringArrayList("step_desc_list", step_desc);
                addStepBundle.putIntegerArrayList("step_sec_list", step_sec);
                addStepBundle.putIntegerArrayList("step_min_list", step_min);
                addStepBundle.putIntegerArrayList("step_hour_list", step_hour);

                Fragment ans = new add_new_step();
                ans.setArguments(addStepBundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.addNewRecipeHostLayout, ans);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });



        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_OK);
            }
        });
        return view;
    }

    private void sendRecipe() {
        if (name.getText().toString().trim().length() <2) {
            name.setError("Минимальная длина название 2 символа");
            name.requestFocus();
            return;
        }

        if (description.getText().toString().trim().length() == 0) {
            description.setError("Это поле не должно быть пустым");
            description.requestFocus();
            return;
        }

        if (steps.size() < 1) {
            Toast.makeText(getActivity(), "В рецепте должны быть этапы", Toast.LENGTH_LONG).show();
            addStep.setError("");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(getActivity(), "Укажите фотографию", Toast.LENGTH_LONG).show();
            gallery.setError("");
            return;
        }

        Recipe r = new Recipe();
        String filename = UUID.randomUUID().toString();
        storageReference = FirebaseStorage.getInstance().getReference(filename);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 Toast.makeText(getContext(),taskSnapshot.getStorage().getDownloadUrl().toString(), Toast.LENGTH_LONG ).show(); ;
            }
        });


        r.setName(name.getText().toString().trim());
        r.setDescription(description.getText().toString().trim());
        r.setSteps(steps);
        r.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        r.setDislike(0);
        r.setLike(0);
        r.setViews(0);
        FirebaseDatabase.getInstance().getReference("Recipe").push().setValue(r);
    }
}