def readFileContents(file_path):
    with open(file_path, 'r') as file:
        return file.read()


def getContentBetweenBrackets(text, open_bracket='{', close_bracket='}'):
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

def getTextAfterKeyword(text, keyword):
    start = text.find(keyword)
    if start == -1:
        return ''
    start += len(keyword)
    return text[start:].strip()

# Gets all variables from within the record body
# separates variables that are in the same line into multiple strings
# used to filter blank lines and comments
def extractRecordVariables(text):
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
def transformJavaVariable(variable): 
    parts = variable.split(' ')
    if len(parts) < 2:
        return None
    type_name = parts[-2].strip()
    variable_name = parts[-1].replace(';', '').strip()
    return (type_name, variable_name)

# Extracts variables from a Java file with a record
def getJavaRecordVariables(text, record_name):
    complete_name = 'record ' + record_name
    if not complete_name in text:
        raise Exception(f"Could not find {complete_name}")
    record = getTextAfterKeyword(text, complete_name)
    content_between_brackets = getContentBetweenBrackets(record, '(', ')')
    variables = extractRecordVariables(content_between_brackets)
    return [transformJavaVariable(var) for var in variables if transformJavaVariable(var) is not None]

# Gets all lines from within the interface body that are variable declarations
# used to filter blank lines and comments
def extractInterfaceVariables(text):
    lines = text.splitlines()
    return [line.strip() for line in lines if not line.strip().startswith('/') and ':' in line]

# Transforms the text of a TypeScript variable definition to the internal representation
def transformTypescriptVariable(variable):
    parts = variable.split(':')
    if len(parts) < 2:
        return None
    variable_name = parts[0].strip()
    type_name = parts[1].strip().replace(';', '').replace(',', '')
    return (type_name, variable_name)

# Extracts variables from a TypeScript file with an interface
def getTypescriptInterfaceVariables(text, interface_name):
    complete_name = 'interface ' + interface_name
    if not complete_name in text: 
        raise Exception(f"Could not find {complete_name}")
    interface_text = getTextAfterKeyword(text, complete_name)
    content_between_brackets = getContentBetweenBrackets(interface_text, '{', '}')
    variables = extractInterfaceVariables(content_between_brackets)
    return [transformTypescriptVariable(var) for var in variables if transformTypescriptVariable(var) is not None]

# Basic map from Java types to TypeScript types
typeMatches = {
    'int': 'number',
    'Integer': 'number',
    'double': 'number',
    'String': 'string',
    'boolean': 'boolean',
    'Set<File>': 'string[]',
    'List<String>': 'string[]',
}

# Strips a file path for pretty printing
def getPrettyFilePath(file_path):
    return file_path.split('/')[-1] if '/' in file_path else file_path

# transforms a file path to be based on the project root
def getAbsoluteFilePath(file_path):
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
            f"::warning file={getAbsoluteFilePath(self.java_file_path)},line={self.java_line_number},title=Variable types do not match::Type of Java variable '{self.java_variable[1]}' ({self.java_variable[0]}) does not match TypeScript variable '{self.typescript_variable[1]}' ({self.typescript_variable[0]}) in {getAbsoluteFilePath(self.typescript_file_path)} at line {self.typescript_line_number}.",
            f"::warning file={getAbsoluteFilePath(self.typescript_file_path)},line={self.typescript_line_number},tile=Variable types do not match::Type of TypeScript variable '{self.typescript_variable[1]}' ({self.typescript_variable[0]}) does not match Java variable '{self.java_variable[1]}' ({self.java_variable[0]}) in {getAbsoluteFilePath(self.java_file_path)} at line {self.java_line_number}."
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
            f"::error file={getAbsoluteFilePath(self.file_path)},line={self.line_number},title=Variable not found in Java equivalent::TypeScript variable '{self.variable[1]}' ({self.variable[0]}) does not match any Java variable in {getAbsoluteFilePath(self.other_file_path)}.",
            f"::error file={getAbsoluteFilePath(self.other_file_path)},line={self.other_line_number},title=Missing variable from TypeScript equivalent::Java record should have a variable equivalent to '{self.variable[1]}' ({self.variable[0]}) from {getAbsoluteFilePath(self.file_path)} at line {self.line_number}."
        ]
# Error for: Variable from Java record not found in TypeScript interface
class JavaError(Error):
    def actions_print(self):
        return [
            f"::error file={getAbsoluteFilePath(self.file_path)},line={self.line_number},title=Variable not found in TypeScript equivalent::Java variable '{self.variable[1]}' ({self.variable[0]}) does not match any TypeScript variable in {getAbsoluteFilePath(self.other_file_path)}.",
            f"::error file={getAbsoluteFilePath(self.other_file_path)},line={self.other_line_number},title=Missing variable from Java equivalent::TypeScript interface should have a variable equivalent to '{self.variable[1]}' ({self.variable[0]}) from {getAbsoluteFilePath(self.file_path)} at line {self.line_number}."
        ]

# Finds the line number of subtext
def findLineNumber(lines, subtext):
    for i, line in enumerate(lines):
        if subtext in line:
            return i + 1  # Return 1-based line number
    return -1  # Not found

