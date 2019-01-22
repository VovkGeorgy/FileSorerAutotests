package by.home.fileSorterAutotest.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Exception message entity
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "exception_message", schema = "public", catalog = "sorterBase")
public class ExceptionMessage extends AbstractMessage {

    @Column(name = "type_of_exception")
    private String typeOfException;

    public ExceptionMessage() {
    }

    public ExceptionMessage(String fileName, boolean isValid) {
        this.fileName = fileName;
        this.isValid = isValid;
    }

    public ExceptionMessage(String typeOfException, String messageType, Long id, String message, String throwingTime, String
            fileName, boolean isValid) {
        this.typeOfException = typeOfException;
        this.messageType = messageType;
        this.id = id;
        this.message = message;
        this.throwingTime = throwingTime;
        this.fileName = fileName;
        this.isValid = isValid;
    }
}