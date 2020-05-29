package com.gimmecocktail.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public final class Cocktail implements Parcelable {
    @Ignore
    public static final int N_MAX_INGREDIENTS = 15;

    @PrimaryKey @NonNull
    private final String id; //strId

    private final String name; // strDrink
    private final String genericInfo; // strAlcoholic
    private final String thumbnailUrl; // strDrinkThumb
    private final String ingredients; // { strIngredient1 => strMeasure1, ...}
    private final String instructions; // strInstructions

    public Cocktail(String id, String name, String genericInfo, String thumbnailUrl, String ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.genericInfo = genericInfo;
        this.thumbnailUrl = thumbnailUrl;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Cocktail(
            String id,
            String name,
            String type,
            String category,
            Map<String,String> ingredients,
            String thumbnailUrl,
            String glass,
            String instructions) {
        this.id = id;
        this.name = name;
        this.genericInfo = createGenericInfo(type, category, glass);
        this.thumbnailUrl = thumbnailUrl;
        this.ingredients = convertIngredients(ingredients);
        this.instructions = instructions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getIngredients() {
        return ingredients;
    }

    private String convertIngredients(Map<String,String> ingredientsMap) {
        StringBuilder ingredients = new StringBuilder();
        for (Map.Entry<String,String> ingredient: ingredientsMap.entrySet()) {
            ingredients.append(ingredient.getKey());
            if (!ingredient.getValue().equals("null")) {
                ingredients.append(": ").append(ingredient.getValue()).append("\n");
            }
        }
        return ingredients.toString().trim();
    }

    public String getInstructions() {
        return instructions;
    }

    public String getGenericInfo() {
        return genericInfo;
    }

    private String createGenericInfo(String type, String category, String glass) {
        String s = "";
        s += "Type: " + type + "\n";
        s += "Category: " + category + "\n";
        s += "Glass: " + glass;
        return s;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ "
                + "id: \"" + this.id + "\", "
                + "name: \"" + this.name + "\", "
                + "thumbnailUrl: \"" + this.thumbnailUrl + "\", "
                + "genericInfo: \"" + this.genericInfo + "\", "
                + "ingredients: \"" + this.ingredients + "\", "
                + "instructions: \"" + this.instructions + "\""
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
        this.id = in.readString();
        this.name = in.readString();
        this.thumbnailUrl = in.readString();
        this.genericInfo = in.readString();
        this.ingredients = in.readString();
        this.instructions = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(thumbnailUrl);
        dest.writeString(genericInfo);
        dest.writeString(ingredients);
        dest.writeString(instructions);
    }
}
