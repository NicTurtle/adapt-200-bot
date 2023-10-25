package com.example.Adapt200Bot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Questionnaire {
    private ArrayList<Boolean> answersList = new ArrayList<>();
    private List<String> questions = readQuestionsFromFile("Materials/testFile.txt");
    public static List<String> readQuestionsFromFile(String fileName) {
        List<String> questions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    questions.add(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return questions;

    }

    public ArrayList<Boolean> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(ArrayList<Boolean> answersList) {
        this.answersList = answersList;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
}



//https://sprotyvg7.com.ua/wp-content/uploads/2022/05/ЗБІРНИК-МЕТОДИК-ДЛЯ-ДІАГНОСТИКИ-НЕГАТИВНИХ-ПСИХІЧНИХ-СТАНІВ-ВІЙСЬКОВОСЛУЖБОВЦІВ.pdf
