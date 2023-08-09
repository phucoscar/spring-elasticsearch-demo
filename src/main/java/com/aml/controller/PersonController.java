package com.aml.controller;

import com.aml.dto.PersonDto;
import com.aml.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/add-person")
    public ResponseEntity<?> addPerson(@RequestBody PersonDto personDto) {
        personService.save(personDto);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PostMapping("/get-person-by-id")
    public ResponseEntity<?> getPerson(@RequestParam Long id) {
        return new ResponseEntity<>(personService.getPersonById(id), HttpStatus.OK);
    }
}
