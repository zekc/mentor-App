package com.obss.mentorapp.service;

import com.obss.mentorapp.entity.Topic;
import com.obss.mentorapp.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    public Optional<Topic> findById(Long id) {
        return topicRepository.findById(id);
    }

    public Topic save(Topic topic) {
        return topicRepository.save(topic);
    }

    public void deleteById(Long id) {
        topicRepository.deleteById(id);
    }

    public Optional<Topic> findByName(String name) {
        return topicRepository.findByName(name);
    }

}
