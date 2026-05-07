package com.dossantosh.springfirstmodulith.core.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record KeysetPage<T extends Serializable>(List<T> content, boolean hasNext, boolean hasPrevious, Long nextId,
		Long previousId) implements Serializable {

	public KeysetPage {
		content = content == null ? List.of() : List.copyOf(content);
	}

	public static <R, T extends Serializable> KeysetPage<T> fromSlice(List<R> rows, int limit, Direction direction,
			Long lastId, Function<R, T> mapper, Function<T, Long> idExtractor) {
		Objects.requireNonNull(direction, "direction cannot be null");
		Objects.requireNonNull(mapper, "mapper cannot be null");
		Objects.requireNonNull(idExtractor, "idExtractor cannot be null");

		List<R> source = rows == null ? List.of() : rows;
		int pageSize = Math.max(limit, 0);
		boolean hasMore = source.size() > pageSize;

		List<T> content = source.stream().limit(pageSize).map(mapper).toList();
		if (direction == Direction.PREVIOUS) {
			content = reversedCopy(content);
		}

		if (content.isEmpty()) {
			return new KeysetPage<>(List.of(), false, false, null, null);
		}

		Long nextId = idExtractor.apply(content.getLast());
		Long previousId = idExtractor.apply(content.getFirst());
		boolean hasNext = direction == Direction.NEXT ? hasMore : lastId != null;
		boolean hasPrevious = direction == Direction.NEXT ? lastId != null : hasMore;

		return new KeysetPage<>(content, hasNext, hasPrevious, nextId, previousId);
	}

	private static <T> List<T> reversedCopy(List<T> values) {
		ArrayList<T> reversed = new ArrayList<>(values);
		Collections.reverse(reversed);
		return reversed;
	}
}
