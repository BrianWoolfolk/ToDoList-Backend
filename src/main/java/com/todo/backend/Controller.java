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

import com.todo.backend.Metrics.LastMetrics;
import com.todo.backend.ToDo.Priority;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class Controller {

    // #region ################################ DATA BASE
    private List<ToDo> DB = new ArrayList<>();
    private Metrics metrics = new Metrics();

    private SearchParams previousParams = new SearchParams();
    private List<ToDo> lastResponse = null;
    // #endregion

    // #region ################################ FILTER & SORTING
    private List<ToDo> filter(Boolean done, String text, Priority priority) {
        List<ToDo> filtered = new ArrayList<>(); // CLEAR ALL

        for (ToDo item : DB) {
            if (done != null && item.getDone() != done)
                continue;
            if (priority != null && item.getPriority() != priority)
                continue;
            if (text != null && !item.getText().contains(text))
                continue;

            filtered.add(item); // FILTERS PASSED
        }

        return filtered;
    }

    private void sortList(List<ToDo> filtered, Boolean sortPriority, Boolean sortDueDate) {
        Collections.sort(filtered, new Comparator<ToDo>() {
            @Override
            public int compare(final ToDo item1, final ToDo item2) {
                int diff = 0;
                if (sortPriority != null) {
                    if (sortPriority)
                        diff = item1.getPriority().compareTo(item2.getPriority());
                    else
                        diff = item2.getPriority().compareTo(item1.getPriority());
                }

                if (sortDueDate != null && diff == 0) {
                    Instant d1 = item1.getDue_date();
                    Instant d2 = item2.getDue_date();

                    if (d1 == null && d2 == null)
                        return 0;

                    if (sortDueDate) {
                        diff = d1 == null ? 1
                                : (d2 == null ? -1
                                        : item1.getDue_date().compareTo(item2.getDue_date()));
                    } else {
                        diff = d1 == null ? -1
                                : (d2 == null ? 1
                                        : item2.getDue_date().compareTo(item1.getDue_date()));
                    }
                }

                return diff;
            }
        });
    }
    // #endregion

    // #region ################################ GET
    @RequestMapping(method = { RequestMethod.GET }, value = { "/todos" })
    public GETResponse getAllToDos(
            @RequestParam(defaultValue = "1") int pag,
            @RequestParam(required = false) Boolean done,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Boolean sortPriority,
            @RequestParam(required = false) Boolean sortDueDate) {
        // TO AVOID UNNECESARY PROCESSES, WE STORE CACHE AND CHECK FOR CHANGES
        List<ToDo> filtered = null;
        LastMetrics lm = this.metrics.calculate(this.DB); // CHECK METRICS AGAIN
        Boolean isSame = this.previousParams.isSameAsPrevious(done, text, priority, sortPriority, sortDueDate);

        // SAME PARAMS, AND ALREADY AN ANSWER; SO TAKE BACK THE ANSWER
        if (isSame && lastResponse != null) {
            filtered = lastResponse;
        } else {
            // DIFFERENT PARAMS OR THERE'S NOT A PREVIOUS
            filtered = new ArrayList<>(DB.subList(0, DB.size()));

            // FILTER (IF ANY)
            if (done != null || text != null || priority != null) {
                filtered = this.filter(done, text, priority);
            }

            // SORT BY (IF ANY)
            if (sortDueDate != null || sortPriority != null) {
                sortList(filtered, sortPriority, sortDueDate);
            }

            lastResponse = filtered; // SAVE THE LIST FOR NEXT
        }

        // EVEN IF WE GOT THE SAME ANSWER, WE CAN SEND A DIFFERENT PAGE TO CLIENT
        int maxpage = (int) Math.max(Math.ceil(filtered.size() / 10f), 1);
        int page = (int) Math.min(Math.max(pag, 1), maxpage);
        List<ToDo> slice = filtered.subList(10 * (page - 1), Math.min(10 * page, filtered.size()));

        return (new GETResponse(slice, page, maxpage, lm));
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

        this.metrics.needsRecalculate();
        this.lastResponse = null; // FORCE RECALCULATE
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

                this.metrics.needsRecalculate();
                this.lastResponse = null; // FORCE RECALCULATE
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

                this.metrics.needsRecalculate();
                this.lastResponse = null; // FORCE RECALCULATE
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

                this.metrics.needsRecalculate();
                this.lastResponse = null; // FORCE RECALCULATE
                return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
            }
        }

        return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    }
    // #endregion

    /**
     * Shortcut to save previous Search Params and see if its the same or not
     */
    public class SearchParams {
        private Boolean done = null;
        private String text = null;
        private Priority priority = null;
        private Boolean sortPriority = null;
        private Boolean sortDueDate = null;

        public Boolean isSameAsPrevious(
                Boolean done,
                String text,
                Priority priority,
                Boolean sortPriority,
                Boolean sortDueDate) {
            Boolean check = true;

            if (done != this.done || text != this.text || priority != this.priority
                    || sortPriority != this.sortPriority || sortDueDate != this.sortDueDate) {
                check = false;
            }

            this.done = done;
            this.text = text;
            this.priority = priority;
            this.sortPriority = sortPriority;
            this.sortDueDate = sortDueDate;

            return check;
        }
    }
}
