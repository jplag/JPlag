void main() {
    Optional<String> oing1 = Optional.of("");
    Optional<String> oing2 = Optional.ofNullable("");
    Optional<String> oing3 = Optional.empty();

    oing1.orElseThrow();
    oing2.map(String::toCharArray).isPresent();
    oing3.filter(String::isBlank).orElse("");

    OptionalInt oing4 = OptionalInt.empty();
    oing4.orElseThrow();
}