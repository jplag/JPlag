Sociology
=========

The students of the Sociology course decided to make an experience between them. They agreed that each one would invent a story about himself, tell it to their colleagues who they relate better, and would tell them also all the stories that were told them. Based on this experience, they agreed to consider that a person's group would be the set of people, including herself, whose history became known and who made known their own. They knew that under these conditions, a person's group is necessarily the group of each of the people in their group.

They intended to determine the number of groups with four or more people and the number of people outside these groups. When they were about to start the experiment, some students suggested their replacement for a simulation on a computer to various hypothetical scenarios.


Input
-----

The first line is given the number of scenarios to consider. Following are the descriptions of the scenarios. The first line of each scenario contains the number of students (≥ 4). Next appears one line per student: the first number in this line identifies the student and the following is the number of students with whom they consider to relate better. The remaining numbers in this line identify these students, and there may be none. Consecutive integers are used, starting from 1, to number people.


Output
------

For each scenario, you will have a line with identification of this case and, in the next line, two integers separated by a space. The first is the number of groups with four people or more.bThe second is the number of people outside these groups. Either numbers can be 0.


Example
-------

### Input

```txt
4
4
1 3 2 4 3
4 0
2 2 1 3
3 2 2 1
6
1 2 3 5
2 2 3 4
4 1 2
3 2 2 1
6 1 5
5 2 6 1
8
1 4 6 2 4 5 
3 1 2
2 2 3 4 
4 1 5
6 0
5 3 4 8 7
7 1 5
8 2 5 3
10
1 4 6 2 4 5 
3 2 2 1
9 0
2 2 3 4 
4 2 5 9
6 1 1
5 3 4 8 7
7 1 5
8 1 5
10 1 9
```

### Output

```txt
Caso #1
0 4
Caso #2
1 0
Caso #3
1 2
Caso #4
2 2
```

Credits
-------

DCC/FCUP 2008 - António Porto
