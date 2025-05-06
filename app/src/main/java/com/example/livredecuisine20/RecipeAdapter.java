package com.example.livredecuisine20;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Adaptateur pour afficher une liste de recettes dans un RecyclerView.
 *
 * Chaque élément de la liste représente une recette avec ses détails :
 * - Titre
 * - Temps de préparation
 * - Instructions
 * - Ingrédients
 *
 * L'utilisateur peut également supprimer une recette via un bouton.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private ArrayList<Recipe> recipeList;

    /**
     * Constructeur de l'adaptateur RecipeAdapter.
     *
     * @param recipeList Liste des recettes à afficher.
     */
    public RecipeAdapter(ArrayList<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.titleTextView.setText(recipe.getTitle());
        holder.timeTextView.setText(recipe.getTime());
        holder.instructionTextView.setText(recipe.getInstruction());

        // Affichage des ingrédients sous forme de liste
        StringBuilder ingredients = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (String ingredient : recipe.getIngredients()) {
                ingredients.append("- ").append(ingredient.trim()).append("\n");
            }
        }
        holder.ingredientsTextView.setText(ingredients.toString().trim());

        // Définition de l'action de suppression
        holder.deleteButton.setOnClickListener(v -> {
            deleteRecipeFromDatabase(recipe, position, holder);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    /**
     * Supprime une recette de la base de données via une requête HTTP,
     * puis la retire de la liste et met à jour l'affichage.
     *
     * @param recipe Recette à supprimer.
     * @param position Position de la recette dans la liste.
     * @param holder ViewHolder associé à la recette.
     */
    private void deleteRecipeFromDatabase(Recipe recipe, int position, RecipeViewHolder holder) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/Cuisine/delete_recipe.php?id=" + recipe.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.getResponseCode();

                recipeList.remove(position);

                ((Activity) holder.itemView.getContext()).runOnUiThread(() -> {
                    notifyItemRemoved(position);
                    Toast.makeText(holder.itemView.getContext(), "Recette supprimée", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * ViewHolder utilisé pour afficher les informations d'une recette
     * et contenir les références aux éléments de l'interface utilisateur.
     */
    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeTextView, instructionTextView, ingredientsTextView;
        Button deleteButton;

        /**
         * Constructeur du ViewHolder.
         *
         * @param itemView Vue représentant un élément de recette.
         */
        public RecipeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recipeTitle);
            timeTextView = itemView.findViewById(R.id.recipeTime);
            instructionTextView = itemView.findViewById(R.id.recipeInstruction);
            ingredientsTextView = itemView.findViewById(R.id.recipeIngredients);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
