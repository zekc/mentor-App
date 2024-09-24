package com.obss.mentorapp.service;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.obss.mentorapp.elastic.CourseElasticDTO;
import com.obss.mentorapp.elastic.CourseSearchRepository;
import com.obss.mentorapp.entity.Course;
import com.obss.mentorapp.mapper.CourseMapper;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CourseSearchService {

    private final CourseSearchRepository courseSearchRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserService userService;
    private final CourseMapper courseMapper; // Mapper'ı inject ettik

    public CourseSearchService(CourseSearchRepository courseSearchRepository,
                               ElasticsearchTemplate elasticsearchTemplate,
                               UserService userService,
                               CourseMapper courseMapper) { // Constructor ile inject ediyoruz
        this.courseSearchRepository = courseSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.userService = userService;
        this.courseMapper = courseMapper; // Inject edilen mapper
    }

    public List<Course> findAllCourses() {
        List<CourseElasticDTO> courseElasticDTOs = new ArrayList<>();
        courseSearchRepository.findAll().forEach(courseElasticDTOs::add);

        // Statik olmayan mapper kullanımı
        List<Course> courses = courseMapper.toEntityList(courseElasticDTOs);

        // Mentor ve mentee bilgilerini doldurmak için
        courses.forEach(course -> {
            if (course.getMentor() == null && course.getMentorId() != null) {
                course.setMentor(userService.findById(course.getMentorId()).orElse(null));
            }
            if (course.getMentee() == null && course.getMenteeId() != null) {
                course.setMentee(userService.findById(course.getMenteeId()).orElse(null));
            }
        });

        return courses;
    }

    public List<Course> searchCourses(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return findAllCourses();
        }

        Query searchQuery = MatchPhraseQuery.of(m -> m
                .field("description")
                .query(keyword)
        )._toQuery();

        NativeQuery query = NativeQuery.builder()
                .withQuery(searchQuery)
                .build();

        SearchHits<CourseElasticDTO> searchHits = elasticsearchTemplate.search(query, CourseElasticDTO.class);
        List<CourseElasticDTO> courseElasticDTOs = searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent())
                .toList();

        // Statik olmayan mapper kullanımı
        List<Course> courses = courseMapper.toEntityList(courseElasticDTOs);

        // Mentor bilgilerini doldurmak için
        courses.forEach(course -> {
            if (course.getMentor() == null && course.getMentorId() != null) {
                course.setMentor(userService.findById(course.getMentorId())
                        .orElseThrow(() -> new RuntimeException("Mentor not found for ID: " + course.getMentorId())));
            }
        });

        return courses;
    }

    public List<Course> searchCoursesByKeywordAndTopic(String keyword, String topic) {
        if (!StringUtils.hasText(keyword) && !StringUtils.hasText(topic)) {
            return findAllCourses();
        }

        List<Query> queries = new ArrayList<>();
        if (StringUtils.hasText(topic)) {
            queries.add(MatchPhraseQuery.of(m -> m
                    .field("name")
                    .query(topic)
            )._toQuery());
        }

        if (StringUtils.hasText(keyword)) {
            queries.add(MatchPhraseQuery.of(m -> m
                    .field("description")
                    .query(keyword)
            )._toQuery());
        }

        Query combinedQuery = Query.of(q -> q.bool(b -> b.must(queries)));

        NativeQuery query = NativeQuery.builder()
                .withQuery(combinedQuery)
                .build();

        SearchHits<CourseElasticDTO> searchHits = elasticsearchTemplate.search(query, CourseElasticDTO.class);
        List<CourseElasticDTO> courseElasticDTOs = searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent())
                .toList();

        // Statik olmayan mapper kullanımı
        return courseMapper.toEntityList(courseElasticDTOs);
    }

    public List<Course> searchCoursesByTopic(String topic) {
        if (!StringUtils.hasText(topic)) {
            return findAllCourses();
        }

        Query searchQuery = MatchPhraseQuery.of(m -> m
                .field("name")
                .query(topic)
        )._toQuery();

        NativeQuery query = NativeQuery.builder()
                .withQuery(searchQuery)
                .build();

        SearchHits<CourseElasticDTO> searchHits = elasticsearchTemplate.search(query, CourseElasticDTO.class);
        List<CourseElasticDTO> courseElasticDTOs = searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent())
                .toList();

        // Statik olmayan mapper kullanımı
        return courseMapper.toEntityList(courseElasticDTOs);
    }
}
