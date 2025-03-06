package com.annyo.postmanclitest.person;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("person")
public class PersonController {

    @GetMapping("all")
    public ResponseEntity<List<Person>> getPerson() {
        return ResponseEntity.ok().body(Constant.getPersons());
    }

    @PostMapping("add")
    public ResponseEntity<Person> addPerson(@RequestBody Person person) {;
        return ResponseEntity.ok().body(Constant.addPerson(person));
    }

    @DeleteMapping("remove/{id}")
    public ResponseEntity<Person> removePerson(@PathVariable int id) {
        return ResponseEntity.ok().body(Constant.removePerson(id));
    }
}
