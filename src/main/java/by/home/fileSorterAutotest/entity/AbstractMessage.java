package by.home.fileSorterAutotest.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


/**
 * Cass of abstract message entity
 */
@Data
@MappedSuperclass
public class AbstractMessage {

    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name = "message_type")
    protected String messageType;

    @Column(name = "message")
    protected String message;

    @Column(name = "throwing_time")
    protected String throwingTime;

    @Column(name = "file_name")
    protected String fileName;

    @Column(name = "is_valid")
    protected boolean isValid;
}
