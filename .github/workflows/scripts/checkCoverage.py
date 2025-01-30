import os
import xml.etree.ElementTree as ET

def get_all_pom_files():
    pom_files = []
    for root, dirs, files in os.walk("../../.."):
        for file in files:
            if file == "pom.xml":
                pom_files.append(os.path.join(root, file))
    return pom_files

# get content from a file as a string
def get_file_content(file):
    with open(file, "r") as f:
        return f.read()

# extract xml field artifact id from string
def extract_artifact_id(xml):
    root = ET.fromstring(xml)
    return root.find("{http://maven.apache.org/POM/4.0.0}artifactId").text

excluded_artifacts = ["coverage-report", "aggregator", "languages"]
artifact_ids = [extract_artifact_id(get_file_content(file)) for file in get_all_pom_files()]
print("All artifacts: " + str(artifact_ids))
filtered_artifact_ids = [artifact_id for artifact_id in artifact_ids if artifact_id not in excluded_artifacts]

coverage_report_pom = ""
with open("../../../coverage-report/pom.xml", "r") as f:
    coverage_report_pom = f.read()
xml = ET.fromstring(coverage_report_pom)
coverage_report_artifacts = [dependency.find("{http://maven.apache.org/POM/4.0.0}artifactId").text for dependency in xml.find("{http://maven.apache.org/POM/4.0.0}dependencies").findall("{http://maven.apache.org/POM/4.0.0}dependency")]
print("Coverage report artifacts: " + str(coverage_report_artifacts))

only_in_coverage_report = [artifact_id for artifact_id in coverage_report_artifacts if artifact_id not in filtered_artifact_ids]
print("Only in coverage report: " + str(only_in_coverage_report))
not_in_coverage_report = [artifact_id for artifact_id in filtered_artifact_ids if artifact_id not in coverage_report_artifacts]
print("Not in coverage report: " + str(not_in_coverage_report))

if len(not_in_coverage_report) > 0:
    raise Exception("Some artifacts are not in the coverage report: " + str(not_in_coverage_report))
if len(only_in_coverage_report) > 0:
    raise Exception("Some artifacts are only in the coverage report: " + str(only_in_coverage_report))