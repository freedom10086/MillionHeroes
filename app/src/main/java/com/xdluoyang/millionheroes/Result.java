package com.xdluoyang.millionheroes;


import java.util.ArrayList;
import java.util.List;

public class Result {
    List<String> results;

    public Result() {
        results = new ArrayList<>();
    }

    public void addResult(String result) {
        results.add(result);
    }

    public String getTitle() {
        int i = results.size() - 3;
        String title = "";
        if (i > 0) {
            for (int j = 0; j < i; j++) {
                title += results.get(j);
            }
            return title;
        } else if (i >= -2) {
            return results.get(0);
        } else {
            //既没有题目也没有答案
            return "";
        }
    }

    public List<String> getAnswers() {
        if (results.size() > 1) {
            int startIndex = Math.max(results.size() - 3,1) ;
            return results.subList(startIndex, results.size());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return "标题:" + getTitle() + "   答案:" + getAnswers();
    }
}
