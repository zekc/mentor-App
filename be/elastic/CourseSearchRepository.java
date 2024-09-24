package com.obss.mentorapp.elastic;

import com.obss.mentorapp.elastic.CourseElasticDTO;
import com.obss.mentorapp.entity.Course;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSearchRepository extends ElasticsearchRepository<CourseElasticDTO, Long> {

    List<CourseElasticDTO> findByDescriptionContaining(String keyword);

    List<CourseElasticDTO> findByNameContaining(String keyword);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}]}}")
    List<CourseElasticDTO> searchByName(String name);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"description\": \"?0\"}}]}}")
    List<CourseElasticDTO> searchByDescription(String description);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}, {\"match\": {\"description\": \"?1\"}}]}}")
    List<CourseElasticDTO> searchByNameAndDescription(String name, String description);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.mentor WHERE c.id = :id")
    Course findByIdWithMentor(@Param("id") Long id);


}
