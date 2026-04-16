#!/bin/bash
# Complete Bash test file covering all token types for JPlag

# Variable assignment
greeting="Hello, World!"
count=42
test=(1 2 3 4 5)
length=${#test}

# Local variable (inside function)
# Export variable
export PATH="/usr/local/bin:$PATH"

# Readonly variable
readonly MAX_RETRIES=3

# Declare variable
declare -i number=10

# Function definition with 'function' keyword
function say_hello() {
    local name="$1"
    echo "$name says $greeting"
    return 0
}

# Function definition without 'function' keyword
greet() {
    local target="$1"
    echo "Greetings, $target!"
}

# If/elif/else
if [ "$count" -gt 50 ]; then
    echo "Count is greater than 50"
elif [ "$count" -gt 25 ]; then
    echo "Count is greater than 25"
else
    echo "Count is 25 or less"
fi

# For loop with list
for item in apple banana cherry; do
    echo "Fruit: $item"
done

# For loop C-style
for ((i = 0; i < 10; i++)); do
    echo "Index: $i"
done

# While loop
counter=0
while [ "$counter" -lt 5 ]; do
    echo "Counter: $counter"
    counter=$((counter + 1))
done

# Until loop
value=0
until [ "$value" -ge 3 ]; do
    echo "Value: $value"
    value=$((value + 1))
done

# Case statement
fruit="apple"
case "$fruit" in
    apple)
        echo "It's an apple"
        ;;
    banana)
        echo "It's a banana"
        ;;
    *)
        echo "Unknown fruit"
        ;;
esac

# Select statement
select option in "Option1" "Option2" "Quit"; do
    case "$option" in
        Quit)
            break
            ;;
        *)
            echo "You chose: $option"
            continue
            ;;
    esac
done

# Pipeline
ls -la | grep ".sh" | wc -l

# Logical AND and OR
test -f "complete.sh" && echo "File exists" || echo "File not found"

# Redirection
echo "log entry" >> output.log
cat < input.txt > output.txt 2>&1

# Subshell
(cd /tmp && ls -la)

# Brace group
{ echo "grouped"; echo "commands"; }

# Arithmetic expression
result=$((count + number))
((count++))

# Test expression with double brackets
if [[ "$greeting" == *"World"* ]]; then
    echo "Contains World"
fi

# Command substitution with $()
current_date=$(date +%Y-%m-%d)

# Command substitution with backticks
hostname=`hostname`

# Special variables and escaped characters
status=$?
arg_count=$#
find /tmp -name "*.log" -exec rm {} \;

# Heredoc
cat <<EOF
This is heredoc content
with multiple lines
EOF

# Nested function call
say_hello "Alice"
greet "Bob"
