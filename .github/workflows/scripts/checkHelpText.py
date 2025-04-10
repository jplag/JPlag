import subprocess

# Extract all triple fenced blocks from the text and returns them without the ```
# Each section is a list of lines, and the list of sections is a list of lists
def getAllCodeSections(text):
    codeSections = []
    currentSection = None

    for line in text:
        if line.startswith("```"):
            if currentSection is not None:
                codeSections.append(currentSection)
                currentSection = None
            else:
                currentSection = []
        else:
            if currentSection is not None:
                currentSection.append(line)

    if currentSection is not None:
        codeSections.append(currentSection)

    return codeSections

# Reads a file as a list of lines
def getFilesText(file):
    f = open(file, 'r', encoding='utf-8')
    return f.read().split('\n')

# Filters the sections to find the one that starts with "Parameter descriptions:", which is the one indicating the CLI text
def getCliTextCodeSection(sections):
    for section in sections:
        if section[0].startswith("Parameter descriptions:"):
            return section
    return None

# Removes empty lines at the end of the list
def removeEmptyLinesAtEndOfDocument(text):
    while len(text) > 0 and text[-1].strip() == "":
        text.pop()
    return text

# Stores the offset of the CLI text in the original file
lineOffset = {}

# Reads the file and extracts the CLI text from it
def getCliTextFromFile(file):
    text = getFilesText(file)
    codeSections = getAllCodeSections(text)
    cliTextSection = getCliTextCodeSection(codeSections)
    removedEmptyLines = removeEmptyLinesAtEndOfDocument(cliTextSection)
    lineOffset[file] = text.index(cliTextSection[0]) + 1 if cliTextSection else 0
    return removedEmptyLines

class Error:
    def __init__(self, fileName, lineNumber, message):
        self.fileName = fileName
        self.lineNumber = lineNumber
        self.message = message

    def __str__(self):
        return f"{self.fileName.split('/')[-1]}:{self.lineNumber+lineOffset[self.fileName]}: {self.message}"

errors = []

helpTextLines = subprocess.run(["java", "-jar", "jplag.jar", "-h"], capture_output=True, text=True).stdout.split('\n')[5::]
helpTextLines = removeEmptyLinesAtEndOfDocument(helpTextLines)

filesToCheck = ["../../../README.md", "../../../docs/1.-How-to-Use-JPlag.md"]

# Verifies that the CLI text from the jar matches the one in the md file
def checkCliTextAgainstHelpText(mdText, fileName):
    minLines = min(len(mdText), len(helpTextLines))
    if (len(mdText) != len(helpTextLines)):
        errors.append(Error(fileName, minLines, f"The number of lines in the CLI text from the jar({len(helpTextLines)}) does not match the md text({len(mdText)})."))
    for i in range(minLines):
        if (mdText[i] != helpTextLines[i]):
            helpSpace = helpTextLines[i].replace(" ", '\u23B5')
            mdSpace = mdText[i].replace(" ", '\u23B5')
            errors.append(Error(fileName, i, f"Does not match the CLI text from the jar. Expected: '{helpSpace}', Found: '{mdSpace}'."))

for file in filesToCheck:
    cliText = getCliTextFromFile(file)

    if cliText is None:
        errors.append(Error(fileName, 0, "No CLI text found in the file."))
    else:
        checkCliTextAgainstHelpText(cliText, file)

if len(errors) > 0:
    print("Errors found:")
    for error in errors:
        print(error)
    exit(1)
else:
    print("No errors found.")
    exit(0)