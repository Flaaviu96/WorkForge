package dev.workforge.app.WorkForge.Repository.Impl;


import dev.workforge.app.WorkForge.DTO.PageResultDTO;
import dev.workforge.app.WorkForge.DTO.TaskFilter;
import dev.workforge.app.WorkForge.DTO.TaskSummaryDTO;
import dev.workforge.app.WorkForge.Model.Task;
import dev.workforge.app.WorkForge.Repository.TaskCriteriaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class TaskCriteriaRepositoryImpl implements TaskCriteriaRepository {

    @PersistenceContext
    EntityManager entityManager;

    private static final int PAGE_SIZE = 9;

    @Override
    public PageResultDTO<TaskSummaryDTO> findTasksByFilter(TaskFilter taskFilter, long projectId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TaskSummaryDTO> cq = cb.createQuery(TaskSummaryDTO.class);
        Root<Task> taskRoot = cq.from(Task.class);

        List<Predicate> predicates = buildPredicates(taskFilter, cb, taskRoot, projectId);

        cq.select(cb.construct(
                TaskSummaryDTO.class,
                taskRoot.get("id"),
                taskRoot.get("taskName"),
                taskRoot.get("state").get("name"),
                taskRoot.get("createdDate"),
                taskRoot.get("taskMetadata").get("assignedTo")
        ));

        cq.where(predicates.toArray(new Predicate[0]));

        if (taskFilter.isNextPage() || taskFilter.isFirstSearch()) {
            cq.orderBy(cb.asc(taskRoot.get("id")));
        } else {
            cq.orderBy(cb.desc(taskRoot.get("id")));
        }

        TypedQuery<TaskSummaryDTO> query = entityManager.createQuery(cq);
        query.setMaxResults(PAGE_SIZE + 1);

        List<TaskSummaryDTO> results = query.getResultList();
        boolean hasNext = results.size() > PAGE_SIZE;

        Long nextCursorId = null;

        if (!taskFilter.isNextPage() && !taskFilter.isFirstSearch()) {
            Collections.reverse(results);
        }

        if (hasNext) {
            nextCursorId =  results.get(results.size() - 1).taskId();
            results = results.subList(0, PAGE_SIZE);
        }

        Long prevCursorId = !results.isEmpty() ? results.get(0).taskId() : null;

        return new PageResultDTO<>(results, hasNext, nextCursorId, prevCursorId);
    }

    private List<Predicate> buildPredicates(TaskFilter filter, CriteriaBuilder cb, Root<Task> root, long projectId) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getAssignedTo() != null) {
            predicates.add(cb.equal(root.get("taskMetadata").get("assignedTo"), filter.getAssignedTo()));
        }

        if (filter.getTaskName() != null) {
            String likePattern = normalizeLikePattern(filter.getTaskName());
            predicates.add(cb.like(cb.lower(root.get("taskName")), likePattern));
        }

        if (filter.getState() != null) {
            predicates.add(cb.equal(root.get("state").get("name"), filter.getState()));
        }

        if (filter.getCreatedDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), filter.getCreatedDateFrom()));
        }

        if (filter.getCreatedDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), filter.getCreatedDateTo()));
        }

        if (filter.getCursorTaskId() != 0) {
            Predicate cursorPredicate = filter.isNextPage()
                    ? cb.greaterThanOrEqualTo(root.get("id"), filter.getCursorTaskId())
                    : cb.lessThanOrEqualTo(root.get("id"), filter.getCursorTaskId());
            predicates.add(cursorPredicate);
        }

        predicates.add(cb.equal(root.get("project").get("id"), projectId));
        return predicates;
    }

    private String normalizeLikePattern(String input) {
        return input
                .replaceAll("\\*+", "%")
                .replaceAll("^%+", "%")
                .replaceAll("%+$", "%")
                .toLowerCase();
    }
}
