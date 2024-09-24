package com.obss.mentorapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.obss.mentorapp.dto.CourseDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Setter
@Getter
@Entity
@Transactional
@Document(indexName = "course") // ElasticSearch index name
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {

    @Id // JPA'dan gelen ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(type = FieldType.Text) // ElasticSearch için tanımlama
    private String name;

    @ManyToOne(fetch = FetchType.EAGER) // Mentor ilişkisi EAGER olarak ayarlandı
    @JoinColumn(name = "mentor_id")
    @Field(type = FieldType.Object)
    private User mentor;

    @ManyToOne(fetch = FetchType.EAGER) // Mentee ilişkisi EAGER olarak ayarlandı
    @JoinColumn(name = "mentee_id")
    @Field(type = FieldType.Object)
    private User mentee;

    @Column(length = 1000)
    @Field(type = FieldType.Text) // Elasticsearch için Text olarak tanımlandı
    private String description;

    @Field(type = FieldType.Boolean) // Elasticsearch için boolean olarak tanımlandı
    private boolean isCompleted;

    @Field(type = FieldType.Boolean) // Kurs aktif/pasif durumu için yeni alan
    private boolean isActive;

    // Kursun fazlarını tutacak liste
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Phase> phases;

    // Mentor adını almak için yardımcı method
    public String getMentorName() {
        return mentor != null ? mentor.getUsername() : null;
    }

    // Mentee adını almak için yardımcı method
    public String getMenteeName() {
        return mentee != null ? mentee.getUsername() : "Mentee bilgisi eksik";
    }

    public Long getMentorId() {
        return mentor != null ? mentor.getId() : null; // Mentor varsa mentorun id'sini döner, yoksa null
    }

    public Long getMenteeId() {
        return mentee != null ? mentee.getId() : null; // Mentee varsa mentee'nin id'sini döner, yoksa null
    }

    // Kursun aktif olup olmadığını kontrol eden yardımcı method
    public boolean checkActiveStatus() {
        return mentee != null; // Eğer mentee atanmışsa kurs aktif, mentee yoksa pasif
    }

    // Kurs durumu güncellendiğinde aktif/pasif durumu otomatik ayarlanabilir
    public void updateStatus() {
        this.isActive = checkActiveStatus(); // Kursun aktif olup olmadığını belirle
    }

    public CourseDTO toModel(){
        return CourseDTO.builder()
                .id(id)
                .name(name)
                .mentorName(mentor != null ? mentor.getUsername() : "Mentor bilgisi eksik")  // Mentor bilgisi ekleniyor mu?
                .menteeName(mentee != null ? mentee.getUsername() : "Mentee bilgisi eksik")  // Mentee bilgisi ekleniyor mu?
                .description(description)
                .isCompleted(isCompleted)
                .isActive(isActive)
                .build();
    }

}
