package gg.psyduck.bidoofunleashed.api.battlables;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class Category {
	private final String id;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Category category = (Category) o;
		return id.equalsIgnoreCase(category.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id.toLowerCase());
	}
}
