package com.todo.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private List<String> items = new ArrayList<>();

    public Controller() {
        items.add("initial value");
    }

    // One syntax to implement a
    // GET method
    @GetMapping("/")
    public String home() {
        StringBuilder str = new StringBuilder();
        for (String item : items) {
            str.append(" ").append(item);
        }
        return str.toString();
    }

    // Another syntax to implement a
    // GET method
    @RequestMapping(method = { RequestMethod.GET }, value = { "/gfg" })
    public String info() {
        String str2 = "<html><body><font color=\"green\">"
                + "<h2>GeeksForGeeks is a Computer"
                + " Science portal for Geeks. "
                + "This portal has been "
                + "created to provide well written, "
                + "well thought and well explained "
                + "solutions for selected questions."
                + "</h2></font></body></html>";
        return str2;
    }

    @RequestMapping(method = { RequestMethod.GET }, value = { "/new" })
    public String create() {
        StringBuilder s = new StringBuilder();
        s.append("value ").append(items.size());
        items.add(s.toString());

        return "";
    }
}
