package com.example.reservasmedicasmobile;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.junit.Assert.assertTrue;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    //#TC001
    @Test
    public void testToolbarIsDisplayed() {
        // Verifica que el Toolbar sea visible
        onView(withId(R.id.toolbar))
                .check(matches(isDisplayed()));
    }

    //#TC005
    @Test
    public void testPageLoadTime() {
        long startTime = System.currentTimeMillis();

        // Lanza la actividad principal
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                long endTime = System.currentTimeMillis();
                long loadTime = endTime - startTime;

                // Verifica que el tiempo de carga es menor a 2000 ms (2 segundos)
                assertTrue("La página de inicio carga en más de 2 segundos", loadTime < 2000);
            });
        }
    }
}
