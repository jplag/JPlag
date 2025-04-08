import subprocess


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

def getFilesText(file):
    f = open(file, 'r', encoding='utf-8')
    return f.read().split('\n')

def getCliTextCodeSection(sections):
    for section in sections:
        if section[0].startswith("Parameter descriptions:"):
            return section
    return None

def removeEmptyLinesAtEndOfDocument(text):
    while len(text) > 0 and text[-1].strip() == "":
        text.pop()
    return text

def getCliTextFromFile(file):
    text = getFilesText(file)
    codeSections = getAllCodeSections(text)
    cliTextSection = getCliTextCodeSection(codeSections)
    removedEmptyLines = removeEmptyLinesAtEndOfDocument(cliTextSection)
    return removedEmptyLines

class Error:
    def __init__(self, fileName, lineNumber, message):
        self.fileName = fileName.split('/')[-1]
        self.lineNumber = lineNumber
        self.message = message

    def __str__(self):
        return f"{self.fileName}:{self.lineNumber}: {self.message}"

errors = []

helpTextLines = subprocess.run(["java", "-jar", "jplag.jar", "-h"], capture_output=True, text=True).stdout.split('\n')[5::]
helpTextLines = removeEmptyLinesAtEndOfDocument(helpTextLines)

filesToCheck = ["../../../README.md", "../../../docs/1.-How-to-Use-JPlag.md"]

def checkCliTextAgainstHelpText(mdText, fileName):
    minLines = min(len(mdText), len(helpTextLines))
    if (len(mdText) != len(helpTextLines)):
        errors.append(Error(fileName, minLines, f"The number of lines in the CLI text from the jar({len(helpTextLines)}) does not match the md text({len(mdText)})."))
    for i in range(minLines):
        if (mdText[i] != helpTextLines[i]):
            errors.append(Error(fileName, i + 1, f"Line {i + 1} does not match the CLI text from the jar. Expected: '{helpTextLines[i]}', Found: '{mdText[i]}'."))

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