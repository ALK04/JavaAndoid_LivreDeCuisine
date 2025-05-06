package com.example.livredecuisine20;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Activité principale de l'application Livre de Cuisine.
 *
 * Cette activité permet aux utilisateurs :
 * - d'ajouter dynamiquement des champs pour saisir les ingrédients,
 * - d'enregistrer une recette (titre, temps, instructions, liste d'ingrédients) en envoyant une requête HTTP POST,
 * - de naviguer vers une autre activité (SecondActivity).
 */
public class MainActivity extends AppCompatActivity {

    private LinearLayout ingredientsLayout;
    private Button buttonAddIngredient, buttonSaveRecipe, buttonGoToSecondActivity;
    private EditText editTextTitle, editTextTime, editTextInstruction;
    private ArrayList<EditText> ingredientFields = new ArrayList<>(); // Stocke les champs d'ingrédients ajoutés dynamiquement

    /**
     * Méthode appelée à la création de l'activité.
     *
     * @param savedInstanceState Etat précédent de l'activité, s'il existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ingredientsLayout = findViewById(R.id.ingredientsLayout);
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);
        buttonGoToSecondActivity = findViewById(R.id.buttonGoToSecondActivity);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextTime = findViewById(R.id.editTextTime);
        editTextInstruction = findViewById(R.id.editTextInstruction);

        buttonAddIngredient.setOnClickListener(v -> addIngredientField());
        buttonSaveRecipe.setOnClickListener(v -> saveRecipe());
        buttonGoToSecondActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Ajoute dynamiquement un champ EditText pour la saisie d'un ingrédient.
     */
    private void addIngredientField() {
        EditText newIngredient = new EditText(this);
        newIngredient.setHint("Ingrédient");
        ingredientsLayout.addView(newIngredient);
        ingredientFields.add(newIngredient);
    }

    /**
     * Enregistre une recette en envoyant ses données au serveur via une requête HTTP POST.
     * Vérifie que les champs obligatoires sont remplis et que le temps est au format HH:mm:ss.
     */
    private void saveRecipe() {
        String titre = editTextTitle.getText().toString();
        String time = editTextTime.getText().toString();
        String instruction = editTextInstruction.getText().toString();
        ArrayList<String> ingredients = new ArrayList<>();

        for (EditText ingredientField : ingredientFields) {
            String ingredient = ingredientField.getText().toString();
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        int nombreIngredients = ingredients.size();

        // Validation des données
        if (!isValidTime(time)) {
            Toast.makeText(this, "Le temps doit être au format HH:mm:ss", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(titre)) {
            Toast.makeText(this, "Le titre de la recette est requis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(instruction)) {
            Toast.makeText(this, "Les instructions sont requises", Toast.LENGTH_SHORT).show();
            return;
        }

        // Préparation des données pour la requête POST
        StringBuilder postData = new StringBuilder();
        postData.append("titre=").append(titre);
        postData.append("&time=").append(time);
        postData.append("&instruction=").append(instruction);
        postData.append("&nombreIngredients=").append(nombreIngredients);

        for (int i = 0; i < ingredients.size(); i++) {
            postData.append("&ingredient").append(i).append("=").append(ingredients.get(i));
        }

        // Envoi des données dans un thread secondaire
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/Cuisine/insert_recipe.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(postData.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Recette enregistrée avec succès", Toast.LENGTH_LONG).show();
                        hideKeyboard();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Vérifie si la chaîne de caractères correspond au format d'heure HH:mm:ss.
     *
     * @param time Temps saisi par l'utilisateur.
     * @return true si le format est correct, false sinon.
     */
    private boolean isValidTime(String time) {
        String timePattern = "^([01]?[0-9]|2[0-3]):([0-5]?[0-9]):([0-5]?[0-9])$";
        return time.matches(timePattern);
    }

    /**
     * Masque le clavier virtuel s'il est affiché.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
