<template>
    <li>
        {{ formatAuthors(entry.authors) }}, "<a :href="entry.url">{{ entry.title }}</a>", {{ entry.venue }}, {{ entry.date }}. doi: <a :href="`https://doi.org/${entry.doi}`">{{ entry.doi }}</a>.
    </li>
</template>

<script setup>
defineProps({
  entry: {
    type: Object,
    required: true
  }
});

function formatAuthors(authors) {
  const formattedAuthors = authors.slice(0, 3).map(author => {
    const parts = author.split(' ').map(s => s.trim());
    const first = parts.length > 1 ? parts[0] : undefined;
    const last = parts[parts.length - 1];
    const firstInitial = first ? first.charAt(0) + '.' : '';
    return `${firstInitial} ${last}`;
  });

  if (authors.length > 3) {
    formattedAuthors.push("et al.");
  }

  return formattedAuthors.join(', ');
}
</script>