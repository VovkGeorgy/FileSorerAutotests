package by.home.fileSorterAutotest.repository;

import by.home.fileSorterAutotest.entity.ExceptionMessage;
import org.springframework.data.repository.CrudRepository;

/**
 * Database repository for exception message entity
 */
public interface ExceptionRepository extends CrudRepository<ExceptionMessage, Long> {
}