# Reads files and compares the variables
def checkVariableMatch(java_file, typescript_file):
    java_text = readFileContents(java_file['file_path'])
    java_variables = getJavaRecordVariables(java_text, java_file['record_name'])
    java_lines = java_text.splitlines()

    typescript_text = readFileContents(typescript_file['file_path'])
    typescript_variables = getTypescriptInterfaceVariables(typescript_text, typescript_file['interface_name'])
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
                line_number=findLineNumber(java_lines, f"{java_type} {java_name}"),
                other_file_path=typescript_file['file_path'],
                other_line_number=findLineNumber(typescript_lines, f"interface {typescript_file['interface_name']}")
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
                line_number=findLineNumber(typescript_lines, f"{ts_name}: {ts_type}"),
                other_file_path=java_file['file_path'],
                other_line_number=findLineNumber(java_lines, f"record {java_file['record_name']}")
            ))

    # check for all Java variables that the type matches the TypeScript type if it is comparable
    for java_var in java_variables:
        java_type, java_name = java_var
        for ts_var in typescript_variables:
            ts_type, ts_name = ts_var
            if java_name == ts_name:
                if java_type not in typeMatches:
                    continue
                if typeMatches.get(java_type) != ts_type:
                    annotations.append(Warning(
                        java_variable=java_var,
                        java_file_path=java_file['file_path'],
                        java_line_number=findLineNumber(java_lines, f"{java_type} {java_name}"),
                        typescript_variable=ts_var,
                        typescript_file_path=typescript_file['file_path'],
                        typescript_line_number=findLineNumber(typescript_lines, f"{ts_name}: {ts_type}")
                    ))
                break
    return annotations

# Runs the comparison for the files and prints all errors and warnings
def runForPair(java_file, typescript_file):
    print(f"Checking variables in {getPrettyFilePath(java_file['file_path'])} and {getPrettyFilePath(typescript_file['file_path'])}")
    annotations = checkVariableMatch(java_file, typescript_file)
    if not annotations:
        print("No mismatches found.")
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
    
    return len(errors) > 0

# Iteratively run comparison for all files
def runForAllPairs(pairs):
    found_errors = False
    for java_file, typescript_file in pairs:
        found_errors |= runForPair(java_file, typescript_file)
    if not found_errors:
        exit(0)
    else:
        exit(1)

cli_options = ({ 'file_path': '../../../core/src/main/java/de/jplag/options/JPlagOptions.java', 'record_name': 'JPlagOptions' }, { 'file_path': '../../../report-viewer/src/model/CliOptions.ts', 'interface_name': 'AbstractOptions' })
merging_options = ({ 'file_path': '../../../core/src/main/java/de/jplag/merging/MergingOptions.java', 'record_name': 'MergingOptions' }, { 'file_path': '../../../report-viewer/src/model/CliOptions.ts', 'interface_name': 'CliMergingOptions' })
clustering_options = ({ 'file_path': '../../../core/src/main/java/de/jplag/clustering/ClusteringOptions.java', 'record_name': 'ClusteringOptions' }, { 'file_path': '../../../report-viewer/src/model/CliOptions.ts', 'interface_name': 'CliClusterOptions' })

comparison = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/ComparisonReport.java', 'record_name': 'ComparisonReport' }, { 'file_path': '../../../report-viewer/src/model/factories/ComparisonFactory.ts', 'interface_name': 'ReportFormatComparison' })
match = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/Match.java', 'record_name': 'Match' }, { 'file_path': '../../../report-viewer/src/model/Match.ts', 'interface_name': 'Match' })
code_position = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/CodePosition.java', 'record_name': 'CodePosition' }, { 'file_path': '../../../report-viewer/src/model/Match.ts', 'interface_name': 'CodePosition' })

basecode_match = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/BaseCodeMatch.java', 'record_name': 'BaseCodeMatch' }, { 'file_path': '../../../report-viewer/src/model/factories/BaseCodeReportFactory.ts', 'interface_name': 'ReportFormatBaseCodeMatch' })

submission_file_index = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/SubmissionFileIndex.java', 'record_name': 'SubmissionFileIndex' }, { 'file_path': '../../../report-viewer/src/model/factories/ComparisonFactory.ts', 'interface_name': 'ReportFormatSubmmisionFileIndex' })

run_information = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/RunInformation.java', 'record_name': 'RunInformation' }, { 'file_path': '../../../report-viewer/src/model/factories/RunInformationFactory.ts', 'interface_name': 'ReportFormatRunInformation' })
failed_submission = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/FailedSubmission.java', 'record_name': 'FailedSubmission' }, { 'file_path': '../../../report-viewer/src/model/RunInformation.ts', 'interface_name': 'FailedSubmission' })
version = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/Version.java', 'record_name': 'Version' }, { 'file_path': '../../../report-viewer/src/model/factories/RunInformationFactory.ts', 'interface_name': 'ReportFormatVersion' })

submission_mappings = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/SubmissionMappings.java', 'record_name': 'SubmissionMappings' }, { 'file_path': '../../../report-viewer/src/model/factories/SubmissionMappingsFactory.ts', 'interface_name': 'ReportFormatSubmissionMappings' })

top_comparisons = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/TopComparison.java', 'record_name': 'TopComparison' }, { 'file_path': '../../../report-viewer/src/model/factories/TopComparisonFactory.ts', 'interface_name': 'ReportFormatTopComparison' })

cluster = ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/Cluster.java', 'record_name': 'Cluster' }, { 'file_path': '../../../report-viewer/src/model/Cluster.ts', 'interface_name': 'ReportFormatCluster' })

# ({ 'file_path': '../../../core/src/main/java/de/jplag/reporting/reportobject/model/', 'record_name': '' }, { 'file_path': '../../../report-viewer/src/model/factories/', 'interface_name': '' })

# we do not check the distribution as it has own record and is just a map directly written to a json file


runForAllPairs([cli_options,merging_options, clustering_options, comparison, match, code_position, basecode_match, submission_file_index, run_information, failed_submission, version, submission_mappings, top_comparisons, cluster])
