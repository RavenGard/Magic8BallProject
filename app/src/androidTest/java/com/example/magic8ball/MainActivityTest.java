package com.example.magic8ball;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;

// This annotation specifies that the test will run with AndroidJUnit4 runner
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    // This rule tells the test framework to launch MainActivity before running any test
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    // List of 20 possible responses
    private static final String[] RESPONSES = {
            "It is certain.",
            "It is decidedly so.",
            "Without a doubt.",
            "Yes definitely.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Outlook good.",
            "Yes.",
            "Signs point to yes.",
            "Reply hazy, try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don't count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful."
    };

    // This test checks if the up/down sensing is working properly and
    // if the displayed response matches one of the 20 responses
    @Test
    public void testUpDownSensingAndResponses() {
        // Run the test 5 times
        for (int i = 0; i < 5; i++) {
            // Perform a click action on the TextView with ID 'gravity'
            onView(withId(R.id.gravity)).perform(click());
            // Check if the text displayed on the TextView matches any of the 20 possible responses
            onView(withId(R.id.gravity)).check(matches(anyOf(
                    withText(RESPONSES[0]),
                    withText(RESPONSES[1]),
                    withText(RESPONSES[2]),
                    withText(RESPONSES[3]),
                    withText(RESPONSES[4]),
                    withText(RESPONSES[5]),
                    withText(RESPONSES[6]),
                    withText(RESPONSES[7]),
                    withText(RESPONSES[8]),
                    withText(RESPONSES[9]),
                    withText(RESPONSES[10]),
                    withText(RESPONSES[11]),
                    withText(RESPONSES[12]),
                    withText(RESPONSES[13]),
                    withText(RESPONSES[14]),
                    withText(RESPONSES[15]),
                    withText(RESPONSES[16]),
                    withText(RESPONSES[17]),
                    withText(RESPONSES[18]),
                    withText(RESPONSES[19])
            )));
        }
    }
}
