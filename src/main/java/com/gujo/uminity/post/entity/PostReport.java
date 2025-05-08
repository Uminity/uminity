package com.gujo.uminity.post.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_reports")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "report_at")
    private LocalDateTime reportAt;

    @Column(columnDefinition = "TEXT")
    private String content;
}
