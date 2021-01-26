package nl.parrotlync.discovsuite.spigot.model;

public enum TimeValue {
    DAY(6000L), NIGHT(18000L), SUNRISE(47500L), SUNSET(12750L);

    private final Long value;

    public Long getValue() {
        return this.value;
    }

    private TimeValue(Long value) {
        this.value = value;
    }
}
