package com.gurkensalat.calendar.perrypedia.releasecalendar;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WikiPageRepository extends CrudRepository<WikiPage, Long>
{
    List<WikiPage> findBySeriesPrefixAndIssueNumber(String seriesPrefix, int number);
}
