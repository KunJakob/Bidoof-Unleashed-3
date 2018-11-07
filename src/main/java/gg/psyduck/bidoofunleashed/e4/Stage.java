package gg.psyduck.bidoofunleashed.e4;

import gg.psyduck.bidoofunleashed.api.battlables.BU3Battlable;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface Stage extends BU3Battlable {

	List<UUID> getLeaders();

	int getNPCs();

	boolean isOpen();

	int getStage();

	String getPath();

	EliteFour getBelonging();
}
