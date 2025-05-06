package com.example.livredecuisine20;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Activité permettant d'afficher toutes les recettes stockées dans la base de données
 * sous forme de liste dans un RecyclerView.
 *
 * L'utilisateur peut également revenir à la page d'accueil (MainActivity) via un bouton "Retour".
 */
public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipeList;
    private Button btnRetour;

    /**
     * Méthode appelée à la création de l'activité.
     * Initialise l'interface utilisateur et récupère les recettes depuis la base de données.
     *
     * @param savedInstanceState État sauvegardé de l'instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(recipeAdapter);

        btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        fetchRecipesFromDatabase();
    }

    /**
     * Récupère les recettes depuis la base de données distante en utilisant une requête HTTP,
     * puis met à jour la liste et l'adaptateur pour afficher les recettes.
     */
    private void fetchRecipesFromDatabase() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/Cuisine/get_recipe.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONArray recipesJsonArray = new JSONArray(response.toString());
                for (int i = 0; i < recipesJsonArray.length(); i++) {
                    JSONObject recipeObject = recipesJsonArray.getJSONObject(i);

                    int id = recipeObject.getInt("id");
                    String title = recipeObject.getString("titre");
                    String time = recipeObject.getString("time");
                    String instruction = recipeObject.getString("instruction");

                    ArrayList<String> ingredients = new ArrayList<>();
                    JSONArray ingredientsJsonArray = recipeObject.getJSONArray("ingredients");
                    for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                        ingredients.add(ingredientsJsonArray.getString(j));
                    }

                    Recipe recipe = new Recipe(id, title, time, instruction, ingredients);
                    recipeList.add(recipe);
                }

                runOnUiThread(() -> recipeAdapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SecondActivity.this, "Erreur de récupération des recettes", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
