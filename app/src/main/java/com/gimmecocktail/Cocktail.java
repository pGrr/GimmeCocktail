package com.gimmecocktail;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.Map;

public final class Cocktail implements Parcelable {
    public static final int N_MAX_INGREDIENTS = 15;

    private final String name; // strDrink
    private final String type; // strAlcoholic
    private final String category; // strCategory
    private final String thumbnailUrl; // strDrinkThumb
    private final String glass; // strGlass
    private final String ingredients; // { strIngredient1 => strMeasure1, ...}
    private final String instructions; // strInstructions

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
        this.glass = glass;
        this.ingredients = convertIngredients(ingredients);
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

    public String getGlass() {
        return glass;
    }

    public String getIngredients() {
        return ingredients;
    }

    private String convertIngredients(Map<String,String> ingredientsMap) {
        String ingredients = "";
        for (Map.Entry<String,String> ingredient: ingredientsMap.entrySet()) {
            ingredients += ingredient.getKey();
            if (!ingredient.getValue().equals("null")) {
                ingredients += ": " + ingredient.getValue() + "\n";
            }
        }
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getTypeCategoryAndGlassAsString() {
        String s = "";
        s += "Type: " + this.getType() + "\n";
        s += "Category: " + this.getCategory() + "\n";
        s += "Glass: " + this.getGlass() + "\n";
        return s;
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

    // parcelable part

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Cocktail createFromParcel(Parcel in ) {
            return new Cocktail( in );
        }
        public Cocktail[] newArray(int size) {
            return new Cocktail[size];
        }
    };

    public Cocktail(Parcel in){
        this.name = in.readString();
        this.type = in.readString();
        this.category = in.readString();
        this.thumbnailUrl = in.readString();
        this.glass = in.readString();
        this.ingredients = in.readString();
        this.instructions = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(category);
        dest.writeString(thumbnailUrl);
        dest.writeString(glass);
        dest.writeString(ingredients);
        dest.writeString(instructions);
    }
}
