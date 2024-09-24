package com.obss.mentorapp.elastic;

import com.obss.mentorapp.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CourseListener {

    @Autowired
    private CourseIndexService courseIndexService;

    @EventListener
    public void handleCourseSaveOrUpdate(Course course) {
        // Course kaydedildiğinde veya güncellendiğinde DTO'yu Elasticsearch'e yeniden indeksliyoruz
        courseIndexService.reindexCourse(course);  // Zaten DTO dönüşümünü burada CourseIndexService yapıyor
    }

    @TransactionalEventListener
    public void handleCourseDelete(Course course) {
        // Course silindiğinde, Elasticsearch'ten ID'ye göre siliniyor
        courseIndexService.deleteCourseFromIndex(course.getId());
    }
}
