function set(newSize, newThreshold, newDocuments, newThemewords) {
    with(document.data) {
	size.value = newSize;
	thresh.value = newThreshold;
	docs.value = newDocuments;
	theme.value = newThemewords;
    }
}
