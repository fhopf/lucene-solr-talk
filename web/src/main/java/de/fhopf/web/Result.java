package de.fhopf.web;

import java.util.List;

public class Result {

    private final List<String> result;

    public Result(List<String> result) {
        this.result = result;
    }

    public List<String> getResult() {
        return result;
    }
}
