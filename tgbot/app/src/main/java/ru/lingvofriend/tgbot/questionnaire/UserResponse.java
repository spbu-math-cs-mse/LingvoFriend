package ru.lingvofriend.tgbot.questionnaire;

import java.util.List;

public class UserResponse {
    private Long chatId;
    private String name;
    private String pronoun;
    private Integer age;
    private String goals;
    private String englishExperience;
    private List<String> interests;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getPronoun() {
		return pronoun;
	}

	public void setPronoun(String pronoun) {
		this.pronoun = pronoun;
	}

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getEnglishExperience() {
        return englishExperience;
    }

    public void setEnglishExperience(String englishExperience) {
        this.englishExperience = englishExperience;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}
