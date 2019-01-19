package by.home.fileSorterAutotest.repository;

import by.home.fileSorterAutotest.entity.ErrorMessage;
import org.springframework.data.repository.CrudRepository;

public interface ErrorRepository extends CrudRepository<ErrorMessage, Long> {
}
