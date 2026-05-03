#!/bin/bash
# Complete Bash coverage fixture for the JPlag Bash language module.

# Global assignments and parameter expansions.
greeting="Hello, World"
count=3
accumulator=7
accumulator+=5
list=(one two three)
list_len=${#list}
home_path=$HOME/.local
pid=$$
last_status=$?
arg_count=$#
all_args=$@
all_args_string=$*
shell_flags=$-
last_bg=$!

# Function definitions in all supported forms.
function with_keyword_and_parens() {
    local name="$1"
    echo "hello ${name}"
    return 0
}

function with_keyword_no_parens {
    local alias_name=${1:-anon}
    echo "alias ${alias_name}"
}

without_keyword() {
    local value="$1"
    echo "value=${value}"
}

# Builtin assignment forms.
declare -i declared_number=10
readonly MAX_RETRIES=4
export PATH="/usr/local/bin:${PATH}"

# If / elif / else with both [ ] and [[ ]].
if [ "$count" -gt 10 ]; then
    echo "gt10"
elif [[ "$greeting" == *World* ]]; then
    echo "world"
else
    echo "fallback"
fi

# For loops: explicit list, implicit "$@", and C-style arithmetic for-loop.
for item in alpha beta gamma; do
    echo "item:${item}"
done

for item; do
    echo "arg:${item}"
done

for ((i = 0; i < 2; i++)); do
    echo "i=${i}"
done

# While / until loops.
n=0
while [ "$n" -lt 2 ]; do
    n=$((n + 1))
done

m=0
until [ "$m" -ge 2 ]; do
    m=$((m + 1))
done

# Case items with all supported item terminators (;;, ;&, ;;&).
mode="start"
case "$mode" in
    start)
        echo "start"
        ;;
    next)
        echo "next"
        ;&
    fallback)
        echo "fallback"
        ;;&
    *)
        echo "default"
        ;;
esac

# Select with break / continue control-flow commands.
select option in One Two Stop; do
    case "$option" in
        Stop)
            break
            ;;
        One)
            continue
            ;;
        *)
            echo "chosen:${option}"
            break
            ;;
    esac
done

# Command and argument coverage plus logical terminators.
printf "%s\n" "a" | grep "a" | wc -l
test -f "complete.sh" && echo "exists" || echo "missing"
echo foo\ bar

# Subshell and brace group.
(cd /tmp && pwd)
{ echo "grouped"; echo "commands"; }

# Arithmetic and test expressions as compound commands.
((count++))
result=$((count + declared_number))

if [[ "$result" -ge 10 ]]; then
    echo "arith-ok"
fi

# Command substitution in both forms.
today=$(date +%F)
host=`hostname`

# Here-doc and here-string.
cat <<EOF_BLOCK
line 1
line 2
EOF_BLOCK

cat <<-'EOF_TABS'
	indented
EOF_TABS

grep "World" <<< "$greeting"

# Redirection coverage.
echo "append" >> append.log
echo "overwrite" > out.log
cat < in.log
echo "stderr" >&2
cat <&0
cat <> duplex.log
echo "force" >| force.log

# Special words that are tokenized as words in this grammar.
eval "echo eval"
exec echo "exec"
source ./complete.sh
trap 'echo trapped' EXIT
unset old_value

# Nested calls.
with_keyword_and_parens "Alice"
with_keyword_no_parens "Bob"
without_keyword "Charlie"
