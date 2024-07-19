#!/bin/python

import subprocess

subprocess.run(["git", "config", "--local", "core.hooksPath", "gitHooks/hooks"])