import store from "@/store/store";
import {ComparisonFactory} from "@/model/factories/ComparisonFactory";


beforeEach(() => {
    store.replaceState({
        "submissionIdsToComparisonFileName": new Map<string, Map<string, string>>(),
        "anonymous": new Set(),
        "files": {
            "A-C.json": "{\"id1\":\"A\",\"id2\":\"C\",\"similarity\":0.9966329966329966,\"matches\":[{\"file1\":\"A\\\\GSTiling.java\",\"file2\":\"C\\\\GSTiling.java\",\"start1\":6,\"end1\":247,\"start2\":6,\"end2\":247,\"tokens\":345},{\"file1\":\"A\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":1,\"end1\":218,\"start2\":1,\"end2\":218,\"tokens\":324},{\"file1\":\"A\\\\Structure.java\",\"file2\":\"C\\\\Structure.java\",\"start1\":1,\"end1\":118,\"start2\":1,\"end2\":118,\"tokens\":174},{\"file1\":\"A\\\\Match.java\",\"file2\":\"C\\\\Match.java\",\"start1\":1,\"end1\":51,\"start2\":1,\"end2\":51,\"tokens\":104},{\"file1\":\"A\\\\Table.java\",\"file2\":\"C\\\\Table.java\",\"start1\":5,\"end1\":60,\"start2\":5,\"end2\":60,\"tokens\":92},{\"file1\":\"A\\\\Token.java\",\"file2\":\"C\\\\Token.java\",\"start1\":1,\"end1\":47,\"start2\":1,\"end2\":47,\"tokens\":75},{\"file1\":\"A\\\\Matches.java\",\"file2\":\"C\\\\Matches.java\",\"start1\":3,\"end1\":57,\"start2\":3,\"end2\":57,\"tokens\":70}]}",
            "B-A.json": "{\"id1\":\"B\",\"id2\":\"A\",\"similarity\":0.2457689477557027,\"matches\":[{\"file1\":\"B\\\\Table.java\",\"file2\":\"A\\\\Table.java\",\"start1\":5,\"end1\":60,\"start2\":5,\"end2\":60,\"tokens\":92},{\"file1\":\"B\\\\Token.java\",\"file2\":\"A\\\\Token.java\",\"start1\":1,\"end1\":47,\"start2\":1,\"end2\":47,\"tokens\":75}]}",
            "B-C.json": "{\"id1\":\"B\",\"id2\":\"C\",\"similarity\":0.2457689477557027,\"matches\":[{\"file1\":\"B\\\\Table.java\",\"file2\":\"C\\\\Table.java\",\"start1\":5,\"end1\":60,\"start2\":5,\"end2\":60,\"tokens\":92},{\"file1\":\"B\\\\Token.java\",\"file2\":\"C\\\\Token.java\",\"start1\":1,\"end1\":47,\"start2\":1,\"end2\":47,\"tokens\":75}]}",
            "B-D.json": "{\"id1\":\"B\",\"id2\":\"D\",\"similarity\":0.2827868852459016,\"matches\":[{\"file1\":\"B\\\\Token.java\",\"file2\":\"D\\\\Token.java\",\"start1\":16,\"end1\":47,\"start2\":14,\"end2\":44,\"tokens\":59},{\"file1\":\"B\\\\Table.java\",\"file2\":\"D\\\\Table.java\",\"start1\":30,\"end1\":60,\"start2\":22,\"end2\":52,\"tokens\":50},{\"file1\":\"B\\\\Table.java\",\"file2\":\"D\\\\Table.java\",\"start1\":5,\"end1\":21,\"start2\":5,\"end2\":21,\"tokens\":29}]}",
            "D-A.json": "{\"id1\":\"D\",\"id2\":\"A\",\"similarity\":0.7787255393878575,\"matches\":[{\"file1\":\"D\\\\Structure.java\",\"file2\":\"A\\\\Structure.java\",\"start1\":3,\"end1\":120,\"start2\":1,\"end2\":118,\"tokens\":174},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"A\\\\Submission.java\",\"start1\":3,\"end1\":111,\"start2\":1,\"end2\":109,\"tokens\":146},{\"file1\":\"D\\\\Match.java\",\"file2\":\"A\\\\Match.java\",\"start1\":3,\"end1\":53,\"start2\":1,\"end2\":51,\"tokens\":104},{\"file1\":\"D\\\\Matches.java\",\"file2\":\"A\\\\Matches.java\",\"start1\":5,\"end1\":59,\"start2\":3,\"end2\":57,\"tokens\":70},{\"file1\":\"D\\\\Token.java\",\"file2\":\"A\\\\Token.java\",\"start1\":14,\"end1\":44,\"start2\":16,\"end2\":47,\"tokens\":59},{\"file1\":\"D\\\\Table.java\",\"file2\":\"A\\\\Table.java\",\"start1\":22,\"end1\":52,\"start2\":30,\"end2\":60,\"tokens\":50},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"A\\\\Submission.java\",\"start1\":165,\"end1\":194,\"start2\":173,\"end2\":203,\"tokens\":42},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"A\\\\Submission.java\",\"start1\":129,\"end1\":155,\"start2\":134,\"end2\":160,\"tokens\":41},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"A\\\\Submission.java\",\"start1\":112,\"end1\":127,\"start2\":116,\"end2\":132,\"tokens\":30},{\"file1\":\"D\\\\Table.java\",\"file2\":\"A\\\\Table.java\",\"start1\":5,\"end1\":21,\"start2\":5,\"end2\":21,\"tokens\":29},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"A\\\\Submission.java\",\"start1\":156,\"end1\":163,\"start2\":164,\"end2\":171,\"tokens\":18},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"A\\\\Submission.java\",\"start1\":195,\"end1\":202,\"start2\":204,\"end2\":211,\"tokens\":13}]}",
            "D-C.json": "{\"id1\":\"D\",\"id2\":\"C\",\"similarity\":0.7787255393878575,\"matches\":[{\"file1\":\"D\\\\Structure.java\",\"file2\":\"C\\\\Structure.java\",\"start1\":3,\"end1\":120,\"start2\":1,\"end2\":118,\"tokens\":174},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":3,\"end1\":111,\"start2\":1,\"end2\":109,\"tokens\":146},{\"file1\":\"D\\\\Match.java\",\"file2\":\"C\\\\Match.java\",\"start1\":3,\"end1\":53,\"start2\":1,\"end2\":51,\"tokens\":104},{\"file1\":\"D\\\\Matches.java\",\"file2\":\"C\\\\Matches.java\",\"start1\":5,\"end1\":59,\"start2\":3,\"end2\":57,\"tokens\":70},{\"file1\":\"D\\\\Token.java\",\"file2\":\"C\\\\Token.java\",\"start1\":14,\"end1\":44,\"start2\":16,\"end2\":47,\"tokens\":59},{\"file1\":\"D\\\\Table.java\",\"file2\":\"C\\\\Table.java\",\"start1\":22,\"end1\":52,\"start2\":30,\"end2\":60,\"tokens\":50},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":165,\"end1\":194,\"start2\":173,\"end2\":203,\"tokens\":42},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":129,\"end1\":155,\"start2\":134,\"end2\":160,\"tokens\":41},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":112,\"end1\":127,\"start2\":116,\"end2\":132,\"tokens\":30},{\"file1\":\"D\\\\Table.java\",\"file2\":\"C\\\\Table.java\",\"start1\":5,\"end1\":21,\"start2\":5,\"end2\":21,\"tokens\":29},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":156,\"end1\":163,\"start2\":164,\"end2\":171,\"tokens\":18},{\"file1\":\"D\\\\Submission.java\",\"file2\":\"C\\\\Submission.java\",\"start1\":195,\"end1\":202,\"start2\":204,\"end2\":211,\"tokens\":13}]}",
            "E-A.json": "{\"id1\":\"E\",\"id2\":\"A\",\"similarity\":0.0,\"matches\":[]}",
            "E-B.json": "{\"id1\":\"E\",\"id2\":\"B\",\"similarity\":0.0,\"matches\":[]}",
            "E-C.json": "{\"id1\":\"E\",\"id2\":\"C\",\"similarity\":0.0,\"matches\":[]}",
            "E-D.json": "{\"id1\":\"E\",\"id2\":\"D\",\"similarity\":0.0,\"matches\":[]}",
            "overview.json": "{\"jplag_version\":{\"major\":0,\"minor\":0,\"patch\":0},\"submission_folder_path\":[\"C:\\\\Users\\\\23651\\\\Desktop\\\\JPlag\\\\core\\\\src\\\\test\\\\resources\\\\de\\\\jplag\\\\samples\\\\PartialPlagiarism\"],\"base_code_folder_path\":\"\",\"language\":\"Javac based AST plugin\",\"file_extensions\":[\".java\",\".JAVA\"],\"submission_id_to_display_name\":{\"A\":\"A\",\"B\":\"B\",\"C\":\"C\",\"D\":\"D\",\"E\":\"E\"},\"submission_ids_to_comparison_file_name\":{\"A\":{\"B\":\"B-A.json\",\"C\":\"A-C.json\",\"D\":\"D-A.json\",\"E\":\"E-A.json\"},\"B\":{\"A\":\"B-A.json\",\"C\":\"B-C.json\",\"D\":\"B-D.json\",\"E\":\"E-B.json\"},\"C\":{\"A\":\"A-C.json\",\"B\":\"B-C.json\",\"D\":\"D-C.json\",\"E\":\"E-C.json\"},\"D\":{\"A\":\"D-A.json\",\"B\":\"B-D.json\",\"C\":\"D-C.json\",\"E\":\"E-D.json\"},\"E\":{\"A\":\"E-A.json\",\"B\":\"E-B.json\",\"C\":\"E-C.json\",\"D\":\"E-D.json\"}},\"failed_submission_names\":[],\"excluded_files\":[],\"match_sensitivity\":9,\"date_of_execution\":\"14/12/22\",\"execution_time\":40,\"metrics\":[{\"name\":\"AVG\",\"distribution\":[1,0,2,0,0,0,0,3,0,4],\"topComparisons\":[{\"first_submission\":\"A\",\"second_submission\":\"C\",\"similarity\":0.9966329966329966},{\"first_submission\":\"D\",\"second_submission\":\"A\",\"similarity\":0.7787255393878575},{\"first_submission\":\"D\",\"second_submission\":\"C\",\"similarity\":0.7787255393878575},{\"first_submission\":\"B\",\"second_submission\":\"D\",\"similarity\":0.2827868852459016},{\"first_submission\":\"B\",\"second_submission\":\"A\",\"similarity\":0.2457689477557027},{\"first_submission\":\"B\",\"second_submission\":\"C\",\"similarity\":0.2457689477557027},{\"first_submission\":\"E\",\"second_submission\":\"A\",\"similarity\":0.0},{\"first_submission\":\"E\",\"second_submission\":\"D\",\"similarity\":0.0},{\"first_submission\":\"E\",\"second_submission\":\"B\",\"similarity\":0.0},{\"first_submission\":\"E\",\"second_submission\":\"C\",\"similarity\":0.0}],\"description\":\"Average of both program coverages. This is the default similarity which works in most cases: Matches with a high average similarity indicate that the programs work in a very similar way.\"},{\"name\":\"MAX\",\"distribution\":[5,1,0,0,0,0,0,0,0,4],\"topComparisons\":[{\"first_submission\":\"A\",\"second_submission\":\"C\",\"similarity\":0.9966329966329966},{\"first_submission\":\"B\",\"second_submission\":\"A\",\"similarity\":0.9766081871345029},{\"first_submission\":\"B\",\"second_submission\":\"C\",\"similarity\":0.9766081871345029},{\"first_submission\":\"D\",\"second_submission\":\"A\",\"similarity\":0.9639751552795031},{\"first_submission\":\"D\",\"second_submission\":\"C\",\"similarity\":0.9639751552795031},{\"first_submission\":\"B\",\"second_submission\":\"D\",\"similarity\":0.8070175438596491},{\"first_submission\":\"E\",\"second_submission\":\"A\",\"similarity\":0.0},{\"first_submission\":\"E\",\"second_submission\":\"D\",\"similarity\":0.0},{\"first_submission\":\"E\",\"second_submission\":\"B\",\"similarity\":0.0},{\"first_submission\":\"E\",\"second_submission\":\"C\",\"similarity\":0.0}],\"description\":\"Maximum of both program coverages. This ranking is especially useful if the programs are very different in size. This can happen when dead code was inserted to disguise the origin of the plagiarized program.\"}],\"clusters\":[]}"
        },
        "submissions": {
            "A": new Map<string, string>().set("A\\GSTiling.java","1").set("A\\Match.java","2").set("A\\Matches.java","3").set("A\\Structure.java","4").set("A\\Submission.java","5").set("A\\Table.java","6").set("A\\Token.java","7").set("A\\TokenConstants.java","8"),
            "B": new Map<string, string>().set("B\\Table.java","1").set("B\\Token.java","2").set("B\\TokenConstants.java","3"),
            "C": new Map<string, string>().set("C\\GSTiling.java","1").set("C\\Match.java","2").set("C\\Matches.java","3").set("C\\Structure.java","4").set("C\\Submission.java","5").set("C\\Table.java","6").set("C\\Token.java","7").set("C\\TokenConstants.java","8"),
            "D": new Map<string, string>().set("D\\Match.java","2").set("D\\Matches.java","3").set("D\\Structure.java","4").set("D\\Submission.java","5").set("D\\Table.java","6").set("D\\Token.java","7").set("D\\TokenConstants.java","8"),
            "E": new Map<string, string>().set("E\\WhatAmIDoingHere.java","1"),
        },
        "local": false,
        "zip": true,
        "single": false,
        "fileString": "",
        "fileIdToDisplayName": new Map().set("A","A").set("B","B").set("C","C").set("D","D").set("E","E")
});
})
const json = {
    "id1": "B",
    "id2": "A",
    "similarity": 0.2457689477557027,
    "matches": [
        {
            "file1": "B\\Table.java",
            "file2": "A\\Table.java",
            "start1": 5,
            "end1": 60,
            "start2": 5,
            "end2": 60,
            "tokens": 92
        },
        {
            "file1": "B\\Token.java",
            "file2": "A\\Token.java",
            "start1": 1,
            "end1": 47,
            "start2": 1,
            "end2": 47,
            "tokens": 75
        }
    ]
};

test("CF", () => {
    const comparison = ComparisonFactory.getComparison(json);
    expect(comparison.firstSubmissionId).toMatch("B");
    expect(comparison.secondSubmissionId).toMatch("A");
    expect(comparison.similarity).toBe(0.2457689477557027);
    expect(comparison.filesOfFirstSubmission.size).toBe(3);
    expect(comparison.filesOfSecondSubmission.size).toBe(8);
    expect(comparison.colors.length).toBe(2);
    expect(comparison.allMatches.length).toBe(2);
})