package com.todo.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.backend.ToDo.Priority;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class Controller {

    // #region ################################ DATA BASE
    private List<ToDo> DB = new ArrayList<>();
    // #endregion

    // #region ################################ HOME
    @RequestMapping(method = { RequestMethod.GET }, value = { "/" })
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
    // #endregion

    // #region ################################ GET
    @RequestMapping(method = { RequestMethod.GET }, value = { "/todos" })
    public GETResponse getAllToDos(
            @RequestParam(defaultValue = "1") int pag,
            @RequestParam(required = false) Boolean done,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Priority priority) {
        // Return variable
        List<ToDo> filtered = new ArrayList<>(DB.subList(0, DB.size()));

        if (done != null || text != null || priority != null) {
            filtered = new ArrayList<>(); // CLEAR ALL

            for (ToDo item : DB) {
                if (done != null && item.getDone() != done)
                    continue;
                if (priority != null && item.getPriority() != priority)
                    continue;
                if (text != null && !item.getText().contains(text))
                    continue;

                filtered.add(item); // FILTERS PASSED
            }
        }

        int maxpage = (int) Math.max(Math.ceil(filtered.size() / 10f), 1);
        int page = (int) Math.min(Math.max(pag, 1), maxpage);

        return (new GETResponse(
                filtered.subList(10 * (page - 1), Math.min(10 * page, filtered.size())),
                page,
                maxpage));
    }

    @RequestMapping(method = { RequestMethod.GET }, value = { "/todos/{id}" })
    public ResponseEntity<?> getToDo(@PathVariable int id) {
        if (id < 0)
            return new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);

        for (ToDo toDo : DB) {
            if (toDo.getId() == id)
                return new ResponseEntity<ToDo>(toDo, HttpStatus.FOUND);
        }

        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }
    // #endregion

    // #region ################################ POST
    @RequestMapping(method = { RequestMethod.POST }, value = { "/todos" })
    public ResponseEntity<String> createToDo(@RequestBody BasicToDo toDo) {
        boolean stat = DB.add(
                new ToDo(
                        toDo.getText(),
                        toDo.getPriority(),
                        toDo.getDue_date()));

        if (!stat)
            return new ResponseEntity<>("Server error", HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>("Added new ToDo item", HttpStatus.CREATED);
    }

    @RequestMapping(method = { RequestMethod.PUT }, value = { "/todos/{id}" })
    public ResponseEntity<String> updateToDo(@PathVariable int id, @RequestBody Map<String, String> body)
            throws ParseException {
        if (id < 0)
            return new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);

        for (int i = 0; i < DB.size(); i++) {
            ToDo todo = DB.get(i);
            if (todo.getId() == id) {

                String text = body.get("text");
                if (text != null)
                    todo.setText(text);

                String priority = body.get("priority");
                if (priority != null)
                    todo.setPriority(Priority.valueOf(priority));

                String due_date = body.get("due_date");
                if (due_date != null) {
                    if (due_date.equals("none"))
                        todo.setDue_date(null);
                    else
                        todo.setDue_date(Instant.parse(due_date));
                }

                return new ResponseEntity<>("Item modified", HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }
    // #endregion

    // #region ################################ DONE/UNDONE
    @RequestMapping(method = { RequestMethod.POST }, value = { "/todos/{id}/done" })
    public ResponseEntity<String> markDone(@PathVariable int id) {
        if (id < 0)
            return new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);

        for (ToDo toDo : DB) {
            if (toDo.getId() == id) {
                toDo.setDone(true);
                return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = { RequestMethod.PUT }, value = { "/todos/{id}/undone" })
    public ResponseEntity<String> markUndone(@PathVariable int id) {
        if (id < 0)
            return new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);

        for (ToDo toDo : DB) {
            if (toDo.getId() == id) {
                toDo.setDone(false);
                return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }
    // #endregion
}
