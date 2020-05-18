package com.gimmecocktail;

import android.graphics.Bitmap;
import android.media.Image;

import androidx.annotation.NonNull;

import java.util.Map;

public final class Cocktail {
    public static final int N_MAX_INGREDIENTS = 15;

    private final String name; // strDrink
    private final String type; // strAlcoholic
    private final String category; // strCategory
    private final String thumbnailUrl; // strDrinkThumb
    private final String glass; // strGlass
    private final Map<String, String> ingredients; // { strIngredient1 => strMeasure1, ...}
    private final String instructions; // strInstructions
    private Bitmap thumbnail;

    public Cocktail(
            String name,
            String type,
            String category,
            Map<String,String> ingredients,
            String thumbnailUrl,
            String glass,
            String instructions) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnail = null;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public Bitmap getThumbnail() { return thumbnail; }

    public void setThumbnail(Bitmap thumbnail) { this.thumbnail = thumbnail; }

    public String getGlass() {
        return glass;
    }

    public String getIngredients() {
        String ingredients = new String();
        for (Map.Entry<String,String> ingredient: this.ingredients.entrySet()) {
            ingredients += ingredient.getKey() + ": " + ingredient.getValue() + "\n";
        }
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
