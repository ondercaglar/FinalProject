package com.udacity.gradle.builditbigger;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class MainActivityAndroidTest {


    String exampleJoke    = "This is a joke from a Java Library";
    String exampleJokeGCE = "This is a joke from a Java Library via GCE";


    //Add code to test that your Async task successfully retrieves a non-empty string.

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void tell_joke_fromtask_btn()
    {
        //  Click on tell joke via GCE button
        onView((withId(R.id.tell_joke_fromtask_btn))).perform(click());

        // Verify that Async task successfully retrieves a non-empty string.
        onView(withId(R.id.joke_textview)).check(matches(withText(exampleJokeGCE)));
    }


    @Test
    public void launch_display_joke_btn()
    {
        // Click on Display Joke on New Page
        onView((withId(R.id.launch_display_joke_btn))).perform(click());

        // Verify that joke retrieved successfully
        onView(withId(R.id.joke_textview)).check(matches(withText(exampleJoke)));
    }
}
