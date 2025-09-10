package co.com.pragma.model.pagination;

import java.util.List;

public record PaginationResponse<T> (
         List<T> content,
         int page,
         int size,
         long totalElements,
         int totalPages
){}
