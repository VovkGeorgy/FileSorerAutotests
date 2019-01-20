package by.home.fileSorterAutotest.repository;

import by.home.fileSorterAutotest.entity.ExceptionMessage;
import org.springframework.data.repository.CrudRepository;

public interface ExceptionRepository extends CrudRepository<ExceptionMessage, Long> {
}
