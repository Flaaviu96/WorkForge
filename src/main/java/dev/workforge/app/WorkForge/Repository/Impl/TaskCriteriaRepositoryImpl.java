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
    private EntityManager entityManager;

    private static final int PAGE_SIZE = 9;

    @Override
    public PageResultDTO<TaskSummaryDTO> findTasksByFilter(TaskFilter filter, long projectId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TaskSummaryDTO> cq = cb.createQuery(TaskSummaryDTO.class);
        Root<Task> root = cq.from(Task.class);

        List<Predicate> predicates = buildPredicates(filter, cb, root, projectId);
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        cq.select(cb.construct(
                TaskSummaryDTO.class,
                root.get("id"),
                root.get("taskName"),
                root.get("state").get("name"),
                root.get("createdDate"),
                root.get("taskMetadata").get("assignedTo")
        ));

        cq.orderBy(filter.isNextPage() || filter.isFirstSearch()
                ? cb.asc(root.get("id"))
                : cb.desc(root.get("id")));

        TypedQuery<TaskSummaryDTO> query = entityManager.createQuery(cq);
        query.setMaxResults(PAGE_SIZE + 1);
        List<TaskSummaryDTO> results = query.getResultList();

        boolean hasMore = results.size() > PAGE_SIZE;
        if (hasMore) {
            results = results.subList(0, PAGE_SIZE);
        }

        if (!filter.isNextPage() && !filter.isFirstSearch()) {
            Collections.reverse(results);
        }
        Long prevCursor = 0L, nextCursor = 0L;
        if (!filter.isNextPage()) {
            prevCursor = !hasMore ? 0 : results.get(0).taskId();
            nextCursor = results.isEmpty() ? 0 : results.get(results.size() - 1).taskId();
        } else {
            prevCursor = results.isEmpty() ? 0 : results.get(0).taskId();
            nextCursor = !hasMore ? 0 : results.get(results.size() - 1).taskId();
        }
        return new PageResultDTO<>(results, hasMore, nextCursor, prevCursor);
    }

    private List<Predicate> buildPredicates(TaskFilter filter, CriteriaBuilder cb, Root<Task> root, long projectId) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            if (filter.getAssignedTo() != null) {
                predicates.add(cb.equal(root.get("taskMetadata").get("assignedTo"), filter.getAssignedTo()));
            }
            if (filter.getTaskName() != null && isValid(filter.getTaskName())) {
                predicates.add(cb.like(
                        cb.lower(root.get("taskName")), normalizeLikePattern(filter.getTaskName())
                ));
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
                        ? cb.greaterThan(root.get("id"), filter.getCursorTaskId())
                        : cb.lessThan(root.get("id"), filter.getCursorTaskId());
                predicates.add(cursorPredicate);
            }
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

    private boolean isValid(String taskname) {
        String regex = "^(?!\\*+$)(?!.*\\*{2,}).*$";
        return taskname.matches(regex);
    }
}
