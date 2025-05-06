package com.example.livredecuisine20;

import java.util.ArrayList;

/**
 * Classe représentant une recette dans l'application Livre de Cuisine.
 *
 * Une recette contient :
 * - un identifiant unique,
 * - un titre,
 * - un temps de préparation (formaté HH:mm:ss),
 * - des instructions de préparation,
 * - une liste d'ingrédients.
 */
public class Recipe {
    private int id;
    private String title;
    private String time;
    private String instruction;
    private ArrayList<String> ingredients;

    /**
     * Constructeur de la classe Recipe.
     *
     * @param id Identifiant unique de la recette.
     * @param title Titre de la recette.
     * @param time Temps de préparation (format HH:mm:ss).
     * @param instruction Instructions pour réaliser la recette.
     * @param ingredients Liste des ingrédients nécessaires.
     */
    public Recipe(int id, String title, String time, String instruction, ArrayList<String> ingredients) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.instruction = instruction;
        this.ingredients = ingredients;
    }

    /**
     * Récupère l'identifiant de la recette.
     *
     * @return Identifiant de la recette.
     */
    public int getId() {
        return id;
    }

    /**
     * Modifie l'identifiant de la recette.
     *
     * @param id Nouvel identifiant.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Récupère le titre de la recette.
     *
     * @return Titre de la recette.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Récupère le temps de préparation de la recette.
     *
     * @return Temps de préparation (HH:mm:ss).
     */
    public String getTime() {
        return time;
    }

    /**
     * Récupère les instructions de préparation de la recette.
     *
     * @return Instructions de la recette.
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Récupère la liste des ingrédients de la recette.
     *
     * @return Liste des ingrédients.
     */
    public ArrayList<String> getIngredients() {
        return ingredients;
    }
}
