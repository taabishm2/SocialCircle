package com.socialcircle.model.data;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatGptResponse {

    private List<Choices> choices = new ArrayList<>();

    private class Choices {

        private String text;

    }

}
