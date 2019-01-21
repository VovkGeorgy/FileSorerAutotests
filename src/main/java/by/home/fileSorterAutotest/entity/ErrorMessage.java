package by.home.fileSorterAutotest.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Error message entity
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "error_message", schema = "public", catalog = "sorterBase")
public class ErrorMessage extends AbstractMessage {

    @Column(name = "type_of_error")
    private String typeOfError;

    public ErrorMessage() {
    }
}