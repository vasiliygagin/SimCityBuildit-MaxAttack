import java.util.Date;

public class Energy {

	private static final int TIME_TO_GENERATE_ENERGY_x1 = 15 * 60 * 1000;
	private static final int TIME_TO_GENERATE_ENERGY_x3 = 5 * 60 * 1000;
	private static final int HOURS_2 = 2 * 60 * 60 * 1000;
	private static final int WAR_LENGTH = 36 * 60 * 60 * 1000;

	private Date warStart;
	private long warEnd;
	private long x3Millis;

	public Energy(Date warStart) {
		this.warStart = warStart;
		warEnd = warStart.getTime() + WAR_LENGTH;
		x3Millis = warEnd - HOURS_2;
	}

	public int getGeneratedEnergy() {
		return getGeneratedEnergy(System.currentTimeMillis());
	}

	public int getFutureEnergy() {
		return getFutureEnergy(System.currentTimeMillis());
	}

	private int getGeneratedEnergy(long now) {

		int generatedEnergy;
		if (now < x3Millis) {
			generatedEnergy = (int) ((now - warStart.getTime()) / TIME_TO_GENERATE_ENERGY_x1);
		} else {
			generatedEnergy = (int) ((now - x3Millis) / TIME_TO_GENERATE_ENERGY_x3 + 34 * 4);
		}
		return 6 + generatedEnergy;
	}

	private int getFutureEnergy(long now) {

		int energyToGenerate;
		if (now < x3Millis) {
			energyToGenerate = (int) ((x3Millis - now) / TIME_TO_GENERATE_ENERGY_x1) + 24;
		} else {
			energyToGenerate = (int) ((warEnd - now) / TIME_TO_GENERATE_ENERGY_x3 + 24);
		}
		return energyToGenerate;
	}
}
