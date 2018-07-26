package com.coingaming.guessnumber.messages;

public enum ResultMessage {
    WIN("You win"),
    LOSE("You lose"),
    ENTER("Enter the number"),
    START("Start round");

    private String message;

    ResultMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}