package com.example.foodhub.Search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodhub.R;
import com.example.foodhub.Step;
import com.example.foodhub.User;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchRecipeOpen extends Fragment {

    private ArrayList<Integer> sec = new ArrayList<>();
    private ArrayList<Integer> min = new ArrayList<>();
    private ArrayList<Integer> hour = new ArrayList<>();
    private ArrayList<String> stepDesc = new ArrayList<>();
    private ArrayList<Step> steps = new ArrayList<>();

    private TextView RecipeName, ViewsCounter, LikeCounter, DislikeCounter, RecipeDesc, Username;
    private ImageView RecipeImg;
    private RecyclerView recyclerView;
    private Button LikeBtn, DislikeBtn;

    private String recipeID;
    private int likeC, dislikeC;
    private boolean l, dl;

    OpenRecipeStepsAdapter openRecipeStepsAdapter;

    public SearchRecipeOpen() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_recipe_open, container, false);

        RecipeDesc = view.findViewById(R.id.OpenRecipeDesc);
        RecipeName = view.findViewById(R.id.OpenRecipeName);
        ViewsCounter = view.findViewById(R.id.OpenRecipeViewCounter);
        LikeCounter = view.findViewById(R.id.OpenRecipeLikeCounter);
        DislikeCounter = view.findViewById(R.id.OpenRecipeDislikeCounter);
        Username = view.findViewById(R.id.OpenRecipeUsername);
        RecipeImg = view.findViewById(R.id.OpenRecipeImage);
        LikeBtn = view.findViewById(R.id.OpenRecipeLikeBtn);
        DislikeBtn = view.findViewById(R.id.OpenRecipeDislikeBtn);

        recyclerView = view.findViewById(R.id.OpenRecipeRecyclerView);


        Bundle bundle = new Bundle();
        bundle = this.getArguments();

        RecipeName.setText(bundle.getString("recipe_name"));
        RecipeDesc.setText(bundle.getString("recipe_desc"));
        ViewsCounter.setText(Integer.toString(bundle.getInt("recipe_views")));
        LikeCounter.setText(Integer.toString(bundle.getInt("recipe_likes")));
        DislikeCounter.setText(Integer.toString(bundle.getInt("recipe_dislike")));
        Username.setText(bundle.getString("username"));

        sec = bundle.getIntegerArrayList("step_sec");
        min = bundle.getIntegerArrayList("step_min");
        hour = bundle.getIntegerArrayList("step_hour");
        stepDesc = bundle.getStringArrayList("step_desc");
        recipeID = bundle.getString("recipe_ID");
        likeC = bundle.getInt("recipe_like");
        dislikeC = bundle.getInt("recipe_dislike");

        for (int i=0; i < stepDesc.size(); i++) {
            steps.add(new Step(stepDesc.get(i), sec.get(i), min.get(i), hour.get(i)));
        }

        openRecipeStepsAdapter = new OpenRecipeStepsAdapter(getContext(), steps);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(openRecipeStepsAdapter);

        Picasso.get().load(bundle.getString("recipe_img")).resize(350, 350).placeholder(R.drawable.gal).centerCrop().into(RecipeImg);

        l = true;
        dl = true;
        LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (l) {
                    l = false;
                    Toast.makeText(inflater.getContext(), "Спасибо за вашу оценку", Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference("Recipe").child(recipeID).child("like").setValue(likeC + 1);
                    DislikeBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        DislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dl) {
                    dl = false;
                    Toast.makeText(inflater.getContext(), "Спасибо за вашу оценку", Toast.LENGTH_LONG).show();
                    FirebaseDatabase.getInstance().getReference("Recipe").child(recipeID).child("dislike").setValue(likeC + 1);
                    LikeBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }
}