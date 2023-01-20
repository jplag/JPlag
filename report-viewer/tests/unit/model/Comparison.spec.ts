import { Comparison } from "@/model/Comparison";

test('comparison similarity should be saved as is', () => {
    const comparison = new Comparison("studentA", "studentB", 0.5);
    expect(comparison.similarity).toBe(0.5);
})