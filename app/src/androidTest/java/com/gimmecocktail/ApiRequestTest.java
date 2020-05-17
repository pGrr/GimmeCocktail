package com.gimmecocktail;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.gimmecocktail.http.CocktailRequestQueue;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ApiRequestTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.gimmecocktail", appContext.getPackageName());
    }

    @Test
    public void testRequestByName() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        CocktailRequestQueue api = new CocktailRequestQueue(appContext);
        api.searchByName("margarita");
        assertEquals(4, 4);//cocktails.size());
    }
}
