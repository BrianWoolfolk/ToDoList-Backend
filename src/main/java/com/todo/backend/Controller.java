package com.todo.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.todo.backend.ToDo.Priority;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    private List<ToDo> DB = new ArrayList<>();

    // One syntax to implement a
    // GET method
    @GetMapping("/")
    public String home() {
        String str = "<html><body>"
                + "<h2>Menu:</h2><ul>"
                + "<li><b>GET todos/</b>: retrieves all, with pagination and filters <br/>"
                + "<code>pag: >1, filter_done: boolean?, filter_text: string?, filter_priority: Priority?</code></li>"
                + "<li><b>POST todos/</b>: creates a new ToDo item <br/>"
                + "<code>text: string, priority: Priority, due_date: ISOstring?</code></li>"
                + "<li><b>PUT todos/{id}/</b>: identical to previous, but modifies an item by its 'id' <br/>"
                + "<code>text: string, priority: Priority, due_date: ISOstring?</code></li>"
                + "<li><b>POST todos/{id}/done</b>: marks as DONE an specific item by its 'id' <br/>"
                + "<code>none</code></li>"
                + "<li><b>PUT todos/{id}/undone</b>: marks as UNDONE an specific item by its 'id' <br/>"
                + "<code>none</code></li>"
                + "</ul></h2></body></html>";
        return str;
    }

    // Another syntax to implement a
    // GET method
    @RequestMapping(method = { RequestMethod.GET }, value = { "/todos" })
    public GETResponse getAllToDos(@RequestBody ToDoFilter filters) {
        // Return variable
        List<ToDo> filtered = new ArrayList<>(DB.subList(0, DB.size()));

        Boolean filter_done = filters.getFilter_done();
        String filter_text = filters.getFilter_text();
        Priority filter_priority = filters.getFilter_priority();

        if (filter_done != null || filter_text != null || filter_priority != null) {
            filtered = new ArrayList<>(); // CLEAR ALL

            for (ToDo item : DB) {
                if (filter_done != null && item.getDone() != filter_done)
                    continue;
                if (filter_priority != null && item.getPriority() != filter_priority)
                    continue;
                if (filter_text != null && !item.getText().contains(filter_text))
                    continue;

                filtered.add(item); // FILTERS PASSED
            }
        }

        int maxpage = (int) Math.max(Math.ceil(filtered.size() / 10), 1);
        int page = (int) Math.min(filters.getPag(), maxpage);

        return (new GETResponse(
                filtered.subList(10 * (page - 1), Math.min(10 * page, filtered.size())),
                page,
                maxpage));
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/todos" })
    public ResponseEntity<String> createToDo(@RequestBody BasicToDo toDo) {
        boolean stat = DB.add(
                new ToDo(
                        toDo.getText(),
                        toDo.getPriority(),
                        toDo.getDue_date()));

        if (!stat) {
            return new ResponseEntity<>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Added new ToDo item", HttpStatus.CREATED);
    }
}
