package com.obss.mentorapp.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "courseelasticsearchdto")
public class CourseElasticDTO {
    private Long id;
    private String name;
    private String description;
    private String mentorName;
    private Long mentorId;   // Yeni eklenen alan
    private String menteeName;
    private boolean isCompleted;
}
