package com.example.magic8ball;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Answers {
    private static final List<String> responses = Arrays.asList(
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
    );

    private static final Random random = new Random();

    public static String getRandomAnswer() {
        int index = random.nextInt(responses.size());
        return responses.get(index);
    }
}
