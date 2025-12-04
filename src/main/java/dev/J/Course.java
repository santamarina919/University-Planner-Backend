package dev.J;

import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String courseId;

    String name;

    int units;

    @ManyToOne(fetch = FetchType.LAZY)
    Campus owningCampus;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    Prerequisite rootPrerequisite;

    @Nullable
    private String subject = null;

    @Nullable
    private String number = null;

    private static final Pattern p = Pattern.compile("[a-zA-z]+");

    public Course(){}


    public String subject(){
        if(subject != null) return subject;
        parseCourseId();
        return subject;
    }

    public String number() {
        if(number != null) return number;
        parseCourseId();
        return number;
    }

    private void parseCourseId(){
        Matcher matcher = p.matcher(courseId);
        String subject = matcher.group();
        String number = courseId.substring(subject.length());
        this.subject = subject;
        this.number = number;
    }

}
