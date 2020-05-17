package com.gimmecocktail;

import android.media.Image;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public final class Cocktail {
    public static final int N_MAX_INGREDIENTS = 15;

    private final String name; // strDrink
    private final String type; // strAlcoholic
    private final String category; // strCategory
    private final Image thumbnail; // fetch strDrinkThumb (url) => save image locally
    private final String glass; // strGlass
    private final Map<String, String> ingredients; // { strIngredient1 => strMeasure1, ...}
    private final String instructions; // strInstructions

    public Cocktail(
            String name,
            String type,
            String category,
            Map<String,String> ingredients,
            Image thumbnail,
            String glass,
            String instructions) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.thumbnail = thumbnail;
        this.glass = glass;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public String getGlass() {
        return glass;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    @NonNull
    @Override
    public String toString() {
        return "Cocktail: { "
                + "name: " + this.name + "; "
                + "type: " + this.type + "; "
                + "category: " + this.category + "; "
                + "glass: " + this.glass + "; "
                + "name: " + this.name + "; "
                + "ingredients: " + this.ingredients + "; "
                + "instructions: " + this.instructions + "; "
                + " }";
    }
}
