package com.example.Adapt200Bot;

import com.example.Adapt200Bot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class TestBot extends TelegramLongPollingBot {
    Questionnaire quests = new Questionnaire();
    Map<Long, User> userMap = new HashMap<>();
    @Autowired
    BotConfig config = new BotConfig();

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    public String getBotToken() {
        return config.getToken();
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            User user = userMap.get(chatId);
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                if (user == null) {
                    user = new User(chatId);
                    userMap.put(chatId, user);
                }
                user.userAnswers.clear();
                if (user.questNumber >= quests.questions.size()) {
                    user.questNumber = 0;
                }
                sendQuestionWithButtons(chatId, quests.questions.get(user.questNumber));
            }


            if (user != null && user.questNumber < quests.questions.size()) {
                String currentQuestion = quests.questions.get(user.questNumber);

                if (user.userAnswers.size() == user.questNumber) {
                    boolean isAnswer = messageText.equals("Да");

                    if (isAnswer || messageText.equals("Нет")) {
                        user.userAnswers.add(isAnswer);
                        user.questNumber++;
                    } else {
                        sendMessage(chatId, "Пожалуйста, используйте кнопки Да или Нет для ответа.");
                    }
                } else {
                    sendQuestionWithButtons(chatId, currentQuestion);
                }
            } else {
                sendMessage(chatId, "Пожалуйста, отправьте /start, чтобы начать тест.");
            }
        }

        if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            User user = userMap.get(chatId);
            String callbackData = update.getCallbackQuery().getData();

            if (user != null) {
                if ("yes".equals(callbackData)) {
                    user.userAnswers.add(true);
                    user.questNumber++;
                } else if ("no".equals(callbackData)) {
                    user.userAnswers.add(false);
                    user.questNumber++;
                }

                if (user.questNumber < quests.questions.size()) {
                    sendQuestionWithButtons(chatId, quests.questions.get(user.questNumber));
                } else {
                    StringBuilder resultMessage = new StringBuilder("Результаты:\n");
                    for (int i = 0; i < user.userAnswers.size(); i++) {
                        String answer = user.userAnswers.get(i) ? "Да" : "Нет";
                        resultMessage.append(i+1).append(": ").append(answer).append("\n");
                    }
                    sendMessage(chatId, resultMessage.toString() +
                            "\n \n Пожалуйста, отправьте /start, чтобы начать тест заново.");

                    user.userAnswers.clear();
                    user.questNumber = 0;
                }
            }
        }
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendQuestionWithButtons(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton buttonYes = new InlineKeyboardButton();
        buttonYes.setText("Да");
        buttonYes.setCallbackData("yes");

        InlineKeyboardButton buttonNo = new InlineKeyboardButton();
        buttonNo.setText("Нет");
        buttonNo.setCallbackData("no");

        row.add(buttonYes);
        row.add(buttonNo);

        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
