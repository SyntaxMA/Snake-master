package com.example.demo2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonClass {

    public String getJSON(Joc estatJoc) {
        String resposta="No s'ha processat el json";
        try {
            resposta= new ObjectMapper().writeValueAsString(estatJoc);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resposta;
    }

    public Joc getObject(String json) {

        try {
            return new ObjectMapper().readValue(json, Joc.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
