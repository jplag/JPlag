void main() {
    String path = "DoesNotExist.txt";
    Scanner other = null; // This is just here to keep the tokens similar.
    try (Scanner scanner = other = new Scanner(new File(path))) { // same for = other =
        while (scanner.hasNext()) {
            System.out.println(scanner.nextLine());
        }
    } catch (FileNotFoundException exception) {
        exception.printStackTrace();
    } finally {
        if (other != null) { // This as well...
            other.close(); // This as well...
        }
    }
}
