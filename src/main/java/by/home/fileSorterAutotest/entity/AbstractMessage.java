package by.home.fileSorterAutotest.entity;

import lombok.Data;

import javax.persistence.*;


/**
 * Cass of abstract message entity
 */
@Data
@MappedSuperclass
public class AbstractMessage {

    enum MessageType {
        ERROR,
        EXCEPTION,
        CRITICAL_ERROR,
        NOTICE
    }

    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    protected MessageType messageType;

    @Column(name = "message")
    protected String message;

    @Column(name = "throwing_time")
    protected String throwingTime;

    @Column(name = "file_name")
    protected String fileName;

    @Column(name = "is_valid")
    protected boolean isValid;
}
