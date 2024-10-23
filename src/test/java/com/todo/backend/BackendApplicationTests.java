package com.todo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.todo.backend.controller.ToDoController;
import com.todo.backend.model.BasicToDo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ToDoController.class)
public class BackendApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Test
	public void testGetAll_withoutParams_thenSuccess() throws Exception {
		this.mockMvc.perform(get("/todos")).andExpect(status().isOk());
	}

	@Test
	public void testGetOne_withoutID_thenFailure() throws Exception {
		this.mockMvc.perform(get("/todos/-1")).andExpect(status().isBadRequest());
	}

	@Test
	public void testPostTodo_withoutParams_thenFailure() throws Exception {
		BasicToDo basicToDo = new BasicToDo();
		basicToDo.setText("");
		basicToDo.setDue_date(null);

		this.mockMvc.perform(post("/todos", basicToDo)).andExpect(status().isBadRequest());
	}

	@Test
	public void testDelete_nonExistingItem_thenFailure() throws Exception {
		this.mockMvc.perform(delete("/todos/1000/delete")).andExpect(status().isNotFound());
	}
}
