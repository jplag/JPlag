package de.jplag.merging;

public record AlteringParameters(int seed, int percent) {

    public AlteringParameters(int seed, int percent) {
        this.seed = seed < 0 ? 0 : seed;
        this.percent = percent < -1 ? -1 : percent;
    }

    public AlteringParameters() {
        this(0, -1);
    }

    public AlteringParameters withSeed(int seed) {
        return new AlteringParameters(seed < 0 ? 0 : seed, percent);
    }

    public AlteringParameters withPercent(int percent) {
        return new AlteringParameters(seed, percent < -1 ? -1 : percent);
    }
}
