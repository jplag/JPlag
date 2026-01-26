---
prev: false
next: false
aside: false
---

# Publications

This page lists selected publications that present the foundation, application, and obfuscation resilience of JPlag.

<div v-html="bibJPLAG"></div>

::: warning
This page is currently under construction.
Until it is ready, please visit [the project wiki](https://github.com/jplag/JPlag/wiki).
:::

<script setup>
import { ref } from 'vue';
import * as bibtex from "bibtex";
import { bib } from "./bib.js";

const entries = bibtex.parseBibFile(bib).entries_raw;
const bibJPLAG = ref(filterAndFormatEntries(entries, "jplag"));

function filterAndFormatEntries(entries, tag) {
    const filteredEntries = entries.filter(entry => entry.getFieldAsString("tag") == tag);
    filteredEntries.sort((a, b) => {
        const dateA = new Date(a.getFieldAsString("date"));
        const dateB = new Date(b.getFieldAsString("date"));
        return dateB - dateA; // Sort by date descending
    });
    const formattedEntries = filteredEntries.map(entry => formatBibEntry(entry));

    return `<ul><li>${formattedEntries.join("</li><li>")}</li></ul>`;
}

function formatBibEntry(entry) {
    const title = entry.getFieldAsString("title");
    const author = entry.getFieldAsString("author");
    const date = entry.getFieldAsString("date");

    let url = entry.getFieldAsString("url");
    const doi = entry.getFieldAsString("doi");

    if(!url) {
        if(doi) {
            url = `https://doi.org/${doi}`;
        } else {
            url = "#";
        }
    }

    let venue = "";
    if(entry.type == "inproceedings") {
        venue = `${entry.getFieldAsString("booktitle")}, ${entry.getFieldAsString("publisher")}`
    } else if (entry.type == "article") {
        venue = `${entry.getFieldAsString("journaltitle")}, ${entry.getFieldAsString("publisher")}`
    } else if (entry.type == "misc") {
        venue = entry.getFieldAsString("publisher");
    } else if (entry.type == "thesis") {
        venue = `${entry.getFieldAsString("institution")}, ${entry.getFieldAsString("type")}`
    }

    const formattedAuthorList = formatBibtexAuthors(author);

    let formattedBibEntry = `${formattedAuthorList}, "<a href="${url}">${title}</a>", ${venue}, ${date}`;

    if(doi) {
        formattedBibEntry = formattedBibEntry + `, doi: <a href="https://doi.org/${doi}">${doi}</a>`;
    }

    return `${formattedBibEntry}.`;
}

function formatBibtexAuthors(bibtexAuthors) {
  const authors = bibtexAuthors.split(/\s+and\s+/);

  const formattedAuthors = authors.slice(0, 3).map(author => {
    const [last, first] = author.split(',').map(s => s.trim());
    const firstInitial = first ? first.charAt(0) + '.' : '';
    return `${firstInitial} ${last}`;
  });

  if (authors.length > 3) {
    formattedAuthors.push("et al.");
  }

  return formattedAuthors.join(', ');
}
</script>
