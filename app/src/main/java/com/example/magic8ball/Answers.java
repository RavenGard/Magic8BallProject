package com.example.magic8ball;

import java.util.Random;

public class Answers {

    private static final String[] answers = {
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

    public static String getRandomAnswer() {
        Random random = new Random();
        int index = random.nextInt(answers.length);
        return answers[index];
    }
}


// private static ArrayList<String> examples = new ArrayList<String>();

//     public static String random() {
//         examples.add("new message 1");
//         examples.add("hello world");
//         examples.add("bye world");
//         examples.add("yes");
//         examples.add("no");
//         examples.add("ExampleYes");

//         Random randomResponse = new Random();
//         int arraySize = examples.size();

//         return examples.get(randomResponse.nextInt(arraySize));
//     }
