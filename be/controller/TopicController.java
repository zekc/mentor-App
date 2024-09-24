package com.obss.mentorapp.controller;

import com.obss.mentorapp.entity.Topic;
import com.obss.mentorapp.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        List<Topic> topics = topicService.findAll();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        return topicService.findById(id)
                .map(topic -> new ResponseEntity<>(topic, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {
        Topic createdTopic = topicService.save(topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody Topic topicDetails) {
        return topicService.findById(id)
                .map(topic -> {
                    topic.setName(topicDetails.getName());
                    Topic updatedTopic = topicService.save(topic);
                    return new ResponseEntity<>(updatedTopic, HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        return topicService.findById(id)
                .map(topic -> {
                    topicService.deleteById(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
