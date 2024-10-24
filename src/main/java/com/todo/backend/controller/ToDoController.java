package com.todo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todo.backend.dto.ToDoDTO;
import com.todo.backend.model.GETResponse;
import com.todo.backend.model.SearchParams;
import com.todo.backend.model.ToDo.Priority;
import com.todo.backend.service.impl.ToDoServiceImpl;

import java.time.Instant;

@RestController
@CrossOrigin(origins = "*")
public class ToDoController {

    @Autowired
    private ToDoServiceImpl toDoService;

    // #region ################################ FILTER & SORTING
    // private ResponseEntity<String> toggleDoneUndone(int id, Integer inPage,
    // boolean markedAs) {
    // boolean found = false;

    // if (inPage == null) {
    // for (ToDo toDo : DB) {
    // if (toDo.getId() == id) {
    // toDo.setDone(markedAs);
    // found = true;
    // break;
    // }
    // }
    // } else {
    // // IF inPage EXISTS, WE TOGGLE ALL FROM THE LAST RESPONSE (IF ANY)
    // List<ToDo> aux = this.lastResponse == null ? this.DB : this.lastResponse;

    // int maxpage = (int) Math.max(Math.ceil(aux.size() / 10f), 1);
    // int page = (int) Math.min(Math.max(inPage, 1), maxpage);
    // List<ToDo> slice = aux.subList(10 * (page - 1), Math.min(10 * page,
    // aux.size()));

    // for (ToDo toDo : slice)
    // toDo.setDone(markedAs);

    // found = true;
    // }

    // this.metrics.needsRecalculate();
    // this.lastResponse = null; // FORCE RECALCULATE

    // if (found)
    // return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
    // return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
    // }

    // private List<ToDo> filter(Boolean done, String text, Priority priority) {
    // List<ToDo> filtered = new ArrayList<>(); // CLEAR ALL

    // for (ToDo item : DB) {
    // if (done != null && item.getDone() != done)
    // continue;
    // if (priority != null && item.getPriority() != priority)
    // continue;
    // if (text != null && !item.getText().contains(text))
    // continue;

    // filtered.add(item); // FILTERS PASSED
    // }

    // return filtered;
    // }

    // private void sortList(List<ToDo> filtered, Boolean sortPriority, Boolean
    // sortDueDate, Boolean sortText) {
    // Comparator<ToDo> comparator = Comparator.comparing(ToDo::getId);

    // if (sortPriority != null) {
    // comparator = Comparator.comparing(ToDo::getPriority);
    // if (!sortPriority) {
    // comparator = comparator.reversed();
    // }
    // }

    // if (sortDueDate != null) {
    // Comparator<ToDo> dueDateComparator = Comparator.comparing(ToDo::getDueDate,
    // Comparator.nullsLast(Comparator.naturalOrder()));
    // if (!sortDueDate) {
    // dueDateComparator = dueDateComparator.reversed();
    // }
    // comparator = comparator.thenComparing(dueDateComparator);
    // }

    // if (sortText != null) {
    // Comparator<ToDo> textComparator = Comparator.comparing(ToDo::getText);
    // if (!sortText) {
    // textComparator = textComparator.reversed();
    // }
    // comparator = comparator.thenComparing(textComparator);
    // }

    // filtered.sort(comparator);
    // }
    // #endregion

    // #region ################################ GET
    @RequestMapping(method = { RequestMethod.GET }, value = { "/todos" })
    public ResponseEntity<GETResponse> getToDos(SearchParams searchParams) {
        return toDoService.getToDos(searchParams);
    }

    @RequestMapping(method = { RequestMethod.GET }, value = { "/todos/{id}" })
    public ResponseEntity<?> getById(@PathVariable int id) {
        return toDoService.getById(id);
    }
    // #endregion

    // #region ################################ POST
    @RequestMapping(method = { RequestMethod.POST }, value = { "/todos" })
    public ResponseEntity<?> createToDo(@RequestBody ToDoDTO toDo) {
        // Check for validation of empty text or text length > 120
        if (toDo.getText().length() <= 0 || toDo.getText().length() > 120)
            return new ResponseEntity<>("Text length must be 120 or lower", HttpStatus.BAD_REQUEST);

        // Check for non null properties
        if (toDo.getPriority() == null)
            return new ResponseEntity<>("Priority cannot be null", HttpStatus.BAD_REQUEST);

        // Check for invalid priority
        try {
            Priority.valueOf(toDo.getPriority().toString());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid priority", HttpStatus.BAD_REQUEST);
        }

        // Check for invalid due date
        if (toDo.getDueDate() != null) {
            try {
                Instant.parse(toDo.getDueDate().toString());
            } catch (Exception e) {
                return new ResponseEntity<>("Invalid due date", HttpStatus.BAD_REQUEST);
            }
        }

        // Create the ToDo item
        return toDoService.create(toDo);
    }

    @RequestMapping(method = { RequestMethod.PUT }, value = { "/todos/{id}" })
    public ResponseEntity<?> updateToDo(@PathVariable int id, @RequestBody ToDoDTO toDo) {
        // Check for invalid ID
        if (id < 0)
            return new ResponseEntity<>("Invalid ID", HttpStatus.BAD_REQUEST);

        // Check for invalid text length
        if (toDo.getText().length() <= 0 || toDo.getText().length() > 120)
            return new ResponseEntity<>("Text length must be 120 or lower", HttpStatus.BAD_REQUEST);

        // Check for non null properties
        if (toDo.getPriority() == null)
            return new ResponseEntity<>("Priority cannot be null", HttpStatus.BAD_REQUEST);

        // Check for invalid priority
        try {
            Priority.valueOf(toDo.getPriority().toString());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid priority", HttpStatus.BAD_REQUEST);
        }

        // Check for invalid due date
        if (toDo.getDueDate() != null) {
            try {
                Instant.parse(toDo.getDueDate().toString());
            } catch (Exception e) {
                return new ResponseEntity<>("Invalid due date", HttpStatus.BAD_REQUEST);
            }
        }

        // Update the ToDo item
        return toDoService.update(id, toDo);
    }
    // #endregion

    // #region ################################ DONE/UNDONE
    @RequestMapping(method = { RequestMethod.POST }, value = { "/todos/{id}/done" })
    public ResponseEntity<?> markDone(@PathVariable int id, @RequestParam(required = false) Integer inPage) {
        return toDoService.toggleDone(id, true);
    }

    @RequestMapping(method = { RequestMethod.PUT }, value = { "/todos/{id}/undone" })
    public ResponseEntity<?> markUndone(@PathVariable int id, @RequestParam(required = false) Integer inPage) {
        return toDoService.toggleDone(id, false);
    }
    // #endregion

    // #region ################################ DELETE
    @RequestMapping(method = { RequestMethod.DELETE }, value = { "/todos/{id}/delete" })
    public ResponseEntity<?> delete(@PathVariable int id) {
        return toDoService.delete(id);
    }
    // #endregion
}
