package dev.J.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Entity
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String courseId;

    private String name;

    private int units;

    @ManyToOne(fetch = FetchType.LAZY)
    private Campus owningCampus;

    @OneToOne(optional = true, fetch = FetchType.LAZY)
    private Prerequisite rootPrerequisite;

    @Nullable
    private transient String subject = null;
    @Nullable
    private transient String number = null;

    private static final Pattern p = Pattern.compile("[a-zA-Z]+");

    public Course(){}


    public String getSubject(){
        if(subject != null) return subject;
        parseCourseId();
        return subject;
    }

    public String getNumber() {
        if(number != null) return number;
        parseCourseId();
        return number;
    }

    private void parseCourseId(){
        Matcher matcher = p.matcher(courseId);
        System.out.println(courseId);
        String subject;
        if(matcher.find()) subject = matcher.group();
        else throw new IllegalStateException("No matching course id");
        String number = courseId.substring(subject.length());
        this.subject = subject;
        this.number = number;
    }



}
