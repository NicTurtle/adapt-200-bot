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
    private Questionnaire quests = new Questionnaire();
    private Map<Long, User> userMap = new HashMap<>();
    @Autowired
    private
    BotConfig config = new BotConfig();

    @Override
    public String getBotUsername() {
        return getConfig().getBotName();
    }

    public String getBotToken() {
        return getConfig().getToken();
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            User user = getUserMap().get(chatId);
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                if (user == null) {
                    user = new User(chatId);
                    getUserMap().put(chatId, user);
                }
                user.getUserAnswers().clear();
                if (user.getQuestNumber() >= getQuests().getQuestions().size()) {
                    user.setQuestNumber(0);
                }
                sendQuestionWithButtons(chatId, getQuests().getQuestions().get(user.getQuestNumber()));
            }


            if (user != null && user.getQuestNumber() < getQuests().getQuestions().size()) {
                String currentQuestion = getQuests().getQuestions().get(user.getQuestNumber());

                if (user.getUserAnswers().size() == user.getQuestNumber()) {
                    boolean isAnswer = messageText.equals("Да");

                    if (isAnswer || messageText.equals("Нет")) {
                        user.getUserAnswers().add(isAnswer);
                        user.setQuestNumber(user.getQuestNumber() + 1);
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
            User user = getUserMap().get(chatId);
            String callbackData = update.getCallbackQuery().getData();

            if (user != null) {
                if ("yes".equals(callbackData)) {
                    user.getUserAnswers().add(true);
                    user.setQuestNumber(user.getQuestNumber() + 1);
                } else if ("no".equals(callbackData)) {
                    user.getUserAnswers().add(false);
                    user.setQuestNumber(user.getQuestNumber() + 1);
                }

                if (user.getQuestNumber() < getQuests().getQuestions().size()) {
                    sendQuestionWithButtons(chatId, getQuests().getQuestions().get(user.getQuestNumber()));
                } else {
                    StringBuilder resultMessage = new StringBuilder("Результаты:\n");
                    for (int i = 0; i < user.getUserAnswers().size(); i++) {
                        String answer = user.getUserAnswers().get(i) ? "Да" : "Нет";
                        resultMessage.append(i+1).append(": ").append(answer).append("\n");
                    }
                    sendMessage(chatId, resultMessage.toString() +
                            "\n \n Пожалуйста, отправьте /start, чтобы начать тест заново.");

                    user.getUserAnswers().clear();
                    user.setQuestNumber(0);
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

    public Questionnaire getQuests() {
        return quests;
    }

    public void setQuests(Questionnaire quests) {
        this.quests = quests;
    }

    public Map<Long, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<Long, User> userMap) {
        this.userMap = userMap;
    }

    public BotConfig getConfig() {
        return config;
    }

    public void setConfig(BotConfig config) {
        this.config = config;
    }
}
