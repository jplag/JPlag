import re

CORE_PACKAGE = '../../../core/src/main/java/de/jplag'
REPORTING_PACKAGE = f"{CORE_PACKAGE}/reporting/reportobject/model"
REPORT_VIEWER_MODELS = f"../../../report-viewer/model/src"
REPORT_VIEWER_FACTORIES = f"../../../report-viewer/parser/src/factories"

cli_options = ({ 'file_path': f"{CORE_PACKAGE}/options/JPlagOptions.java", 'record_name': 'JPlagOptions' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/CliOptions.ts", 'interface_name': 'AbstractOptions' })
merging_options = ({ 'file_path': f"{CORE_PACKAGE}/merging/MergingOptions.java", 'record_name': 'MergingOptions' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/CliOptions.ts", 'interface_name': 'CliMergingOptions' })
clustering_options = ({ 'file_path': f"{CORE_PACKAGE}/clustering/ClusteringOptions.java", 'record_name': 'ClusteringOptions' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/CliOptions.ts", 'interface_name': 'CliClusterOptions' })

comparison = ({ 'file_path': f"{REPORTING_PACKAGE}/ComparisonReport.java", 'record_name': 'ComparisonReport' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/ComparisonFactory.ts", 'interface_name': 'ReportFormatComparison' })
match = ({ 'file_path': f"{REPORTING_PACKAGE}/Match.java", 'record_name': 'Match' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/Match.ts", 'interface_name': 'ReportFormatMatch' })
code_position = ({ 'file_path': f"{REPORTING_PACKAGE}/CodePosition.java", 'record_name': 'CodePosition' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/Match.ts", 'interface_name': 'CodePosition' })

basecode_match = ({ 'file_path': f"{REPORTING_PACKAGE}/BaseCodeMatch.java", 'record_name': 'BaseCodeMatch' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/BaseCodeReportFactory.ts", 'interface_name': 'ReportFormatBaseCodeMatch' })

submission_file_index = ({ 'file_path': f"{REPORTING_PACKAGE}/SubmissionFileIndex.java", 'record_name': 'SubmissionFileIndex' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/ComparisonFactory.ts", 'interface_name': 'ReportFormatSubmissionFileIndex' })
submission_file = ({ 'file_path': f"{REPORTING_PACKAGE}/SubmissionFile.java", 'record_name': 'SubmissionFile' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/ComparisonFactory.ts", 'interface_name': 'ReportSubmissionFile' })

run_information = ({ 'file_path': f"{REPORTING_PACKAGE}/RunInformation.java", 'record_name': 'RunInformation' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/RunInformationFactory.ts", 'interface_name': 'ReportFormatRunInformation' })
failed_submission = ({ 'file_path': f"{REPORTING_PACKAGE}/FailedSubmission.java", 'record_name': 'FailedSubmission' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/RunInformation.ts", 'interface_name': 'FailedSubmission' })
version = ({ 'file_path': f"{REPORTING_PACKAGE}/Version.java", 'record_name': 'Version' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/RunInformationFactory.ts", 'interface_name': 'ReportFormatVersion' })

submission_mappings = ({ 'file_path': f"{REPORTING_PACKAGE}/SubmissionMappings.java", 'record_name': 'SubmissionMappings' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/SubmissionMappingsFactory.ts", 'interface_name': 'ReportFormatSubmissionMappings' })

top_comparison = ({ 'file_path': f"{REPORTING_PACKAGE}/TopComparison.java", 'record_name': 'TopComparison' }, { 'file_path': f"{REPORT_VIEWER_FACTORIES}/TopComparisonFactory.ts", 'interface_name': 'ReportFormatTopComparison' })

cluster = ({ 'file_path': f"{REPORTING_PACKAGE}/Cluster.java", 'record_name': 'Cluster' }, { 'file_path': f"{REPORT_VIEWER_MODELS}/Cluster.ts", 'interface_name': 'ReportFormatCluster' })

# we do not check the distribution as it has own record and is just a map directly written to a json file

file_list = [cli_options, merging_options, clustering_options, comparison, match, code_position, basecode_match, submission_file_index, submission_file, run_information, failed_submission, version, submission_mappings, top_comparison, cluster]

type_matches = {
    # Basic map from Java types to TypeScript types
    'int': 'number',
    'Integer': 'number',
    'double': 'number',
    'Double': 'number',
    'long': 'number',
    'String': 'string',
    'boolean': 'boolean',
    'File': 'string',
    'SubmissionState': 'SubmissionState',
    'SimilarityMetric': 'MetricJsonIdentifier',
    'Preprocessing': 'string',
    'ClusteringAlgorithm': 'string',
    'InterClusterSimilarity': 'string',
}
# add all record interface matches to the typeMatches list
for java_file, typescript_file in file_list:
    record_name = java_file['record_name']
    interface_name = typescript_file['interface_name']
    type_matches[record_name] = interface_name


def read_file_contents(file_path):
    with open(file_path, 'r') as file:
        return file.read()


def get_content_between_brackets(text, open_bracket='{', close_bracket='}'):
    start = text.find(open_bracket)
    end = start + 1
    bracket_count = 1
    while end < len(text) and bracket_count > 0:
        if text[end] == open_bracket:
            bracket_count += 1
        elif text[end] == close_bracket:
            bracket_count -= 1
        end += 1
    return text[start + 1:end - 1].strip() if bracket_count == 0 else ''

def get_text_after_keyword(text, keyword):
    start = text.find(keyword)
    if start == -1:
        return ''
    start += len(keyword)
    return text[start:].strip()

# Gets all variables from within the record body
# separates variables that are in the same line into multiple strings
# used to filter blank lines and comments
def extract_record_variables(text):
    variables = []
    current_variable = ''
    open_brackets = 0
    for char in text:
        if char == '(' or char == '{' or char == '[' or char == '<':
            open_brackets += 1
            current_variable += char
        elif char == ')' or char == '}' or char == ']' or char == '>':
            open_brackets -= 1
            current_variable += char
        elif char == ',' and open_brackets == 0:
            variables.append(current_variable.strip())
            current_variable = ''
        else:
            current_variable += char
    variables.append(current_variable.strip())  # Add the last variable
    return variables

# Transforms the text of a Java variable definition to the internal representation
def transform_java_variable(variable): 
    parts = variable.rsplit(' ', 1)
    if len(parts) < 2:
        return None
    type_name_and_prior = parts[-2].strip()
    type_name = extract_type_from_long(type_name_and_prior)
    variable_name = parts[-1].replace(';', '').strip()
    return (type_name, variable_name)

def extract_type_from_long(type_name_and_prior):
    open_brackets = 0
    for i in range(len(type_name_and_prior) - 1, -1, -1):
        if type_name_and_prior[i] == ')' or type_name_and_prior[i] == ']' or type_name_and_prior[i] == '>' or type_name_and_prior[i] == '}':
            open_brackets += 1
        elif type_name_and_prior[i] == '(' or type_name_and_prior[i] == '[' or type_name_and_prior[i] == '<' or type_name_and_prior[i] == '{':
            open_brackets -= 1
        elif type_name_and_prior[i] == ' ' and open_brackets == 0:
            return type_name_and_prior[i + 1:]
    return type_name_and_prior

# Extracts variables from a Java file with a record
def get_java_record_variables(text, record_name):
    complete_name = 'record ' + record_name
    if not complete_name in text:
        raise Exception(f"Could not find {complete_name}")
    record = get_text_after_keyword(text, complete_name)
    content_between_brackets = get_content_between_brackets(record, '(', ')')
    variables = extract_record_variables(content_between_brackets)
    return [transform_java_variable(var) for var in variables if transform_java_variable(var) is not None]

# Gets all lines from within the interface body that are variable declarations
# used to filter blank lines and comments
def extract_interface_variables(text):
    lines = text.splitlines()
    return [line.strip() for line in lines if not line.strip().startswith('/') and ':' in line]

# Transforms the text of a TypeScript variable definition to the internal representation
def transform_typescript_variable(variable):
    parts = variable.split(':')
    if len(parts) < 2:
        return None
    variable_name = parts[0].strip()
    type_name = parts[1].strip()
    return (type_name, variable_name)

# Extracts variables from a TypeScript file with an interface
def get_typescript_interface_variables(text, interface_name):
    complete_name = 'interface ' + interface_name
    if not complete_name in text: 
        raise Exception(f"Could not find {complete_name}")
    interface_text = get_text_after_keyword(text, complete_name)
    content_between_brackets = get_content_between_brackets(interface_text, '{', '}')
    variables = extract_interface_variables(content_between_brackets)
    return [transform_typescript_variable(var) for var in variables if transform_typescript_variable(var) is not None]


# Verifies that the java and ts type match
# if no data for check is found, it is reported as matching
def check_type_match(java_type, ts_type):
    # check simple types
    if java_type in type_matches:
        return ts_type == type_matches[java_type]
    
    # check list types
    java_list_check = re.search(r"^List<(.*)>$", java_type)
    if java_list_check:
        ts_list_check = re.search(r"^(.*)\[]$", ts_type)
        if ts_list_check:
            # check types of objects in list
            return check_type_match(java_list_check.group(1).strip(), ts_list_check.group(1).strip())
        else:
            # No list type is ts code
            return False
    
    # check set types
    java_set_check = re.search(r"^Set<(.*)>$", java_type)
    if java_set_check:
        ts_set_check = re.search(r"^(.*)\[]$", ts_type)
        if ts_set_check:
            # check types of objects in set
            return check_type_match(java_set_check.group(1).strip(), ts_set_check.group(1).strip())
        else:
            # No set type is ts code
            return False
        
    # check map types
    java_map_check = re.search(r"^Map<([^,]*),(.*)>$", java_type)
    if java_map_check:
        ts_record_check = re.search(r"^Record<([^,]*),(.*)>$", ts_type)
        if ts_record_check:
            # check types of key and value
            key_check = check_type_match(java_map_check.group(1).strip(), ts_record_check.group(1).strip())
            value_check = check_type_match(java_map_check.group(2).strip(), ts_record_check.group(2).strip())
            return key_check and value_check
        else:
            # is not a record in ts
            return False

    # If we have no defined way of checking types, we ignore it and assume its true, rather then giving a warning
    return True

# Strips a file path for pretty printing
def get_pretty_file_path(file_path):
    return file_path.split('/')[-1] if '/' in file_path else file_path

# transforms a file path to be based on the project root
def get_absolute_file_path(file_path):
    parts = file_path.split('/')
    return '/'.join(filter(lambda x: x != '.' and x != '..' and x != '', parts))

# Warning for mismatching type
class Warning:
    def __init__(self, java_variable, java_file_path, java_line_number, typescript_variable, typescript_file_path, typescript_line_number):
        self.java_variable = java_variable
        self.java_file_path = java_file_path
        self.java_line_number = java_line_number
        self.typescript_variable = typescript_variable
        self.typescript_file_path = typescript_file_path
        self.typescript_line_number = typescript_line_number
    
    def is_error(self):
        return False
    def is_warning(self):
        return True
    def actions_print(self):
        return [
            f"::warning file={get_absolute_file_path(self.java_file_path)},line={self.java_line_number},title=Variable types do not match::Type of Java variable '{self.java_variable[1]}' ({self.java_variable[0]}) does not match TypeScript variable '{self.typescript_variable[1]}' ({self.typescript_variable[0]}) in {get_absolute_file_path(self.typescript_file_path)} at line {self.typescript_line_number}.",
            f"::warning file={get_absolute_file_path(self.typescript_file_path)},line={self.typescript_line_number},title=Variable types do not match::Type of TypeScript variable '{self.typescript_variable[1]}' ({self.typescript_variable[0]}) does not match Java variable '{self.java_variable[1]}' ({self.java_variable[0]}) in {get_absolute_file_path(self.java_file_path)} at line {self.java_line_number}."
        ]

# Base Error
class Error:
    def __init__(self, variable, file_path, line_number, other_file_path, other_line_number):
        self.variable = variable
        self.file_path = file_path
        self.line_number = line_number
        self.other_file_path = other_file_path
        self.other_line_number = other_line_number
    def is_error(self):
        return True
    def is_warning(self):
        return False
# Error for: Variable from a TypeScript interface not found in Java record
class TsError(Error):
    def actions_print(self):
        return [
            f"::error file={get_absolute_file_path(self.file_path)},line={self.line_number},title=Variable not found in Java equivalent::TypeScript variable '{self.variable[1]}' ({self.variable[0]}) does not match any Java variable in {get_absolute_file_path(self.other_file_path)}.",
            f"::error file={get_absolute_file_path(self.other_file_path)},line={self.other_line_number},title=Missing variable from TypeScript equivalent::Java record should have a variable equivalent to '{self.variable[1]}' ({self.variable[0]}) from {get_absolute_file_path(self.file_path)} at line {self.line_number}."
        ]
# Error for: Variable from Java record not found in TypeScript interface
class JavaError(Error):
    def actions_print(self):
        return [
            f"::error file={get_absolute_file_path(self.file_path)},line={self.line_number},title=Variable not found in TypeScript equivalent::Java variable '{self.variable[1]}' ({self.variable[0]}) does not match any TypeScript variable in {get_absolute_file_path(self.other_file_path)}.",
            f"::error file={get_absolute_file_path(self.other_file_path)},line={self.other_line_number},title=Missing variable from Java equivalent::TypeScript interface should have a variable equivalent to '{self.variable[1]}' ({self.variable[0]}) from {get_absolute_file_path(self.file_path)} at line {self.line_number}."
        ]

# Finds the line number of subtext
def find_line_number(lines, subtext):
    for i, line in enumerate(lines):
        if subtext in line:
            return i + 1  # Return 1-based line number
    return -1  # Not found

# Reads files and compares the variables
def check_variable_match(java_file, typescript_file):
    java_text = read_file_contents(java_file['file_path'])
    java_variables = get_java_record_variables(java_text, java_file['record_name'])
    java_lines = java_text.splitlines()

    typescript_text = read_file_contents(typescript_file['file_path'])
    typescript_variables = get_typescript_interface_variables(typescript_text, typescript_file['interface_name'])
    typescript_lines = typescript_text.splitlines()

    annotations = []

    # Check if all Java variables are in interface
    for java_var in java_variables:
        java_type, java_name = java_var
        matched = False
        for ts_var in typescript_variables:
            ts_type, ts_name = ts_var
            if java_name == ts_name:
                matched = True
                break
        if not matched:
            annotations.append(JavaError(
                variable=java_var,
                file_path=java_file['file_path'],
                line_number=find_line_number(java_lines, f"{java_type} {java_name}"),
                other_file_path=typescript_file['file_path'],
                other_line_number=find_line_number(typescript_lines, f"interface {typescript_file['interface_name']}")
            ))

    # Check if all TypeScript variables are in record
    for ts_var in typescript_variables:
        ts_type, ts_name = ts_var
        matched = False
        for java_var in java_variables:
            java_type, java_name = java_var
            if ts_name == java_name:
                matched = True
                break
        if not matched:
            annotations.append(TsError(
                variable=ts_var,
                file_path=typescript_file['file_path'],
                line_number=find_line_number(typescript_lines, f"{ts_name}: {ts_type}"),
                other_file_path=java_file['file_path'],
                other_line_number=find_line_number(java_lines, f"record {java_file['record_name']}")
            ))

    # check for all Java variables that the type matches the TypeScript type if it is comparable
    for java_var in java_variables:
        java_type, java_name = java_var
        for ts_var in typescript_variables:
            ts_type, ts_name = ts_var
            if java_name == ts_name:
                if not check_type_match(java_type, ts_type):
                    annotations.append(Warning(
                        java_variable=java_var,
                        java_file_path=java_file['file_path'],
                        java_line_number=find_line_number(java_lines, f"{java_type} {java_name}"),
                        typescript_variable=ts_var,
                        typescript_file_path=typescript_file['file_path'],
                        typescript_line_number=find_line_number(typescript_lines, f"{ts_name}: {ts_type}")
                    ))
                break
    return annotations

# Runs the comparison for the files and prints all errors and warnings
def run_for_pair(java_file, typescript_file):
    print(f"Checking variables in {get_pretty_file_path(java_file['file_path'])} and {get_pretty_file_path(typescript_file['file_path'])}")
    annotations = check_variable_match(java_file, typescript_file)
    if not annotations:
        print("No mismatches found.")
        print("")
        return False

    _errors = [a.actions_print() for a in annotations if a.is_error()]
    errors = [item for sub_list in _errors for item in sub_list]
    if errors:
        print(f"Found {len(errors)} errors:")
        for error in errors:
            print(error)
        print("") # Add a newline for better readability

    _warnings = [a.actions_print() for a in annotations if a.is_warning()]
    warnings = [item for sub_list in _warnings for item in sub_list]
    if warnings:
        print(f"Found {len(warnings)} warnings:")
        for warning in warnings:
            print(warning)
        print("")
    
    return len(errors) > 0

# Iteratively run comparison for all files
def run_for_all_pairs(pairs):
    found_errors = False
    for java_file, typescript_file in pairs:
        found_errors |= run_for_pair(java_file, typescript_file)
    if not found_errors:
        exit(0)
    else:
        exit(1)




run_for_all_pairs(file_list)
