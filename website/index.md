---
# https://vitepress.dev/reference/default-theme-home-page
layout: home
title: JPlag – Detecting Source Code Plagiarism

hero:
  name: "JPlag"
  text: "Detecting Source Code Plagiarism"
  image:
    light: /img/jplag-logo-light.png
    dark: /img/jplag-logo-dark.png
    alt: JPlag – Detecting Source Code Plagiarism
  actions:
    - theme: brand
      text: Download
      link: /download/
    - theme: brand
      text: JPlag Demo
      link: https://demo.jplag.de/
    - theme: alt
      text: Getting Started
      link: /wiki/
    - theme: alt
      text: Take the Survey
      link: https://docs.google.com/forms/d/e/1FAIpQLSckqUlXhIlJ-H2jtu2VmGf_mJt4hcnHXaDlwhpUL3XG1I8UYw/viewform?usp=sf_link

features:
  - icon: 
      dark: img/code-dark.svg
      light: img/code-light.svg
      alt: Large Language Support
    title: Large Language Support
    details: Supports plagiarism detection in all relevant programming langues including Java, C, C++, Python, JavaScript, Typescript, and more.
  - icon: 
      dark: img/performance-dark.svg
      light: img/performance-light.svg
      alt: High Performance
    title: High Performance
    details: Detects academic misconduct even in large datasets of 1000+ submissions on regular consumer hardware in a couple of seconds.
  - icon: 
      dark: img/opensource-dark.svg
      light: img/opensource-light.svg
      alt: Open-Source since 1996
    title: Open-Source since 1996
    details: World-leading open-source plagiarism detection to counter academic misconduct made in Germany since 1996.
  - icon: 
      dark: img/resilient-dark.svg
      light: img/resilient-light.svg
      alt: Obfuscation-Resilience
    title: Obfuscation-Resilience
    details: JPlag is under active research and development to detect and mitigate algorithm and AI-based obfuscation attacks.
---

<div style="margin-bottom:30px;">&nbsp;</div>

# Overview
JPlag is a state-of-the-art source code plagiarism detector. It allows one to check a set of programs for suspicious similarities and thus helps its users tackle plagiarism detection at scale in an ethical way. JPlag compares the structure of the programs by extracting an abstraction layer from their parse trees. Thus, it is resilient to attempts to obfuscate the plagiarism. JPlag offers a powerful graphical interface to analyze its result, allowing to detect outliers and trace similarities between pairs of suspicious programs. JPlag is open-source and can be executed locally, thus complying with the GDPR.

JPlag was created in 1996 and is still actively developed and maintained at Karlsruhe Institute of Technology (KIT). JPlag supports Java, C#, C, C++, Python, Javascript, Typescript, Go, R, Rust, Kotlin, Swift, Scala, and other languages.

<Image src-light="/img/screenshots/OverviewLight.png" src-dark="/img/screenshots/OverviewDark.png" style="margin-bottom:30px;" />
<Image src-light="/img/screenshots/ComparisonLight.png" src-dark="/img/screenshots/ComparisonDark.png" style="margin-bottom:30px;" />
<Image src-light="/img/screenshots/ClusterLight.png" src-dark="/img/screenshots/ClusterDark.png" style="margin-bottom:30px;" />

# Recent Publications

All aspects of JPlag have been [scientifically published](/publications/). For a quick overview of the analysis framework, please see this publication:

<PaperHighlight
  authors="T. Sağlam, S. Hahner, L. Schmid, E. Burger"
  title="Obfuscation-Resilient Software Plagiarism Detection with JPlag"
  reference="IEEE/ACM 46th International Conference on Software Engineering: Companion Proceedings (ICSE-C), 2024"
  url="https://doi.org/10.1145/3639478.3643074"
  doi="10.1145/3639478.3643074" />

<script setup>
import PaperHighlight from './PaperHighlight.vue'
import Image from './Image.vue'
</script>