import requests
import re
import sys

headers = {"Authorization": f"Bearer {sys.argv[1]}"}

owner = "jplag"
repo = "JPlag"
pr_number = int(sys.argv[2])

query = f"""
{{
  repository(owner: "{owner}", name: "{repo}") {{
    pullRequest(number: {pr_number}) {{
      merged
      baseRefName
      body
      closingIssuesReferences (first: 50) {{
        nodes {{
          number
        }}
      }}
    }}
  }}
}}
"""

def run_query(query): # A simple function to use requests.post to make the API call. Note the json= section.
    request = requests.post('https://api.github.com/graphql', json={'query': query}, headers=headers)
    if request.status_code == 200:
        return request.json()
    else:
        raise Exception("Query failed to run by returning code of {}. {}".format(request.status_code, query))


closing_keywords = [
    'closes', 'close', 'closed', 'fix', 'fixes', 'fixed', 'resolves', 'resolve', 'resolved'
]

# checks all subsequeces of the pr body for closing keywords and extracts the coresponidng issue numbers
def subsequences_matching_regex(input_string, regex):
    matches = []
    for i in range(len(input_string)):
        for j in range(i+1, len(input_string)+1):
            subsequence = input_string[i:j]
            match = re.fullmatch(regex, subsequence)
            if match:
                matches.append(int(match.group(1)))
    return matches

# gets all issues linked to pr either via the closing keywords or the sidebar
def get_linked_issues(result):
    closing_issues = []
    for keyword in closing_keywords:
        closing_issues.extend(subsequences_matching_regex(result['body'].lower(), f'{keyword} #([0-9]+)'))
    for k in result['closingIssuesReferences']['nodes']:
        closing_issues.append(k['number'])
    return list(set(closing_issues))

result = run_query(query)['data']['repository']['pullRequest']
issues = get_linked_issues(result)

def close_issue(issue_number):
    requests.patch(f"https://api.github.com/repos/{owner}/{repo}/issues/{issue_number}", json={"state": "closed"}, headers=headers)

if result['baseRefName'] != "develop":
    print("PR not merged to develop, not closing issues")
elif result['merged']:
    print(f"Closing issues: {issues}")
    for issue in issues:
        close_issue(issue)
        print(f"Closed issue {issue}")
else:
  print("PR not merged, not closing issues")