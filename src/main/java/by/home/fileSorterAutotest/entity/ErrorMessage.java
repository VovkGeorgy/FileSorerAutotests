package by.home.fileSorterAutotest.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Error message entity
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "error_message", schema = "public", catalog = "sorterBase")
public class ErrorMessage extends AbstractMessage {

    enum ErrorType {
        STACKOVERFLOW,
        OUT_OF_MEMORY,
        LINKAGE,
        AWT
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_error")
    private ErrorType typeOfError;
}