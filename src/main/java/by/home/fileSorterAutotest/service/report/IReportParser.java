package by.home.fileSorterAutotest.service.report;

import by.home.fileSorterAutotest.entity.AbstractMessage;

import java.io.File;

/**
 * Declare method for file parsers
 */
public interface IReportParser<T extends AbstractMessage> {

    /**
     * Method parse input file
     *
     * @param file input file
     * @return entity from a parsed file
     */
    T parseFile(File file);
}
