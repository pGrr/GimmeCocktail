package com.gimmecocktail.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Map;
import java.util.Objects;

/**
 * The main model class that represents a Cocktail.
 */
@Entity
public final class Cocktail implements Parcelable {

    /**
     * The maximum number of ingredients (used to iterate over Json data provided by the API).
     */
    @Ignore
    public static final int N_MAX_INGREDIENTS = 15;

    /**
     * The Parcelable CREATOR.
     */
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Cocktail createFromParcel(Parcel in ) {
            return new Cocktail( in );
        }
        public Cocktail[] newArray(int size) {
            return new Cocktail[size];
        }
    };

    @PrimaryKey @NonNull
    private final String id; //strId
    private final String name; // strDrink
    private final String genericInfo; // strAlcoholic
    private final String thumbnailUrl; // strDrinkThumb
    private final String ingredients; // { strIngredient1 => strMeasure1, ...}
    private final String instructions; // strInstructions

    @Ignore
    private Bitmap thumbnailBitmap = null;

    /**
     * Instantiates a new Cocktail.
     *
     * @param id           the id
     * @param name         the name
     * @param genericInfo  the generic info (i.e. the type, category and glass)
     * @param thumbnailUrl the thumbnail url
     * @param ingredients  the ingredients
     * @param instructions the instructions
     */
    public Cocktail(@NonNull String id, String name, String genericInfo, String thumbnailUrl, String ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.genericInfo = genericInfo;
        this.thumbnailUrl = thumbnailUrl;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    /**
     * Instantiates a new Cocktail.
     *
     * @param id           the id
     * @param name         the name
     * @param type         the type
     * @param category     the category
     * @param ingredients  the ingredients
     * @param thumbnailUrl the thumbnail url
     * @param glass        the glass
     * @param instructions the instructions
     */
    public Cocktail(
            @NonNull String id,
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

    /**
     * Instantiates a new Cocktail from a Parcel.
     *
     * @param in the in
     */
    public Cocktail(Parcel in){
        this.id = Objects.requireNonNull(in.readString());
        this.name = in.readString();
        this.thumbnailUrl = in.readString();
        this.genericInfo = in.readString();
        this.ingredients = in.readString();
        this.instructions = in.readString();
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets thumbnail url.
     *
     * @return the thumbnail url
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Gets ingredients.
     *
     * @return the ingredients
     */
    public String getIngredients() {
        return ingredients;
    }

    /**
     * Gets instructions.
     *
     * @return the instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Gets generic info.
     *
     * @return the generic info
     */
    public String getGenericInfo() {
        return genericInfo;
    }

    /**
     * Gets the thumbnail bitmap
     *
     * @return the thumbnail bitmap
     */
    public Bitmap getThumbnailBitmap() {
        return thumbnailBitmap;
    }

    /**
     * Sets the thumbnail bitmap
     *
     * @param bitmap
     */
    public void setThumbnailBitmap(Bitmap bitmap) {
        thumbnailBitmap = bitmap;
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

    @Override
    public int describeContents() {
        return 0;
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

    private String createGenericInfo(String type, String category, String glass) {
        String s = "";
        s += "Type: " + type + "\n";
        s += "Category: " + category + "\n";
        s += "Glass: " + glass;
        return s;
    }

}
