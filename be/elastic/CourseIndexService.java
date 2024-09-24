package com.obss.mentorapp.elastic;

import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.repository.CourseRepository;
import org.elasticsearch.ElasticsearchException;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CourseIndexService {

    private static final Logger logger = LoggerFactory.getLogger(CourseIndexService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSearchRepository courseSearchRepository;

    @PostConstruct
    @Async
    @Transactional(readOnly = true)
    public void reindexCourses() {
        try {
            courseSearchRepository.deleteAll();
            List<Course> courses = courseRepository.findAll();

            // Mentor ve mentee bilgilerini manuel olarak yükle
            courses.forEach(course -> {
                if (course.getMentor() != null) {
                    Hibernate.initialize(course.getMentor());
                }
                if (course.getMentee() != null) {
                    Hibernate.initialize(course.getMentee());
                }
            });

            // Course nesnelerini CourseElasticDTO'ya dönüştürüyoruz
            List<CourseElasticDTO> courseElasticDTOs = CourseElasticMapper.toElasticDTOList(courses);

            // DTO'ları Elasticsearch'e kaydediyoruz
            courseSearchRepository.saveAll(courseElasticDTOs);
            logger.info("All courses have been reindexed to Elasticsearch.");
        } catch (HibernateException e) {
            logger.error("Hibernate error occurred while reindexing courses: {}", e.getMessage(), e);
        } catch (ElasticsearchException e) {
            logger.error("Elasticsearch error occurred while reindexing courses: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("General error occurred while reindexing courses: {}", e.getMessage(), e);
        }
    }

    @Async
    @Transactional // Veri tutarlılığı açısından @Transactional ekliyoruz
    public void reindexCourse(Course course) {
        try {
            if (course.getMentor() != null) {
                Hibernate.initialize(course.getMentor()); // kontrol et
            }
            if (course.getMentee() != null) {
                Hibernate.initialize(course.getMentee()); // Mentee'yi manuel olarak yükle
            }

            // Tek bir course'u DTO'ya dönüştürüyoruz
            CourseElasticDTO courseElasticDTO = CourseElasticMapper.toElasticDTO(course);

            // DTO'yu Elasticsearch'e kaydediyoruz
            courseSearchRepository.save(courseElasticDTO);
            logger.info("Course {} has been reindexed to Elasticsearch.", course.getName());
        } catch (Exception e) {
            logger.error("Error occurred while reindexing course {}: {}", course.getName(), e);
        }
    }

    @Async
    @Transactional // Veri tutarlılığı açısından @Transactional ekliyoruz
    public void deleteCourseFromIndex(Long courseId) {
        try {
            courseSearchRepository.deleteById(courseId);
            logger.info("Course with ID {} has been deleted from Elasticsearch index.", courseId);
        } catch (Exception e) {
            logger.error("Error occurred while deleting course with ID {} from Elasticsearch index: {}", courseId, e);
        }
    }
}
