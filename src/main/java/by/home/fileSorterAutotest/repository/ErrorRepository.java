package by.home.fileSorterAutotest.repository;

import by.home.fileSorterAutotest.entity.ErrorMessage;
import org.springframework.data.repository.CrudRepository;

/**
 * Database repository for error message entity
 */
public interface ErrorRepository extends CrudRepository<ErrorMessage, Long> {
}
