package com.gimmecocktail.http;

import com.gimmecocktail.model.Cocktail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonResponses {

    public static List<Cocktail> cocktailSequenceFrom(JSONObject jsonCocktails) throws JSONException {
        List<Cocktail> cocktails = new ArrayList<>();
        JSONArray drinksArray = jsonCocktails.getJSONArray("drinks");
        for (int i=0; i<drinksArray.length(); i++) {
            JSONObject jsonCocktail = drinksArray.getJSONObject(i);
            Cocktail cocktail = cocktailFrom(jsonCocktail);
            cocktails.add(cocktail);
        }
        return cocktails;
    }

    public static Cocktail cocktailFrom(JSONObject jsonCocktail) throws JSONException {
        Map<String,String> ingredients = new LinkedHashMap<>();
        for (int i=1; i<Cocktail.N_MAX_INGREDIENTS; i++) {
            String ingredient = jsonCocktail.getString("strIngredient" + i);
            String measure = jsonCocktail.getString("strMeasure" + i);
            if (!ingredient.equals("null") && !ingredient.isEmpty()) {
                ingredients.put(ingredient, measure);
            }
        }
        String imageUrl = jsonCocktail.getString("strDrinkThumb");
        return new Cocktail(
                jsonCocktail.getString("idDrink"),
                jsonCocktail.getString("strDrink"),
                jsonCocktail.getString("strAlcoholic"),
                jsonCocktail.getString("strCategory"),
                ingredients,
                imageUrl,
                jsonCocktail.getString("strGlass"),
                jsonCocktail.getString("strInstructions"));
    }

}
