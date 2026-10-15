void main() {
    List<Integer> source1 = List.of(1, 2, 3, 4);
    int[] source2 = {1, 2, 3, 4};
    String source3 = "1234";

    IO.println(source1.stream().filter(i -> i % 2 == 0).map(i -> i).toList());
    IO.println(Arrays.toString(Arrays.stream(source2).filter(i -> i % 2 == 0).toArray()));
    IO.println(source3.chars().filter(i -> i % 2 == 0).mapToObj(Integer::toString).mapToLong(Object::hashCode).sum());
}
