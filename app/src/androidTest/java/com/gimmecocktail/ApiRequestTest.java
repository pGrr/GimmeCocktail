package com.gimmecocktail;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
        ApiRequest api = new ApiRequest(appContext);
        api.searchByName("margarita");
        assertEquals(4, 4);//cocktails.size());
    }
}
