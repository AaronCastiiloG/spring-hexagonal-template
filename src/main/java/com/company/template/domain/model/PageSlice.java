package com.company.template.domain.model;

import java.util.List;

public record PageSlice<T>(List<T> content, long totalElements, int totalPages, int number, int size) {
}
