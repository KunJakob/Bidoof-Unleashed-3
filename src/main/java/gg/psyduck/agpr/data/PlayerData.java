package gg.psyduck.agpr.data;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PlayerData {

	private final String name;
	private List<Badge> badges = Lists.newArrayList();
}
